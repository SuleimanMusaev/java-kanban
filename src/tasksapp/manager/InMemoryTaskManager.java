package tasksapp.manager;

import tasksapp.model.Epic;
import tasksapp.model.Subtask;
import tasksapp.model.Task;
import tasksapp.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager;
    protected int nextId = 1;

    private final Set<Task> prioritizedTasks = new TreeSet<>((t1, t2) -> {
        if (t1.getStartTime() == null && t2.getStartTime() == null) {
            return Integer.compare(t1.getId(), t2.getId());
        } else if (t1.getStartTime() == null) {
            return 1;
        } else if (t2.getStartTime() == null) {
            return -1;
        } else {
            int cmp = t1.getStartTime().compareTo(t2.getStartTime());
            return (cmp != 0) ? cmp : Integer.compare(t1.getId(), t2.getId());
        }
    });

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void checkTimeConflicts(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) return;

        for (Task task : prioritizedTasks) {
            if (task.getId() == newTask.getId()) continue;

            if (task.getStartTime() == null || task.getEndTime() == null) continue;

            boolean intersects = newTask.getStartTime().isBefore(task.getEndTime()) &&
                    newTask.getEndTime().isAfter(task.getStartTime());

            if (intersects) {
                throw new IllegalArgumentException("Задача " + newTask.getName() +
                        " пересекается по времени с задачей " + task.getName());
            }
        }
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private int generateId() {
        return nextId++;
    }

    //Методы Task
    @Override
    public List<Task> getAllTasks() {
        return tasks.values().stream().collect(Collectors.toList());
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
        checkTimeConflicts(task);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        System.out.println("Создана новая задача: " + task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(getTaskById(task.getId()));
            checkTimeConflicts(task);
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
            historyManager.remove(id);
            prioritizedTasks.remove(removed);
            System.out.println("Задача ID = " + id + " удалена.");
        } else {
            System.out.println("Ошибка: задача не найдена");
        }
    }

    // Методы для эпиков (Epic)
    @Override
    public List<Epic> getAllEpics() {
        return epics.values().stream().collect(Collectors.toList());
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
        return subtasks.values().stream().collect(Collectors.toList());
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
            checkTimeConflicts(subtask);

            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);

            Epic epic = epics.get(epicId);
            epic.addSubtaskId(subtask.getId());

            updateEpicStatus(epic);
            updateEpicTimeAndDuration(epic);
            System.out.println("Создана подзадача: " + subtask);
        } else
            System.out.println("Ошибка: эпик ID = " + epicId + " не найден.");
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            checkTimeConflicts(subtask);

            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);

            Epic epic = epics.get(subtask.getEpicId());
            updateEpicStatus(epic);
            updateEpicTimeAndDuration(epic);
            System.out.println("Подзадача обновлена: " + subtask);
        } else
            System.out.println("Ошибка: подзадача не найдена.");
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtaskId(id);
            updateEpicStatus(epic);
            updateEpicTimeAndDuration(epic);
            historyManager.remove(id);
            System.out.println("Подзадача ID = " + id + " удалена.");
        } else
            System.out.println("Ошибка: подзадача не найдена");
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null)
            return Collections.emptyList();

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        List<TaskStatus> statuses = subtaskIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(Subtask::getStatus)
                .collect(Collectors.toList());

        boolean allNew = statuses.stream().allMatch(status -> status == TaskStatus.NEW);
        boolean allDone = statuses.stream().allMatch(status -> status == TaskStatus.DONE);

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

        System.out.println("Статус эпика ID = " + epic.getId() + " обновлен: " + epic.getStatus());
    }

    private void updateEpicTimeAndDuration(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpicId(epic.getId());

        if (epicSubtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        List<Subtask> withStart = epicSubtasks.stream()
                .filter(s -> s.getStartTime() != null)
                .collect(Collectors.toList());

        LocalDateTime start = withStart.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime end = withStart.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration totalDuration = epicSubtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setDuration(totalDuration);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
