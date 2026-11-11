package model.command;

import model.PIRManager;
import model.record.Task;

import java.time.LocalDateTime;

public class CreateTask {
    public void createTask(PIRManager manager, String description, LocalDateTime deadline) {
        Task task = new Task(manager.getCurrentID(), description, deadline);
        manager.generateID();
        manager.add(task);
    }
}
