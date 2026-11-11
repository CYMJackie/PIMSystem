package model.command;

import model.PIRManager;
import model.record.Event;

import java.time.LocalDateTime;

public class CreateEvent {
    public void createEvent(PIRManager manager, String description, LocalDateTime alarm, LocalDateTime starting) {
        Event event = new Event(manager.getCurrentID(), description, alarm, starting);
        manager.generateID();
        manager.add(event);
    }
}
