package tasksapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();

    private LocalDateTime endTime;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        System.out.println("Создан эпик: '" + name + "'");
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.getId()) {
            subtaskIds.add(subtaskId);
            System.out.println("Добавлена подзадача ID=" + subtaskId + " в эпик ID=" + this.getId());
        }
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
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + startTime +
                ", duration=" + (duration != null ? duration.toMinutes() + " мин" : "null") +
                ", endTime=" + endTime +
                '}';
    }
}
