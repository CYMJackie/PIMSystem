package model.command;

import model.PIRManager;
import model.record.Text;

public class CreateText {
    public void createText(PIRManager manager, String description) {
        Text text = new Text(manager.getCurrentID(), description);
        manager.generateID();
        manager.add(text);
    }
}
