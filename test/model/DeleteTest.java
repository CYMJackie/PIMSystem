package model;

import model.PIRManager;
import model.Record;
import model.command.*;
import model.record.Contact;
import model.record.Text;
import model.search.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteTest {
    private PIRManager manager;
    private Delete delete;
    @BeforeEach
    void setUp() {
        manager = new PIRManager();
        delete = new Delete();

        new CreateText().createText(manager, "This is a note.");
        new CreateTask().createTask(manager, "This is a task", LocalDateTime.of(2026, 11, 17, 12, 00, 00));
        new CreateTask().createTask(manager, "Project Deadline", LocalDateTime.of(2026, 11, 19, 12, 00, 00));
        new CreateEvent().createEvent(manager, "Team building", LocalDateTime.of(2026, 11, 19, 9, 0), LocalDateTime.of(2026, 11, 20, 10, 0));
        new CreateContact().createContact(manager, "Contact Name", "12345678", "This is an address");
    }

    //FR-25: keep all PIR unchanged and report error when deletion fails
    @Test
    public void testDeletionFails(){
        assertFalse(delete.delete(manager, 99), "Deleting non-existent ID returns false");
        assertEquals(5, manager.getRecords().size(), "Collection unchanged");
    }

    //FR-26: PIR is removed when deletion success, and not appear in search, modifying, printing, store in .pim files
    @Test
    public void testDeletionSuccess(@TempDir Path tempDir) throws Exception{
        int deletedID = 1;
        assertEquals(5, manager.getRecords().size());
        assertTrue(delete.delete(manager, deletedID), "Deleting non-existent ID returns true");
        assertEquals(4, manager.getRecords().size());

        Modify modify = new Modify();
        Record modifyResult = modify.searchByID(manager, deletedID);
        assertNull(modifyResult, "Deleted PIR must not be found for modification");

        Print print = new Print();
        Record printResult = print.searchByID(manager, deletedID);
        assertNull(printResult, "Deleted PIR must not be found for printing");

        Search search = new Search();
        List<Token> tokens = new ArrayList<>();
        tokens.add(Token.criterion(Criterion.forType(Type.TEXT)));
        SearchResult searchResult = search.search(manager, tokens);
        assertTrue(searchResult.getRecords().isEmpty(), "Deleted PIR must not appear in search results");

        File saveFile = tempDir.resolve("deleted_test.pim").toFile();
        Save save = new Save();
        save.exe(manager, saveFile.getAbsolutePath());

        List<String> fileLines = java.nio.file.Files.readAllLines(saveFile.toPath());
        assertEquals(4, fileLines.size(), "FR-27: Saved file should only contain 1 line (the kept record)");
        assertTrue(fileLines.get(0).contains("This is a task"), "The kept record should be in the file");
        assertFalse(fileLines.get(0).contains("Secret Note to Delete"), "The deleted record must NOT be in the file");

    }


}
