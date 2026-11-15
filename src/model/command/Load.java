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

            String[] parts = splitFields(line);
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

    /**
     * Splits one stored line into its fields, honouring the escaping written by {@link Save}.
     * <p>
     * A plain split("\\|") treats every separator as a boundary, so a note or description containing "|"
     * was cut into extra fields and the whole line shifted. Here a backslash takes the next character
     * literally, which is how "|" and "\" inside a value survive save and load.
     */
    private static String[] splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\\' && i + 1 < line.length()) {
                current.append(line.charAt(++i));
            } else if (c == '|') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}
