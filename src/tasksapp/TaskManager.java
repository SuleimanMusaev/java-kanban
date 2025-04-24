package tasksapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TaskManager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private int nextId;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.nextId = 1;
    }

    private int generateId() {
        return nextId++;
    }

    // методы для обычных задач (Task)
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все обычные задачи удалены.");
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        System.out.println("Создана новая задача: " + task);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            System.out.println("Задача обновлена " + task);
        } else {
            System.out.println("Ошибка: задача с ID = " + task.getId() + " не найдена.");
        }
    }

    public void deleteTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null)
            System.out.println("Задача ID = " + id + " удалена.");
        else System.out.println("Ошибка: задача не найдена");
    }

    // Методы для эпиков (Epic)
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        System.out.println("Все эпики и подзадачи удалены.");
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        System.out.println("Создан новый эпик: " + epic);
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
            System.out.println("Эпик обновлен: " + epic);
        } else
            System.out.println("Ошибка: эпик с ID = " + epic.getId() + " не найден.");
    }

    public void delete(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
            System.out.println("Эпик ID = " + id + " и его подзадачи удалены.");
        } else
            System.out.println("Ошибка: эпик не найден.");
    }

    // Методы для подзадачи (Subtask)
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        }
        System.out.println("Все подзадачи удалены.");
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(epicId);
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
            System.out.println("Создана подзадача: " + subtask);
        } else
            System.out.println("Ошибка: эпик ID = " + epicId + " не найден.");
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpicStatus(epic);
            System.out.println("Подзадача обновлена: " + subtask);
        } else
            System.out.println("Ошибка: подзадача не найдена.");
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(id);
            updateEpicStatus(epic);
            System.out.println("Подзадача ID = " + id + " удалена.") ;
        } else
            System.out.println("Ошибка: подзадача не найдена");
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)
            return Collections.emptyList();

        List<Subtask> result = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()) {
            result.add(subtasks.get(subtaskId));
        }
        return result;
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
        System.out.println("Статус эпика ID = " + epic.getId() + " обновлен: " + epic.getStatus());
    }
}
