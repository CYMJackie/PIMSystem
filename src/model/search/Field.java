package model.search;

import java.time.LocalDateTime;
import java.util.EnumSet;

import model.Record;
import model.record.Contact;
import model.record.Event;
import model.record.Task;
import model.record.Text;

/**
 * One searchable field of a PIR.
 *
 * FR-17 groups the text fields (note, description, name, address, mobile number)
 * under "contains", and FR-18 groups the time values (deadline, starting time,
 * alarm time) under "&lt;", "&gt;" and "=". Each field knows which record types
 * actually have it, which is what makes the type check possible.
 */
public enum Field {
    TYPE("type", Kind.TYPE),
    NOTE("note", Kind.TEXT, Type.TEXT),
    DESCRIPTION("description", Kind.TEXT, Type.TASK, Type.EVENT),
    NAME("name", Kind.TEXT, Type.CONTACT),
    ADDRESS("address", Kind.TEXT, Type.CONTACT),
    MOBILE_NUMBER("mobile number", Kind.TEXT, Type.CONTACT),
    DEADLINE("deadline", Kind.TIME, Type.TASK),
    STARTING_TIME("starting time", Kind.TIME, Type.EVENT),
    ALARM_TIME("alarm time", Kind.TIME, Type.EVENT);

    public enum Kind {
        TYPE,
        TEXT,
        TIME
    }

    private final String label;
    private final Kind kind;
    private final EnumSet<Type> recordTypes;

    Field(String label, Kind kind, Type... types) {
        this.label = label;
        this.kind = kind;
        this.recordTypes = EnumSet.noneOf(Type.class);
        for (Type type : types)
            this.recordTypes.add(type);
    }

    public String getLabel() {
        return label;
    }

    public Kind getKind() {
        return kind;
    }

    public EnumSet<Type> getRecordTypes() {
        return EnumSet.copyOf(recordTypes);
    }

    public boolean appliesTo(Type type) {
        return recordTypes.contains(type);
    }

    /** The text stored in this field of the record, or null when the record has no such field. */
    public String readText(Record record) {
        if (record instanceof Text text && this == NOTE)
            return text.getText();
        if (record instanceof Task task && this == DESCRIPTION)
            return task.getDescription();
        if (record instanceof Event event && this == DESCRIPTION)
            return event.getDescription();
        if (record instanceof Contact contact) {
            if (this == NAME)
                return contact.getName();
            if (this == ADDRESS)
                return contact.getAddress();
            if (this == MOBILE_NUMBER)
                return contact.getPhone();
        }
        return null;
    }

    /** The date-time stored in this field of the record, or null when the record has no such field. */
    public LocalDateTime readTime(Record record) {
        if (record instanceof Task task && this == DEADLINE)
            return task.getDeadline();
        if (record instanceof Event event) {
            if (this == STARTING_TIME)
                return event.getStarting();
            if (this == ALARM_TIME)
                return event.getAlarm();
        }
        return null;
    }
}
