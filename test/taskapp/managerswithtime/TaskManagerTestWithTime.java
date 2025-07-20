package taskapp.managerswithtime;

import tasksapp.manager.TaskManager;
import tasksapp.model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class TaskManagerTestWithTime<T extends TaskManager> {

    protected T manager;

    protected Task createTask(String name, TaskStatus status, LocalDateTime localDateTime, Duration duration) {
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


}
