package model.command;

import model.PIRManager;
import model.Record;
import model.record.*;
import java.time.LocalDateTime;

public class Modify {
    public Record searchByID(PIRManager manager, int id) {
        for (Record record : manager.getRecords()) {
            if (record.getId() == id) {
                return record;
            }
        }
        return null;
    }

    public boolean modifyText(Record record, String newText) {
        if (record == null || newText == null || newText.isBlank()) {
            return false;
        }
        ((Text) record).setText(newText);
        return true;
    }

    public boolean modifyTask(Record record, String newDescription, LocalDateTime newDeadline) {
        if (record == null) return false;
        Task task = (Task) record;
        if (newDescription != null && !newDescription.isBlank()) {
            task.setDescription(newDescription);
        }
        if (newDeadline != null) {
            task.setDeadline(newDeadline);
        }
        return true;
    }

    public boolean modifyEvent(Record record, String newDescription, LocalDateTime newStarting, LocalDateTime newAlarm) {
        if (record == null) return false;
        Event event = (Event) record;
        if (newDescription != null && !newDescription.isBlank()) {
            event.setDescription(newDescription);
        }
        if (newStarting != null) {
            event.setStarting(newStarting);
        }
        if (newAlarm != null) {
            event.setAlarm(newAlarm);
        }
        return true;
    }

    public boolean modifyContact(Record record, String newName, String newPhone, String newAddress) {
        if (record == null) return false;
        Contact contact = (Contact) record;
        if (newName != null && !newName.isBlank()) {
            contact.setName(newName);
        }
        if (newPhone != null && !newPhone.isBlank()) {
            contact.setPhone(newPhone);
        }
        if (newAddress != null && !newAddress.isBlank()) {
            contact.setAddress(newAddress);
        }
        return true;
    }
}
