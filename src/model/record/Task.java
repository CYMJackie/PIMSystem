package model.record;

import model.Record;
import java.time.LocalDateTime;

public class Task extends Record {
    private String description;
    private LocalDateTime deadline;

    public Task(int id, String description, LocalDateTime deadline) {
        super(id);
        this.description = description;
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
