package tasksapp.app;

import tasksapp.manager.FileBackedTaskManager;
import tasksapp.model.Epic;
import tasksapp.model.Subtask;
import tasksapp.model.Task;
import tasksapp.model.TaskStatus;

import java.io.File;
import java.io.IOException;

public class FileReadApp {

    public static void main(String[] args) throws IOException {
        File file = new File("src/tasksapp/data.csv");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Не удалось создать файл: " + e.getMessage());
                return;
            }
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Task1", "Description", TaskStatus.NEW);
        manager.createTask(task1);

        Epic epic1 = new Epic("Epic1", "Epic description");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask1", "Subtask Description", TaskStatus.NEW, epic1.getId());
        manager.createSubtask(subtask1);

        // Используем задачи, чтобы они попали в историю
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());

        // читаем из уже существующего файла
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Загруженные задачи:");
        for (Task task : loaded.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Загруженные эпики:");
        for (Epic epic : loaded.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("Загруженные подзадачи:");
        for (Subtask subtask : loaded.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}