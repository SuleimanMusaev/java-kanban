package tasksapp;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        System.out.println("=== Создаем эпик 'Организовать Мероприятие' ===");
        Epic epic = new Epic("Мероприятие", "Организовать праздник в честь открытия.");
        manager.createEpic(epic);

        System.out.println("\n=== Добавляем подзадачи ===");
        Subtask planSubtask = new Subtask("Написать сценарий", "Начало, речь, конец",
                TaskStatus.NEW, epic.getId());
        Subtask anchorSubtask = new Subtask("Найти ведущего", "Обговорить мероприятие",
                TaskStatus.NEW, epic.getId());
        manager.createSubtask(planSubtask);
        manager.createSubtask(anchorSubtask);

        System.out.println("\nСтатус эпика после создания: " + epic.getStatus());

        System.out.println("\n=== Создаем обычную задачу ===");
        Task mallTask = new Task("Сходить в ТЦ", "Купить для продукты", TaskStatus.NEW);
        manager.createTask(mallTask);

        System.out.println("\n=== Меняем статусы ===");
        planSubtask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(planSubtask);
        System.out.println("Статус эпика после изменения подзадачи: " + epic.getStatus());

        mallTask.setStatus(TaskStatus.DONE);
        manager.updateTask(mallTask);

        System.out.println("\n=== Завершаем подзадачи ===");
        planSubtask.setStatus(TaskStatus.DONE);
        anchorSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(planSubtask);
        manager.updateSubtask(anchorSubtask);
        System.out.println("Статус эпика после завершения всех подзадач: " + epic.getStatus());

        manager.getEpicById(epic.getId());
        manager.getSubtaskById(planSubtask.getId());
        manager.getTaskById(mallTask.getId());
        
        System.out.println("\n=== Удаляем задачу 'Сходить в ТЦ' ===");
        manager.deleteTask(mallTask.getId());
        System.out.println("Остались задачи: " + manager.getAllTasks().size());
    }
}
