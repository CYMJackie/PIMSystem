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
                lines.add("text|" + text.getId() + "|" + text.getText());
            } else if (record instanceof model.record.Task task) {
                lines.add("task|" + task.getId() + "|" + task.getDescription() + "|" + task.getDeadline().format(formatter));
            } else if (record instanceof model.record.Event event) {
                lines.add("event|" + event.getId() + "|" + event.getDescription() + "|" + event.getAlarm().format(formatter) + "|" +event.getStarting().format(formatter));
            } else if (record instanceof model.record.Contact contact) {
                lines.add("contact|" + contact.getId() + "|" + contact.getName() + "|" + contact.getPhone() + "|" + contact.getAddress());
            }
        }

        Files.write(Paths.get(fileName), lines);
    }
}
