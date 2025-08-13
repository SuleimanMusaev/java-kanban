package tasksapp.model;

import java.time.format.DateTimeFormatter;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
        System.out.println("Создана подзадача: '" + name + "' (ID эпика: " + epicId + ")");
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                ", startTime=" + startTime.format(DateTimeFormatter.ofPattern("dd, MM, yyyy. HH:mm")) +
                ", duration=" + (duration != null ? duration.toMinutes() + " мин" : "null") +
                '}';
    }
}
