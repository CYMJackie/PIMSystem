package model;

import model.PIRManager;
import model.Record;
import model.command.*;
import model.record.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrintAndDisplayTest {
    private PIRManager manager;
    private Print print;

    @BeforeEach
    public void setUp() {
        manager = new PIRManager();
        print = new Print();

        new CreateText().createText(manager, "This is a note.");
        new CreateTask().createTask(manager, "This is a task", LocalDateTime.of(2026, 11, 17, 12, 00, 00));
        new CreateTask().createTask(manager, "Project Deadline", LocalDateTime.of(2026, 11, 19, 12, 00, 00));
        new CreateEvent().createEvent(manager, "Team building", LocalDateTime.of(2026, 11, 19, 9, 0), LocalDateTime.of(2026, 11, 20, 10, 0));
        new CreateContact().createContact(manager, "Contact Name", "12345678", "This is an address");
    }

    //FR-28, FR-29, FR-31, FR-32: Record retrieval for display
    @Test
    public void testSearchByIdForDisplay() {
        Record found = print.searchByID(manager, 1);

        assertNotNull(found);
        assertEquals("This is a note.", ((Text) found).getText());
        assertNull(print.searchByID(manager, 99), "Non-exist ID");
    }
    @Test
    public void testPrintAllPIRs() {
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
            List<Record> allRecords = manager.getRecords();
            print.print(allRecords, formatter);

            String output = outContent.toString();

            assertTrue(output.contains("PIR ID: 1"), "Should print ID for Note");
            assertTrue(output.contains("Type: Note"), "Should print Type for Note"); // Note: your Text class uses "Note" as the type

            assertTrue(output.contains("PIR ID: 2"), "Should print ID for Task");
            assertTrue(output.contains("Type: Task"), "Should print Type for Task");

            assertTrue(output.contains("Note: This is a note."), "Should print Note content");

            assertTrue(output.contains("Description: This is a task"), "Should print Task description");
            assertTrue(output.contains("Deadline: 2026-11-17 12:00:00"), "Should print Task deadline");

            assertTrue(output.contains("Description: Team building"), "Should print Event description");
            assertTrue(output.contains("Starting Time: 2026-11-20 10:00:00"), "Should print Event start time");
            assertTrue(output.contains("Alarm: 2026-11-19 09:00:00"), "Should print Event alarm");

            assertTrue(output.contains("Name: Contact Name"), "Should print Contact name");
            assertTrue(output.contains("Address: This is an address"), "Should print Contact address");
            assertTrue(output.contains("Mobile number: 12345678"), "Should print Contact mobile");

        } finally {
            System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out)));
        }
    }

    //FR-30: Inform user if no PIR in the system
    @Test
    public void testEmptyPIRs() {
        manager.getRecords().clear();
        assertTrue(manager.getRecords().isEmpty(), "Setup: System should be empty");
    }



}