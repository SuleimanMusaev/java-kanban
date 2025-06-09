package test.java.taskapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasksapp.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class HistoryManagerTest {

    private TaskManager manager;

    @BeforeEach
    void setup() {
        manager = Managers.getDefault();
    }

    @Test
    void duplicateTasksNotStoredInHistory() {
        Task task = new Task("Task", "desc", TaskStatus.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId()); //повторный просмотр

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size()); //Повтор не добавляется?
    }

    @Test
    void deletedTaskShouldBeRemovedFromHistory() {
        Task task = new Task("To delete", "desc", TaskStatus.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId());
        manager.deleteTask(task.getId());

        List<Task> history = manager.getHistory();
        assertFalse(history.contains(task));
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
    void externalChangeToTaskShouldAffectManagerStorage() {
        Task task = new Task("Original", "desc", TaskStatus.NEW);
        manager.createTask(task);
        Task retrieved = manager.getTaskById(task.getId());

        retrieved.setName("Changed");

        assertEquals("Changed", manager.getTaskById(task.getId()).getName());
    }

    @Test
    void deletingEpicAlsoDeletesSubtasks() {
        Epic epic = new Epic("Epic", "desc");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "desc", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask2", "desc", TaskStatus.NEW, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteEpic(epic.getId());

        assertNull(manager.getSubtaskById(subtask1.getId()));
        assertNull(manager.getSubtaskById(subtask2.getId()));
    }
}
