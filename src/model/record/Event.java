package model.record;

import model.Record;
import java.time.LocalDateTime;

public class Event extends Record {
    private String description;
    private LocalDateTime starting, alarm;

    public Event(int id, String description, LocalDateTime alarm, LocalDateTime starting) {
        String type = "Event";
        super(id, type);
        this.description = description;
        this.alarm = alarm;
        this.starting = starting;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getAlarm() {
        return alarm;
    }

    public void setAlarm(LocalDateTime alarm) {
        this.alarm = alarm;
    }

    public LocalDateTime getStarting() {
        return starting;
    }

    public void setStarting(LocalDateTime starting) {
        this.starting = starting;
    }

}
