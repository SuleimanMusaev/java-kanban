package taskapp.manager;

import org.junit.jupiter.api.*;
import tasksapp.manager.FileBackedTaskManager;
import tasksapp.model.Epic;
import tasksapp.model.Subtask;
import tasksapp.model.Task;
import tasksapp.model.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void shouldHandleEmptyFile() throws IOException {
        Files.write(tempFile.toPath(), List.of("id,type,name,status,description,epic", ""));

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(manager.getAllTasks().isEmpty(), "Tasks should be empty");
        assertTrue(manager.getAllEpics().isEmpty(), "Epics should be empty");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Subtasks should be empty");
        assertTrue(manager.getHistory().isEmpty(), "History should be empty");
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task1", "Description1", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Description2", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic = new Epic("Epic", "Description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description",
                TaskStatus.DONE, epic.getId());
        manager.createSubtask(subtask);

        manager.getTaskById(task1.getId());
        manager.getSubtaskById(subtask.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loaded.getAllTasks().size(), "Tasks count mismatch");
        assertEquals(1, loaded.getAllEpics().size(), "Epics count mismatch");
        assertEquals(1, loaded.getAllSubtasks().size(), "Subtasks count mismatch");

        List<Task> history = loaded.getHistory();
        assertEquals(2, history.size(), "History should contain 2 entries");
        assertEquals(task1.getId(), history.get(0).getId(), "First task in history should match");
        assertEquals(subtask.getId(), history.get(1).getId(), "Second task in history should match");
    }

    @Test
    void shouldPreserveTaskDataAfterReload() {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task original = new Task("Original", "Test", TaskStatus.IN_PROGRESS);
        manager.createTask(original);

        FileBackedTaskManager reloaded = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = reloaded.getAllTasks();
        assertEquals(1, tasks.size(), "Should be one task after reload");

        Task loaded = tasks.get(0);
        assertEquals(original.getName(), loaded.getName());
        assertEquals(original.getDescription(), loaded.getDescription());
        assertEquals(original.getStatus(), loaded.getStatus());
        assertEquals(original.getId(), loaded.getId());
    }
}
