package taskapp.managerswithtime;

import org.junit.jupiter.api.Test;
import tasksapp.manager.TaskManager;
import tasksapp.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTestWithTime<T extends TaskManager> {

    protected T manager;

    protected Task createTask(String name, TaskStatus status,
                              LocalDateTime localDateTime, Duration duration) {
        Task task = new Task(name, "desc", status);
        task.setStartTime(localDateTime);
        task.setDuration(duration);
        manager.createTask(task);
        return task;
    }

    protected Epic createEpic(String name) {
        Epic epic = new Epic(name, "desc");
        manager.createEpic(epic);
        return epic;
    }

    protected Subtask createSubtask(Epic epic, TaskStatus status,
                                    LocalDateTime localDateTime, Duration duration) {
        Subtask subtask = new Subtask("subtask",
                "desc", status, epic.getId());
        subtask.setStartTime(localDateTime);
        subtask.setDuration(duration);
        manager.createSubtask(subtask);
        return subtask;
    }

    @Test
    void shouldCreateAndGetTask() {
        Task task = createTask("Task", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        Task fetched = manager.getTaskById(task.getId());

        assertNotNull(fetched);
        assertEquals(task.getId(), fetched.getId());
        assertEquals(task.getName(), fetched.getName());
    }

    @Test
    void shouldCreateAndGetEpic() {
        Epic epic = createEpic("Epic");
        Epic fetched = manager.getEpicById(epic.getId());

        assertNotNull(fetched);
        assertEquals(epic.getId(), fetched.getId());
        assertEquals(epic.getName(), fetched.getName());
    }

    @Test
    void shouldCreateSubtaskAndLinkToEpic() {
        Epic epic = createEpic("Epic");
        Subtask subtask = createSubtask(epic, TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(15));

        Subtask fetched = manager.getSubtaskById(subtask.getId());
        assertNotNull(fetched);
        assertEquals(epic.getId(), fetched.getEpicId());

        List<Subtask> subtasks = manager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, subtasks.size());
        assertEquals(subtask.getId(), subtasks.get(0).getId());
    }

    @Test
    void shouldDeleteTask() {
        Task task = createTask("Task to delete", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        manager.deleteTask(task.getId());

        assertNull(manager.getTaskById(task.getId()));
    }

    @Test
    void shouldAddTasksToHistory() {
        Task task = createTask("History of Task", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(20));
        manager.getTaskById(task.getId());

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task.getId(), history.get(0).getId());
    }
}
