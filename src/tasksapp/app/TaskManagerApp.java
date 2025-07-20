package tasksapp.app;

import tasksapp.manager.Managers;
import tasksapp.manager.TaskManager;
import tasksapp.model.Epic;
import tasksapp.model.Subtask;
import tasksapp.model.Task;
import tasksapp.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskManagerApp {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        System.out.println("=== Создаем эпик 'Организовать Мероприятие' ===");
        Epic epic1 = new Epic("Мероприятие", "Организовать праздник в честь открытия.");
        manager.createEpic(epic1);

        System.out.println("=== Создаем эпик 'Переезд' ===");
        Epic epic2 = new Epic("Переезд", "Спланировать переезд.");
        manager.createEpic(epic2);

        System.out.println("\n=== Добавляем подзадачи ===");
        Subtask planSubtask1 = new Subtask("Написать сценарий.", "Начало, речь, конец.",
                TaskStatus.NEW, epic1.getId());
        planSubtask1.setStartTime(LocalDateTime.of(2025, 7, 20, 10, 0));
        planSubtask1.setDuration(Duration.ofMinutes(90));
        manager.createSubtask(planSubtask1);

        Subtask anchorSubtask = new Subtask("Найти ведущего.", "Обговорить мероприятие.",
                TaskStatus.NEW, epic1.getId());
        anchorSubtask.setStartTime(LocalDateTime.of(2025, 7, 20, 12, 0));
        anchorSubtask.setDuration(Duration.ofMinutes(60));
        manager.createSubtask(anchorSubtask);

        Subtask planSubtask2 = new Subtask("Найти время для переезда.", "Сложить вещи.",
                TaskStatus.NEW, epic2.getId());
        planSubtask2.setStartTime(LocalDateTime.of(2025, 7, 21, 9, 0));
        planSubtask2.setDuration(Duration.ofMinutes(120));
        manager.createSubtask(planSubtask2);

        Subtask trucker = new Subtask("Позвонить грузчику.", "Договорить по цене.",
                TaskStatus.NEW, epic2.getId());
        trucker.setStartTime(LocalDateTime.of(2025, 7, 21, 12, 0));
        trucker.setDuration(Duration.ofMinutes(45));
        manager.createSubtask(trucker);

        System.out.println("\nСтатус эпика после создания: " + epic1.getStatus());
        System.out.println("\nСтатус эпика после создания: " + epic2.getStatus());

        System.out.println("\n=== Создаем обычную задачу ===");
        Task mallTask = new Task("Сходить в ТЦ", "Купить продукты", TaskStatus.NEW);
        mallTask.setStartTime(LocalDateTime.of(2025, 7, 19, 17, 0));
        mallTask.setDuration(Duration.ofMinutes(60));
        manager.createTask(mallTask);

        Task callTask = new Task("Позвонить бабушке в субботу.", "Поздравить с днем рождения.", TaskStatus.NEW);
        callTask.setStartTime(LocalDateTime.of(2025, 7, 18, 20, 0));
        callTask.setDuration(Duration.ofMinutes(30));
        manager.createTask(callTask);

        //просмотр задач
        System.out.println("\n=== Просматриваем задачи (формируем историю) ===");
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(planSubtask1.getId());
        manager.getSubtaskById(anchorSubtask.getId());

        manager.getEpicById(epic2.getId());
        manager.getSubtaskById(planSubtask2.getId());
        manager.getSubtaskById(trucker.getId());

        manager.getTaskById(mallTask.getId());
        manager.getTaskById(callTask.getId());

        System.out.println("История просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("\n=== Меняем статусы ===");
        planSubtask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(planSubtask1);
        System.out.println("Статус эпика после изменения подзадачи: " + manager.getEpicById(epic1.getId()).getStatus());

        mallTask.setStatus(TaskStatus.DONE);
        manager.updateTask(mallTask);

        System.out.println("\n=== Завершаем подзадачи ===");
        planSubtask1.setStatus(TaskStatus.DONE);
        anchorSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(planSubtask1);
        manager.updateSubtask(anchorSubtask);
        System.out.println("Статус эпика после завершения всех подзадач: " + epic1.getStatus());

        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(planSubtask1.getId());
        manager.getTaskById(mallTask.getId());

        System.out.println("\n=== Удаляем задачу 'Сходить в ТЦ' ===");
        manager.deleteTask(mallTask.getId());
        System.out.println("Остались задачи: " + manager.getAllTasks().size());
    }
}
