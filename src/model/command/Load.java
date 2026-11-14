package model.command;

import model.PIRManager;
import model.Record;
import model.record.Contact;
import model.record.Event;
import model.record.Task;
import model.record.Text;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Load {
    public void exe(PIRManager manager, String fileName) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(fileName));

        List<Record> newRecords = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

        for (String line : lines) {
            if (line.isBlank()) continue;

            String[] parts = line.split("\\|", -1);
            String type = parts[0];

            int id = Integer.parseInt(parts[1]);

            switch (type) {
                case "text" -> {
                    String desc = parts[2];
                    Text text = new Text(id, desc);
                    newRecords.add(text);
                }
                case "task" -> {
                    String desc = parts[2];
                    LocalDateTime ddl = LocalDateTime.parse(parts[3], formatter);
                    Task task = new Task(id, desc, ddl);
                    newRecords.add(task);
                }
                case "event" -> {
                    String desc = parts[2];
                    LocalDateTime alarm = LocalDateTime.parse(parts[3], formatter);
                    LocalDateTime start = LocalDateTime.parse(parts[4], formatter);
                    Event event = new Event(id, desc, alarm, start);
                    newRecords.add(event);
                }
                case "contact" -> {
                    String name = parts[2];
                    String phone = parts[3];
                    String address = parts[4];
                    Contact contact = new Contact(id, name, phone, address);
                    newRecords.add(contact);
                }
            }
        }
        manager.setRecords(newRecords);

        int maxID = 0;
        for (Record r : newRecords) {
            if (r.getId() > maxID) {
                maxID = r.getId();
            }
        }

        manager.setCurrentID(maxID + 1);
    }
}
