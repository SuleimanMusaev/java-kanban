package tasksapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private TaskManager manager;

    @BeforeEach
    void setup() {
        manager = Managers.getDefault();
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Task 1", "desc", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "desc", TaskStatus.NEW);
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Subtask sub1 = new Subtask("Sub 1", "desc", TaskStatus.NEW, 1);
        Subtask sub2 = new Subtask("Sub 2", "desc", TaskStatus.NEW, 1);
        sub1.setId(3);
        sub2.setId(3);

        assertEquals(sub1, sub2);
    }

    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic("Epic 1", "desc");
        Epic epic2 = new Epic("Epic 2", "desc");
        epic1.setId(5);
        epic2.setId(5);

        assertEquals(epic1, epic2);
    }

    @Test
    void epicCannotContainItselfAsSubtask() {
        Epic epic = new Epic("Epic", "desc");
        epic.setId(1);
        epic.addSubtaskId(1);

        assertFalse(epic.getSubtaskIds().contains(epic.getId()));
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
        manager.createTask(task);
        Task fetched = manager.getTaskById(task.getId());

        assertEquals(task, fetched);
    }

    @Test
    void manuallyAssignedIdDoesNotConflictWithGeneratedId() {
        Task manualTask = new Task("Manual", "desc", TaskStatus.NEW);
        manualTask.setId(100);
        manager.createTask(manualTask);

        Task autoTask = new Task("Auto", "desc", TaskStatus.NEW);
        manager.createTask(autoTask);

        assertNotEquals(manualTask.getId(), autoTask.getId());
    }

    @Test
    void taskIsUnchangedAfterBeingAddedToManager() {
        Task task = new Task("Task", "desc", TaskStatus.NEW);
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
        manager.createTask(task);
        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();

        assertTrue(history.contains(task));
        assertEquals(task, history.get(history.size() - 1));
    }
}