package model.command;

import java.time.format.DateTimeFormatter;
import java.util.List;

import model.PIRManager;
import model.Record;
import model.record.*;

public class Print {
    public Record searchByID(PIRManager manager, int id) {
        return manager.getRecords().stream().filter(target -> target.getId() == id).findFirst().orElse(null);
    }

    public void print(List<Record> records, DateTimeFormatter formatter) {
        for (Record record : records) {
            System.out.println("--------------------------------------------------------------------------------------------");
            System.out.println("PIR ID: " + record.getId());
            switch (record) {
                case Text text -> {
                    System.out.println("Type: Text");
                    System.out.println("Note: " + text.getText());
                }
                case Task task -> {
                    System.out.println("Type: Task");
                    System.out.println("Description: " + task.getDescription());
                    System.out.println("Deadline: " + task.getDeadline().format(formatter));
                }
                case Event event -> {
                    System.out.println("Type: Event");
                    System.out.println("Description: " + event.getDescription());
                    System.out.println("Starting Time: " + event.getStarting().format(formatter));
                    System.out.println("Alarm: " + event.getAlarm().format(formatter));
                }
                case Contact contact -> {
                    System.out.println("Type: Contact");
                    System.out.println("Name: " + contact.getName());
                    System.out.println("Address: " + contact.getAddress());
                    System.out.println("Mobile number: " + contact.getPhone());
                }
                default -> {}
            }
        }
        System.out.println("============================================================================================");
    }
}
