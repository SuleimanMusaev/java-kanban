package taskapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasksapp.*;

import static org.junit.jupiter.api.Assertions.*;

public class TaskLogicTest {

    private TaskManager manager;

    @BeforeEach
    void setup() {
        manager = Managers.getDefault();
    }

    @Test
    void epicShouldNotContainDeletedSubtaskId() {
        Epic epic = new Epic("Epic", "desc");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "desc", TaskStatus.NEW, epic.getId());
        manager.createTask(subtask);

        manager.deleteSubtask(subtask.getId());

        Epic updated = manager.getEpicById(epic.getId());
        assertFalse(updated.getSubtaskIds().contains(subtask.getId()));
    }

    @Test
    void epicCannotContainItselfAsSubtask() {
        Epic epic = new Epic("Epic", "desc");
        epic.setId(1);
        epic.addSubtaskId(1);

        assertFalse(epic.getSubtaskIds().contains(epic.getId()));
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
}
