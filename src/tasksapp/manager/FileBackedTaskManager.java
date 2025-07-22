package tasksapp.manager;

import tasksapp.model.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super(Managers.getDefaultHistory());
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,startTime,duration,epicId\n");
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }

            writer.newLine();
            writer.write(historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.isEmpty()) {
                throw new RuntimeException("Файл пустой");
            }
            int splitIndex = (int) IntStream.range(0, lines.size())
                    .filter(i -> lines.get(i).isBlank())
                    .findFirst()
                    .orElse(lines.size());

            List<String> taskLines = lines.subList(1, splitIndex);
            taskLines.stream()
                    .map(FileBackedTaskManager::fromString)
                    .forEach(task -> {
                        manager.updateNextId(task.getId());
                        switch (task.getType()) {
                            case TASK -> manager.tasks.put(task.getId(), task);
                            case EPIC -> manager.epics.put(task.getId(), (Epic) task);
                            case SUBTASK -> {
                                Subtask subtask = (Subtask) task;
                                manager.subtasks.put(task.getId(), subtask);
                                manager.epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
                            }
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
        return manager;
    }

    private static Task fromString(String line) {
        String[] fields = line.split(",");

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        LocalDateTime startTime = !"null".equals(fields[5]) ? LocalDateTime.parse(fields[5]) : null;
        Duration duration = !"null".equals(fields[6]) ? Duration.ofMinutes(Long.parseLong(fields[6])) : null;

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setId(id);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                //
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[7]);
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                return subtask;
            default:
                throw new IllegalStateException("Неизвестный тип задачи: " + type);
        }
    }

    private static List<Integer> historyFromString(String value) {
        if (value == null || value.isEmpty()) return List.of();
        return Arrays.stream(value.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private void updateNextId(int id) {
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    private String historyToString(HistoryManager manager) {
        return manager.getHistory().stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(","));
    }

    private String toString(Task task) {
        String epicId = "";
        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        String start = task.getStartTime() != null ?
                task.getStartTime().toString() : "null";
        String duration = task.getDuration() != null ?
                String.valueOf(task.getDuration().toMinutes()) : " null";

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                start,
                duration,
                epicId
        );
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            save();
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            save();
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            save();
        }
        return subtask;
    }
}
