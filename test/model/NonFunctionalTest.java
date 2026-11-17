package model;

import model.PIRManager;
import model.Record;
import model.command.*;
import model.record.*;
import view.command.SaveView;
import view.command.LoadView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class NonFunctionalTest {

    private PIRManager manager;

    @BeforeEach
    void setUp() {
        manager = new PIRManager();
        new CreateText().createText(manager, "This is a note.");
        new CreateTask().createTask(manager, "This is a task", LocalDateTime.of(2026, 11, 17, 12, 00, 00));
    }

    //NFR-01: containing up to 10000 records without crashing
    @Test
    public void testCapacity(@TempDir Path tempDir) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 10000; i++) {
            sb.append("text|").append(i).append("|Note number ").append(i).append("\n");
        }

        File largeFile = tempDir.resolve("large_dataset.pim").toFile();
        Files.writeString(largeFile.toPath(), sb.toString());

        Load load = new Load();

        assertDoesNotThrow(() -> load.exe(manager, largeFile.getAbsolutePath()));

        assertEquals(10000, manager.getRecords().size(), "Should load all 10,000 records");
        assertEquals(10001, manager.getCurrentID(), "ID should update to max+1");
    }

    //NFR-02: Failed Load does not modify the current collection
    @Test
    public void testReliabilityOnFailedLoad(@TempDir Path tempDir) throws Exception {
        int initialSize = manager.getRecords().size();

        Load load = new Load();
        assertThrows(Exception.class, () -> load.exe(manager, "non_existent_file.pim"));
        assertEquals(initialSize, manager.getRecords().size(), "Collection size should not change on failure");
        assertEquals("This is a note.", ((Text) manager.getRecords().get(0)).getText(), "Existing records must remain intact");
    }

    //NFR-04: Model can be tested independently without View/Controller
    @Test
    public void testTestability(){
        Modify modify = new Modify();
        Record record = manager.getRecords().get(0);
        assertTrue(modify.modifyText(record, "Testability"));
    }

    //NFR-06: Usability
    @Test
    public void testUsability(){
        LoadView loadView = new LoadView();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            Load load = new Load();
            try {
                load.exe(manager, "invalid_file_xyz.pim");
            } catch (Exception e) {
                loadView.failLoad(e.getMessage());
            }

        } finally {
            System.setOut(new PrintStream(new java.io.FileOutputStream(FileDescriptor.out)));
        }

        String output = outContent.toString();

        assertTrue(output.contains("Loading failed!"), "Must display a clear failure header");
        assertTrue(output.contains("Error Message:"), "Must display the specific reason/exception");
        assertFalse(output.contains("successfully"), "Must not contain misleading success text");
    }

}
