package tasksapp.manager;

import tasksapp.model.Epic;
import tasksapp.model.Subtask;
import tasksapp.model.Task;

import java.util.List;

public interface TaskManager {
    // методы для обычных задач (Task)
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void createTask(Task task);

    void updateTask(Task task);

    void deleteTask(int id);

    // Методы для эпиков (Epic)
    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpic(int id);

    // Методы для подзадачи (Subtask)
    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int id);

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
