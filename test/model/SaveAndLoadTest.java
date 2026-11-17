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
import view.command.LoadView;
import view.command.SaveView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SaveAndLoadTest {
    private PIRManager manager;

    @BeforeEach
    void setUp() {
        manager = new PIRManager();

        new CreateText().createText(manager, "This is a note.");
        new CreateTask().createTask(manager, "This is a task", LocalDateTime.of(2026, 11, 17, 12, 00, 00));
        new CreateTask().createTask(manager, "Project Deadline", LocalDateTime.of(2026, 11, 19, 12, 00, 00));
        new CreateEvent().createEvent(manager, "Team building", LocalDateTime.of(2026, 11, 19, 9, 0), LocalDateTime.of(2026, 11, 20, 10, 0));
        new CreateContact().createContact(manager, "Contact Name", "12345678", "This is an address");
    }

    //FR33, FR-34, FR-35, FR-36, FR-38: Save and Load preserve all data exactly (.pim extension)
    @Test
    public void testSaveAndLoadRoundTrip(@TempDir Path tempDir) throws Exception {
        //FR-34: ensure the PIM data file have a .pim filename
        File saveFile = tempDir.resolve("test.pim").toFile();
        //FR-33: Save
        Save save = new Save();
        save.exe(manager, saveFile.getAbsolutePath());

        assertTrue(saveFile.exists(), "File should be created");
        //FR-35: Load
        PIRManager newManager = new PIRManager();
        Load load = new Load();
        load.exe(newManager, saveFile.getAbsolutePath());
        //FR-36, FR-37
        List<Record> loadedRecords = newManager.getRecords();
        assertEquals(5, loadedRecords.size(), "All 5 records should be loaded");
        assertTrue(loadedRecords.get(0) instanceof model.record.Text);
        assertEquals("This is a note.", ((model.record.Text) loadedRecords.get(0)).getText());
        assertTrue(loadedRecords.get(4) instanceof model.record.Contact);
        assertEquals("Contact Name", ((model.record.Contact) loadedRecords.get(4)).getName());
        assertEquals(6, newManager.getCurrentID(), "Current ID should be max ID + 1");
    }

    //FR-38: .pim file is fully functional
    @Test
    public void testFileFunctionality(@TempDir Path tempDir) throws Exception {
        File saveFile = tempDir.resolve("functional.pim").toFile();
        new Save().exe(manager, saveFile.getAbsolutePath());

        PIRManager newManager = new PIRManager();
        new Load().exe(newManager, saveFile.getAbsolutePath());

        CreateText createText = new CreateText();
        createText.createText(newManager, "New Text after loading");
        List<Record> loadedRecords = newManager.getRecords();
        assertEquals(7, newManager.getCurrentID(), "The new current ID should be 7)");
        assertEquals("New Text after loading", ((Text)loadedRecords.get(5)).getText());

        Record recordToModify = newManager.getRecords().get(0);
        Modify modify = new Modify();
        assertTrue(modify.modifyText(recordToModify, "Modified Note"));
        assertEquals("Modified Note", ((model.record.Text) recordToModify).getText());

        Print print = new Print();
        assertNotNull(print.searchByID(newManager, 1), "Loaded record should be printable");

        File secondSaveFile = tempDir.resolve("functional_2.pim").toFile();
        assertDoesNotThrow(() -> new Save().exe(newManager, secondSaveFile.getAbsolutePath()));
    }

    //FR-39, FR-41: Malformed file is rejected and active PIRs remain uncorrupted"
    @Test
    public void testLoadMalformedFile(@TempDir Path tempDir) throws Exception {
        File corruptFile = tempDir.resolve("corrupt.pim").toFile();
        Files.writeString(corruptFile.toPath(), "task|ABC|some_description|2026-11-17 12:00:00\n");
        int initialSize = manager.getRecords().size();

        Load load = new Load();
        assertThrows(Exception.class, () -> {
            load.exe(manager, corruptFile.getAbsolutePath());
        }, "Loading a malformed file should throw an exception");
        //FR-41
        assertEquals(initialSize, manager.getRecords().size(), "Active PIRs must remain unchanged");
        assertEquals("This is a note.", ((model.record.Text) manager.getRecords().get(0)).getText(), "Data must be intact");
    }

    //FR-40, FR41: Failure and Succeed Save and Load Handling, inform user
    @Test
    public void testFailedSave(@TempDir Path tempDir) {
        File invalidFile = tempDir.resolve("invalid.pim").toFile();
        invalidFile.mkdirs();

        Save save = new Save();
        SaveView view = new SaveView();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            save.exe(manager, invalidFile.getAbsolutePath());
            view.successSave();
        } catch (Exception e) {
            view.failSave(e.getMessage());
        } finally {
            System.setOut(new PrintStream(new java.io.FileOutputStream(FileDescriptor.out)));
        }

        String output = outContent.toString();
        assertFalse(output.contains("successfully saved"));
        assertTrue(output.contains("Saving file failed!"));
    }

    @Test
    public void testSuccessMessages(@TempDir Path tempDir) throws Exception {
        File saveFile = tempDir.resolve("success.pim").toFile();
        Save save = new Save();
        Load load = new Load();

        SaveView saveView = new SaveView();
        LoadView loadView = new LoadView();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            save.exe(manager, saveFile.getAbsolutePath());
            saveView.successSave();

            load.exe(manager, saveFile.getAbsolutePath());
            loadView.successLoad();

        } finally {
            System.setOut(new PrintStream(new java.io.FileOutputStream(FileDescriptor.out)));
        }

        String output = outContent.toString();
        assertTrue(output.contains("Records are successfully saved"), "Should inform user of successful save");
        assertTrue(output.contains("Records are loaded successfully"), "Should inform user of successful load");
    }


}
