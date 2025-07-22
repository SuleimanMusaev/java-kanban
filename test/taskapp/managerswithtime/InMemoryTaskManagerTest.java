package taskapp.managerswithtime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasksapp.manager.InMemoryHistoryManager;
import tasksapp.manager.InMemoryTaskManager;
import tasksapp.model.Epic;
import tasksapp.model.Task;
import tasksapp.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTestWithTime<InMemoryTaskManager> {
    @BeforeEach
    void init() {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    public void shouldReturnEmptyTaskListInitially() {
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldStoreAndRetrieveTask() {
        Task task = createTask("task1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void epicShouldBeNewWhenAllSubtasksAreNew() {
        Epic epic = createEpic("Epic");
        createSubtask(epic, TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        createSubtask(epic, TaskStatus.NEW, LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
        assertEquals(TaskStatus.NEW, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void epicShouldBeDoneWhenAllSubtasksAreDone() {
        Epic epic = createEpic("Epic");
        createSubtask(epic, TaskStatus.DONE, LocalDateTime.now(), Duration.ofMinutes(30));
        createSubtask(epic, TaskStatus.DONE, LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
        assertEquals(TaskStatus.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void epicShouldBeInProgressWhenSubtasksAreNewAndDone() {
        Epic epic = createEpic("Epic");
        createSubtask(epic, TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        createSubtask(epic, TaskStatus.DONE, LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void epicShouldBeInProgressWhenSubtasksAreInProgress() {
        Epic epic = createEpic("Epic");
        createSubtask(epic, TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(30));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void shouldNotAllowOverlappingTasks() {
        Task task1 = new Task("Task1", "desc", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 7, 20, 20, 20));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task("Task2", "desc", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 7, 20, 20, 40));
        task2.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class,
                () -> manager.createTask(task2),
                "Должно быть исключение: задачи пересекаются по времени"
        );
    }

    //Проверка истории
    @Test
    public void historyShouldBeEmptyInitially() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void historyShouldNotDuplicateTasks() {
        Task task = createTask("Task1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());
        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    public void shouldRemoveFromHistoryCorrectly() {
        Task t1 = createTask("Task1", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task t2 = createTask("Task2", TaskStatus.NEW, LocalDateTime.now().plusDays(1), Duration.ofMinutes(30));
        Task t3 = createTask("Task3", TaskStatus.NEW, LocalDateTime.now().plusDays(2), Duration.ofMinutes(30));

        manager.getTaskById(t1.getId());
        manager.getTaskById(t2.getId());
        manager.getTaskById(t3.getId());

        manager.deleteTask(t1.getId());
        manager.deleteTask(t2.getId());
        manager.deleteTask(t3.getId());

        assertTrue(manager.getHistory().isEmpty());
    }
}
