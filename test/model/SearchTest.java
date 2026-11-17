package model;

import model.PIRManager;
import model.Record;
import model.command.CreateContact;
import model.command.CreateEvent;
import model.command.CreateTask;
import model.command.CreateText;
import model.command.Search;
import model.record.Contact;
import model.record.Text;
import model.search.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SearchTest {
    private PIRManager manager;
    private Search searchCommand;

    @BeforeEach
    public void setUp()
    {
        manager = new PIRManager();
        searchCommand = new Search();

        new CreateText().createText(manager, "This is a note.");
        new CreateTask().createTask(manager, "This is a task", LocalDateTime.of(2026, 11, 17, 12, 00, 00));
        new CreateTask().createTask(manager, "Project Deadline", LocalDateTime.of(2026, 11, 19, 12, 00, 00));
        new CreateEvent().createEvent(manager, "Team building", LocalDateTime.of(2026, 11, 19, 9, 0), LocalDateTime.of(2026, 11, 20, 10, 0));
        new CreateContact().createContact(manager, "Contact Name", "12345678", "This is an address");
    }

    //FR-16, FP-21: Search by type
    @Test
    public void testSearchByType(){
        List<Token> tokens = new ArrayList<>();
        tokens.add(Token.criterion(Criterion.forType(Type.TEXT)));
        SearchResult result = searchCommand.search(manager, tokens);
        assertEquals(1, result.getRecords().size(), "Should find exactly 1 Text record");
        assertTrue(result.getRecords().get(0) instanceof model.record.Text);
        assertEquals("This is a note.", ((Text) result.getRecords().get(0)).getText(), "The text should npt be modified");
    }

    //FR-17, FR-21: Search by text contain
    @Test
    public void testSearchByTextContain(){
        List<Token> tokens = new ArrayList<>();
        tokens.add(Token.criterion(Criterion.forText(Field.DESCRIPTION, "task")));
        SearchResult result = searchCommand.search(manager, tokens);
        assertEquals(1, result.getRecords().size(), "Should find exactly 1 Text record");
        assertEquals(2, result.getRecords().get(0).getId());
    }

    //FR-18, FR-21: Search by time-based comparisons
    @Test
    public void testSearchByTime(){
        List<Token> tokens = new ArrayList<>();
        LocalDateTime time = LocalDateTime.of(2026, 11, 18, 12, 00, 00);
        tokens.add(Token.criterion(Criterion.forTime(Field.DEADLINE, Operator.LESS_THAN, time)));
        SearchResult result = searchCommand.search(manager, tokens);

        assertEquals(1, result.getRecords().size(), "Should find exactly 1 Text record");
        assertEquals(2, result.getRecords().get(0).getId());
    }

    //FR-19, FR-21: Search using combination
    @Test
    public void testAnd(){
        List<Token> tokens = new ArrayList<>();
        tokens.add(Token.criterion(Criterion.forType(Type.TASK)));
        tokens.add(Token.and());
        tokens.add(Token.criterion(Criterion.forText(Field.DESCRIPTION, "Project")));
        SearchResult result = searchCommand.search(manager, tokens);

        assertEquals(1, result.getRecords().size(), "Should find exactly 1 Text record");
        assertEquals(3, result.getRecords().get(0).getId());
    }
    @Test
    public void testOr(){
        List<Token> tokens = new ArrayList<>();
        tokens.add(Token.criterion(Criterion.forType(Type.TASK)));
        tokens.add(Token.or());
        tokens.add(Token.criterion(Criterion.forType(Type.TEXT)));
        SearchResult result = searchCommand.search(manager, tokens);

        List<Integer> returnedIds = new ArrayList<>();
        for (Record r : result.getRecords()) {
            returnedIds.add(r.getId());
        }
        assertEquals(3, result.getRecords().size(), "Should find exactly 3 Text record");
        assertEquals(List.of(1,2,3), returnedIds);
    }
    @Test
    public void testNot(){
        List<Token> tokens = new ArrayList<>();
        tokens.add(Token.not());
        tokens.add(Token.criterion(Criterion.forType(Type.TEXT)));
        SearchResult result = searchCommand.search(manager, tokens);

        List<Integer> returnedIds = new ArrayList<>();
        for (Record r : result.getRecords()) {
            returnedIds.add(r.getId());
        }
        assertEquals(4, result.getRecords().size(), "Should find exactly 4 Text record");
        assertEquals(List.of(2,3,4,5), returnedIds);
    }

    //FR-20, FR-21: Documented precedence and grouping
    @Test
    public void testGrouping(){
        List<Token> tokens = new ArrayList<>();
        // (Type = Text OR Type = Contact) AND Text contains "a"
        tokens.add(Token.leftParen());
        tokens.add(Token.criterion(Criterion.forType(Type.TEXT)));
        tokens.add(Token.or());
        tokens.add(Token.criterion(Criterion.forType(Type.CONTACT)));
        tokens.add(Token.rightParen());
        tokens.add(Token.and());
        tokens.add(Token.criterion(Criterion.forText(Field.NAME, "a")));
        SearchResult result = searchCommand.search(manager, tokens);

        assertEquals(1, result.getRecords().size(), "Should find exactly 1 Text record");
        assertEquals(5, result.getRecords().get(0).getId());
    }

    //FR-22: Return empty if no record found
    @Test
    public void testEmptySearch(){
        List<Token> tokens = new ArrayList<>();
        tokens.add(Token.criterion(Criterion.forText(Field.NOTE, "Hello")));
        SearchResult result = searchCommand.search(manager, tokens);

        assertTrue(result.getRecords().isEmpty(), "Result should be empty");
    }

    //FR-23: The PIR must not be modified after executing search
    @Test
    public void testSearchImmutability() {
        List<Record> originalRecords = new ArrayList<>(manager.getRecords());

        List<Token> tokens = new ArrayList<>();
        tokens.add(Token.criterion(Criterion.forType(Type.TEXT)));

        searchCommand.search(manager, tokens);

        assertEquals(originalRecords, manager.getRecords(), "The manager's record list must remain identical");
        // Checking order and identity
        for (int i = 0; i < originalRecords.size(); i++) {
            assertEquals(originalRecords.get(i).getId(), manager.getRecords().get(i).getId());
        }
    }
}

