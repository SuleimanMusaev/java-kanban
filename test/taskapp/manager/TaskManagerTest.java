package taskapp.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasksapp.manager.Managers;
import tasksapp.manager.TaskManager;
import tasksapp.model.Subtask;
import tasksapp.model.Task;
import tasksapp.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void setup() {
        manager = Managers.getDefault();
    }

    @Test
    void subtaskCannotReferenceItselfAsEpic() {
        Subtask subtask = new Subtask("Sub", "desc", TaskStatus.IN_PROGRESS, 2);
        subtask.setId(1);

        assertNotEquals(subtask.getId(), subtask.getEpicId());
    }

    @Test
    void managersAreAlwaysInitialized() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void inMemoryTaskManagerCanAddAndFindTasksById() {
        Task task = new Task("Task", "desc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        manager.createTask(task);
        Task fetched = manager.getTaskById(task.getId());

        assertEquals(task, fetched);
    }

    @Test
    void manuallyAssignedIdDoesNotConflictWithGeneratedId() {
        Task manualTask = new Task("Manual", "desc", TaskStatus.NEW);
        manualTask.setStartTime(LocalDateTime.now());
        manualTask.setDuration(Duration.ofMinutes(30));
        manualTask.setId(100);
        manager.createTask(manualTask);

        Task autoTask = new Task("Auto", "desc", TaskStatus.NEW);
        autoTask.setStartTime(LocalDateTime.now().plusMinutes(30));
        autoTask.setDuration(Duration.ofMinutes(60));
        manager.createTask(autoTask);

        assertNotEquals(manualTask.getId(), autoTask.getId());
    }

    @Test
    void taskIsUnchangedAfterBeingAddedToManager() {
        Task task = new Task("Task", "desc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        manager.createTask(task);
        Task retrieved = manager.getTaskById(task.getId());

        assertEquals(task.getName(), retrieved.getName());
        assertEquals(task.getDescription(), retrieved.getDescription());
        assertEquals(task.getStatus(), retrieved.getStatus());
        assertEquals(task.getId(), retrieved.getId());
    }

    @Test
    void historyManagerPreservesPreviousVersionsOfTask() {
        Task task = new Task("History Task", "desc", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(30));
        manager.createTask(task);
        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();

        assertTrue(history.contains(task));
        assertEquals(task, history.get(history.size() - 1));
    }
}