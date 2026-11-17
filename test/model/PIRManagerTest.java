package model;

import model.PIRManager;
import model.Record;
import model.command.*;
import model.record.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PIRManagerTest {

    private PIRManager manager;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss")
            .withResolverStyle(ResolverStyle.STRICT);

    @BeforeEach
    void setUp() {
        manager = new PIRManager();
    }

    //FR-DR-01, FR-DR-02, FR-01: every PIR have 1 PIR type and unique ID
    @Test
    public void testTypeAndUniqueID(){
        new CreateText().createText(manager, "Note");
        new CreateTask().createTask(manager, "Task", LocalDateTime.now());
        new CreateEvent().createEvent(manager, "Event", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        new CreateContact().createContact(manager, "Name", "12345678", "Addr");

        List<Record> records = manager.getRecords();

        assertEquals(4, records.size());
        assertTrue(records.get(0) instanceof Text);
        assertTrue(records.get(1) instanceof Task);
        assertTrue(records.get(2) instanceof Event);
        assertTrue(records.get(3) instanceof Contact);

        assertEquals(1, records.get(0).getId());
        assertEquals(2, records.get(1).getId());
        assertEquals(3, records.get(2).getId());
        assertEquals(4, records.get(3).getId());
    }

    //FR-DR-03, FR-02: Text should contain a note field
    @Test
    public void testCreateText(){
        CreateText createText = new CreateText();
        createText.createText(manager, "Note");
        Record record = manager.getRecords().get(0);

        assertEquals("Text", ((Text) record).getText());
    }

    //FR-DR-04, FR-03: Task should contain a description and deadline
    @Test
    public void testCreateTask(){
        CreateTask createTask = new CreateTask();
        LocalDateTime deadline = LocalDateTime.of(2026, 11, 17, 12,00,00);
        createTask.createTask(manager, "Task", deadline);
        Record record = manager.getRecords().get(0);

        assertEquals("Task", ((Task)record).getDescription());
        assertEquals(deadline, ((Task)record).getDeadline());
    }

    //FR-DR-05, FR-04: Event should contain description, starting time and alarm
    @Test
    public void testCreateEvent(){
        CreateEvent createEvent = new CreateEvent();
        LocalDateTime start = LocalDateTime.of(2026, 11, 17, 12,00,00);
        LocalDateTime alarm = LocalDateTime.of(2026, 11, 17, 12,00,00);
        Record record = manager.getRecords().get(0);

        assertEquals("Event", ((Event)record).getDescription());
        assertEquals(start, ((Event)record).getStarting());
        assertEquals(alarm, ((Event)record).getAlarm());
    }

    //FR-DR-06, FR-DR-08, FR-05: Contact should contain name, phone and address(optional)
    @Test
    public void testCreateContact(){
        CreateContact createContact = new CreateContact();
        createContact.createContact(manager, "Name1", "12345678", "Addr");
        createContact.createContact(manager, "Name2", "12345678", null);
        List<Record> records = manager.getRecords();

        assertEquals("Name1", ((Contact)manager.getRecords().get(0)).getName());
        assertEquals("12345678", ((Contact) manager.getRecords().get(0)).getPhone());
        assertEquals("Addr", ((Contact) manager.getRecords().get(0)).getAddress());
        assertEquals("Addr", ((Contact) manager.getRecords().get(1)).getAddress());
    }

    //FR-06, FR-07: Creation rejection
    @Test
    public void testEmptyManagerOnStart() {
        assertTrue(manager.getRecords().isEmpty());
        assertEquals(1, manager.getCurrentID());
    }

    //FR-DR-07: Deadline, starting time, and alarm time shall represent valid points of time.
    @Test
    public void testInvalidPointsOfTime() {
        // Test invalid date (February 30th does not exist)
        String invalidDate = "2026-02-30 12:00:00";
        assertThrows(DateTimeParseException.class, () -> {
            LocalDateTime.parse(invalidDate, formatter);
        }, "System must reject impossible dates like Feb 30");

        // Test invalid time (25:00 hours does not exist)
        String invalidTime = "2026-11-20 25:00:00";
        assertThrows(DateTimeParseException.class, () -> {
            LocalDateTime.parse(invalidTime, formatter);
        }, "System must reject impossible times like 25:00");

        // Test completely malformed strings
        String malformed = "Next Tuesday at noon";
        assertThrows(DateTimeParseException.class, () -> {
            LocalDateTime.parse(malformed, formatter);
        }, "System must reject non-numeric, unstructured time strings");
    }
    @Test
    public void testValidPointsOfTime() {
        String validTime = "2026-11-17 12:00:00";
        LocalDateTime parsed = LocalDateTime.parse(validTime, formatter);

        assertNotNull(parsed);
        assertEquals(2026, parsed.getYear());
        assertEquals(11, parsed.getMonthValue());
        assertEquals(17, parsed.getDayOfMonth());
        assertEquals(12, parsed.getHour());
        assertEquals(00, parsed.getMinute());
        assertEquals(00, parsed.getSecond());
    }

    //FR-DR-09: The system shall use a consistent date and time format for comparison operations.
    @Test
    public void testSamePointsOfTimeFormat() {
        LocalDateTime Deadline1 = LocalDateTime.parse("2026-11-17 12:00:00", formatter);
        LocalDateTime Deadline2 = LocalDateTime.parse("2026-11-19 12:00:00", formatter);
        LocalDateTime Deadline3 =  LocalDateTime.parse("2026-11-17 12:00:00", formatter);
        assertTrue(Deadline1.isBefore(Deadline2), "Format must support 'before' (<) comparison");
        assertTrue(Deadline2.isAfter(Deadline1), "Format must support 'after' (>) comparison");
        assertFalse(Deadline1.isEqual(Deadline2), "Format must distinguish different times");
        assertTrue(Deadline1.isEqual(Deadline3), "Format must support 'equal' (=) comparison");

    }
}

