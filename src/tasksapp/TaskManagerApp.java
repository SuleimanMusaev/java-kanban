package tasksapp;

public class TaskManagerApp {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        System.out.println("=== Создаем эпик 'Организовать Мероприятие' ===");
        Epic epic1 = new Epic("Мероприятие", "Организовать праздник в честь открытия.");
        Epic epic2 = new Epic("Переезд", "Спланировать переезд.");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        System.out.println("\n=== Добавляем подзадачи ===");
        Subtask planSubtask1 = new Subtask("Написать сценарий.", "Начало, речь, конец.",
                TaskStatus.NEW, epic1.getId());
        Subtask anchorSubtask = new Subtask("Найти ведущего.", "Обговорить мероприятие.",
                TaskStatus.NEW, epic1.getId());
        manager.createSubtask(planSubtask1);
        manager.createSubtask(anchorSubtask);

        Subtask planSubtask2 = new Subtask("Найти время для переезда.", "Сложить вещи.",
                TaskStatus.NEW, epic1.getId());
        Subtask trucker = new Subtask("Позвонить грузчику.", "Договорить по цене.",
                TaskStatus.NEW, epic1.getId());
        manager.createSubtask(planSubtask2);
        manager.createSubtask(trucker);

        System.out.println("\nСтатус эпика после создания: " + epic1.getStatus());
        System.out.println("\nСтатус эпика после создания: " + epic2.getStatus());

        System.out.println("\n=== Создаем обычную задачу ===");
        Task mallTask = new Task("Сходить в ТЦ", "Купить продукты", TaskStatus.NEW);
        manager.createTask(mallTask);

        Task callTask = new Task("Позвонить бабушке в субботу.", "Поздравить с днем рождения.", TaskStatus.NEW);
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
