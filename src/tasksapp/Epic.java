package tasksapp;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtaskIds = new ArrayList<>();
        System.out.println("Создан эпик: '" + name + "'");
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
        System.out.println("Добавлена подзадача ID=" + subtaskId + " в эпик ID=" + this.getId());

    }

    public void removeSubtaskId(int subtaskId) {
        System.out.println("Подзадача ID=" + subtaskId + " удалена из эпика ID=" + this.getId());
        subtaskIds.remove((Integer) subtaskId);
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
        System.out.println("Все подзадачи эпика ID=" + this.getId() + " удалены");
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
