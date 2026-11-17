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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ModifyTest {
    private PIRManager manager;

    @BeforeEach
    void setUp() {
        manager = new PIRManager();
    }

    //FR-09, FR-10, FR-11: Modify Task without changing unique ID
    @Test
    public void testModifyTask(){
        LocalDateTime oldDeadline = LocalDateTime.of(2026, 11, 17, 12,00,00);
        new CreateTask().createTask(manager, "Old task", oldDeadline);
        Record record = manager.getRecords().get(0);
        int originalId = record.getId();
        Modify modify = new Modify();

        boolean result = modify.modifyTask(record, "New Task", null);

        assertTrue(result);
        assertEquals("New Task", ((Task) record).getDescription());
        assertEquals(oldDeadline, ((Task) record).getDeadline(), "FR-12: Old deadline should be retained");
        assertEquals(originalId, record.getId(), "FR-11: ID must not change");
    }

    //FR-13, FR-14: Modification validation and rollback
    @Test
    public void testModifyText(){
        new CreateText().createText(manager, "Note");
        Record record = manager.getRecords().get(0);
        Modify modify = new Modify();

        boolean result = modify.modifyText(record, null);
        assertFalse(result, "Modify text should not be modified");
        assertEquals("Note", ((Text) record).getText(), "Original text must be preserved");
    }

    //FR-15: Modified PIR is fully functional (Searchable, Printable, Savable)
    @Test
    public void testModifyIsFullyFunctional(@TempDir Path tempDir) throws Exception{
        LocalDateTime oldDeadline = LocalDateTime.of(2026, 11, 17, 12, 00, 00);
        new CreateTask().createTask(manager, "Old Task", oldDeadline);
        Record record = manager.getRecords().get(0);
        Modify modify = new Modify();

        LocalDateTime newDeadline = LocalDateTime.of(2026, 11, 20, 18, 00, 00);
        modify.modifyTask(record, "New Task", newDeadline);

        Print print = new Print();
        Record foundRecord = print.searchByID(manager, record.getId());
        assertNotNull(foundRecord, "Modified PIR should be retrievable.");
        assertEquals("New Task", ((Task) foundRecord).getDescription(), "Search should return updated data.");

        assertDoesNotThrow(() -> {
            print.print(List.of(foundRecord), java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
        }, "Printing a modified PIR should not throw an exception.");

        File saveFile = tempDir.resolve("modified_save.pim").toFile();
        Save save = new Save();
        assertDoesNotThrow(() -> save.exe(manager, saveFile.getAbsolutePath()), "Saving modified PIR should succeed.");

        List<String> fileLines = java.nio.file.Files.readAllLines(saveFile.toPath());
        assertTrue(fileLines.get(0).contains("New Task"), "Saved file must contain the modified data.");

        Delete delete = new Delete();
        boolean isDeleted = delete.delete(manager, record.getId());
        assertTrue(isDeleted, "Modified PIR should be deletable.");
        assertTrue(manager.getRecords().isEmpty(), "Collection should be empty after deleting the modified PIR.");
    }
}
