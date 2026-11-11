package model.search;

import model.Record;
import model.record.Contact;
import model.record.Event;
import model.record.Task;
import model.record.Text;

/**
 * The four kinds of PIR that can be searched.
 * The name is kept separate from Record.getType() so the search code never
 * depends on the string stored inside a record.
 */
public enum Type {
    TEXT("Text"),
    TASK("Task"),
    EVENT("Event"),
    CONTACT("Contact");

    private final String label;

    Type(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Type of(Record record) {
        if (record instanceof Text)
            return TEXT;
        if (record instanceof Task)
            return TASK;
        if (record instanceof Event)
            return EVENT;
        if (record instanceof Contact)
            return CONTACT;
        throw new IllegalArgumentException("Unknown record type: " + record.getType());
    }
}
