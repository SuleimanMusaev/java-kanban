package tasksapp.manager;

import tasksapp.model.Epic;
import tasksapp.model.Subtask;
import tasksapp.model.Task;
import tasksapp.model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager;
    protected int nextId = 1;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int generateId() {
        return nextId++;
    }

    //Методы Task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все обычные задачи удалены.");
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        System.out.println("Создана новая задача: " + task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            System.out.println("Задача обновлена " + task);
        } else {
            System.out.println("Ошибка: задача с ID = " + task.getId() + " не найдена.");
        }
    }

    @Override
    public void deleteTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            System.out.println("Задача ID = " + id + " удалена.");
            historyManager.remove(id);
        } else {
            System.out.println("Ошибка: задача не найдена");
        }
    }

    // Методы для эпиков (Epic)
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        System.out.println("Все эпики и подзадачи удалены.");
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        System.out.println("Создан новый эпик: " + epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic);
            System.out.println("Эпик обновлен: " + epic);
        } else
            System.out.println("Ошибка: эпик с ID = " + epic.getId() + " не найден.");
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
            System.out.println("Эпик ID = " + id + " и его подзадачи удалены.");
        } else
            System.out.println("Ошибка: эпик не найден.");
    }

    // Методы для подзадачи (Subtask)
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic);
        }
        System.out.println("Все подзадачи удалены.");
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
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

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpicStatus(epic);
            System.out.println("Подзадача обновлена: " + subtask);
        } else
            System.out.println("Ошибка: подзадача не найдена.");
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(id);
            updateEpicStatus(epic);
            System.out.println("Подзадача ID = " + id + " удалена.");
            historyManager.remove(id);
        } else
            System.out.println("Ошибка: подзадача не найдена");
    }

    @Override
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

    public void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
