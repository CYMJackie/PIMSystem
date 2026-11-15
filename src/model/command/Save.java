package model.command;

import model.PIRManager;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
Saving format:
Text: text|id|desc
Task: task|id|desc|ddl
Event: event|id|desc|alarm|start
Contact: contact|id|name|phoneNo|address
 */

public class Save {
    public void exe(PIRManager manager, String fileName) throws Exception {
        List<String> lines = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

        for (model.Record record : manager.getRecords()) {
            if (record instanceof model.record.Text text) {
                lines.add("text|" + text.getId() + "|" + escape(text.getText()));
            } else if (record instanceof model.record.Task task) {
                lines.add("task|" + task.getId() + "|" + escape(task.getDescription()) + "|" + task.getDeadline().format(formatter));
            } else if (record instanceof model.record.Event event) {
                lines.add("event|" + event.getId() + "|" + escape(event.getDescription()) + "|" + event.getAlarm().format(formatter) + "|" +event.getStarting().format(formatter));
            } else if (record instanceof model.record.Contact contact) {
                lines.add("contact|" + contact.getId() + "|" + escape(contact.getName()) + "|" + escape(contact.getPhone()) + "|" + escape(contact.getAddress()));
            }
        }

        Files.write(Paths.get(fileName), lines);
    }

    /**
     * Makes a text value safe to put between two field separators.
     * <p>
     * Without this, a note or description containing "|" was written as-is and the loader split it into
     * extra fields, so the record came back with its text cut in half and the line no longer matched the
     * format. The backslash is escaped first, so the escape character itself also survives the round trip.
     */
    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }
}
