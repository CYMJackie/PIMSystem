package view.command;

import java.time.LocalDateTime;
import java.util.List;

import model.Record;
import model.search.Criterion;
import model.search.Field;
import model.search.SearchResult;

/**
 * Every message the search function shows: the menus that build the query, the
 * results, and the warning for a condition that can never be true.
 */
public class SearchView {

    private static final String SEPARATOR =
            "============================================================================================";

    /** Words shown on each side of a match in a long text field. */
    private static final int CONTEXT_WORDS = 10;

    /** One choice of the main search menu. The controller decides which of them to offer. */
    public enum Option {
        FIELD("Field"),
        NOT("! (negate the next condition)"),
        OPEN_GROUP("( (open a group)"),
        CLOSE_GROUP(") (close the group)"),
        LOGICAL("Logical connector (&&, ||)"),
        REMOVE_LAST("Remove last input"),
        RUN("Run search"),
        CANCEL("Cancel search");

        private final String label;

        Option(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    /** Repeats the query built so far, above every step that adds to it. */
    private void printQuery(String query) {
        System.out.println(SEPARATOR);
        System.out.println("Search query: " + (query.isEmpty() ? "(empty)" : query));
        System.out.println(SEPARATOR);
    }

    public void menu(String query, List<Option> options) {
        printQuery(query);
        System.out.println("Build the search condition: ");
        for (int i = 0; i < options.size(); i++)
            System.out.println((i + 1) + ". " + options.get(i).getLabel());
        System.out.println(SEPARATOR);
        System.out.print("Press number to choose: ");
    }

    public void fieldMenu() {
        System.out.println(SEPARATOR);
        System.out.println("Which field do you want to search? ");
        Field[] fields = Field.values();
        for (int i = 0; i < fields.length; i++)
            System.out.println((i + 1) + ". " + fields[i].getLabel());
        System.out.println((fields.length + 1) + ". Return to last menu");
        System.out.println(SEPARATOR);
        System.out.print("Press number to choose: ");
    }

    public void typeMenu() {
        System.out.println(SEPARATOR);
        System.out.println("Which type of PIR do you want? ");
        System.out.println("1. Text");
        System.out.println("2. Task");
        System.out.println("3. Event");
        System.out.println("4. Contact");
        System.out.println("5. Return to last menu");
        System.out.println(SEPARATOR);
        System.out.print("Press number to choose: ");
    }

    public void operatorMenu(String query, Field field) {
        printQuery(query);
        System.out.println("Which comparison do you want to use for the " + field.getLabel() + "? ");
        System.out.println("1. <");
        System.out.println("2. >");
        System.out.println("3. =");
        System.out.println("4. Return to last menu");
        System.out.println(SEPARATOR);
        System.out.print("Press number to choose: ");
    }

    public void logicalMenu() {
        System.out.println(SEPARATOR);
        System.out.println("How do you want to combine the conditions? ");
        System.out.println("1. && (and)");
        System.out.println("2. || (or)");
        System.out.println("3. Return to last menu");
        System.out.println(SEPARATOR);
        System.out.print("Press number to choose: ");
    }

    public void valuePrompt(String query, Field field) {
        printQuery(query);
        if (field.getKind() == Field.Kind.TIME)
            System.out.println("Input the " + field.getLabel() + "(yyyy-MM-dd HH:mm:ss) to compare with: ");
        else
            System.out.println("Input the value the " + field.getLabel() + " should contain: ");
        System.out.println("Press enter to confirm, or press enter without typing anything to go back.");
        System.out.println(SEPARATOR);
    }

    public void invalidInputMessage() {
        System.out.println("The input is invalid. Please input again!");
        System.out.print("Press number to choose: ");
    }

    public void timeFormatMessage(Field field) {
        System.out.println("The format of the " + field.getLabel()
                + " should be yyyy-MM-dd HH:mm:ss (for example 2026-12-15 09:30:00). Please input again.");
    }

    public void cancelledMessage() {
        System.out.println(SEPARATOR);
        System.out.println("Search cancelled.");
        System.out.println(SEPARATOR);
    }

    public void invalidConditionMessage() {
        System.out.println(SEPARATOR);
        System.out.println("The search condition is not complete, so nothing was searched.");
        System.out.println(SEPARATOR);
    }

    public void printResults(SearchResult result) {
        System.out.println(SEPARATOR);
        System.out.println("Search query: " + result.getQuery());
        System.out.println(SEPARATOR);

        if (result.hasWarning())
            System.out.println(result.getWarning());

        List<Record> records = result.getRecords();
        if (records.isEmpty()) {
            // FR-22: an empty result must be reported, not silently ignored.
            System.out.println("No records found.");
            System.out.println(SEPARATOR);
            return;
        }

        System.out.println(records.size() == 1 ? "1 record found." : records.size() + " records found.");
        for (Record record : records)
            printRecord(record, result);

        System.out.println(SEPARATOR);
    }

    private void printRecord(Record record, SearchResult result) {
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("ID: " + record.getId() + "  |  Type: " + record.getType());

        for (Field field : result.getFields()) {
            if (!field.appliesTo(model.search.Type.of(record)))
                continue;
            if (field.getKind() == Field.Kind.TEXT) {
                String value = field.readText(record);
                System.out.println("  " + capitalised(field.getLabel()) + ": " + snippet(value, result.getTerms(field)));
            } else if (field.getKind() == Field.Kind.TIME) {
                LocalDateTime value = field.readTime(record);
                System.out.println("  " + capitalised(field.getLabel()) + ": "
                        + (value == null ? "(none)" : value.format(Criterion.TIME_FORMAT)));
            }
        }
    }

    /**
     * Shows the words around the match instead of the whole text, so a long note does
     * not flood the screen. A short value is shown as it is, with the match marked.
     */
    private String snippet(String value, List<String> terms) {
        if (value == null || value.isBlank())
            return "(empty)";

        String text = value.trim();
        String[] words = text.split("\\s+");

        int matchedWord = -1;
        String matchedTerm = null;
        for (String term : terms) {
            if (term == null || term.isEmpty())
                continue;
            int position = text.toLowerCase().indexOf(term.toLowerCase());
            if (position < 0)
                continue;
            int word = wordIndexAt(text, position);
            if (matchedWord < 0 || word < matchedWord) {
                matchedWord = word;
                matchedTerm = term;
            }
        }

        if (matchedWord < 0) {
            if (words.length <= CONTEXT_WORDS * 2)
                return text;
            return String.join(" ", java.util.Arrays.copyOfRange(words, 0, CONTEXT_WORDS * 2)) + " ...";
        }

        int start = Math.max(0, matchedWord - CONTEXT_WORDS);
        int end = Math.min(words.length, matchedWord + CONTEXT_WORDS + 1);
        String window = String.join(" ", java.util.Arrays.copyOfRange(words, start, end));
        if (start > 0)
            window = "... " + window;
        if (end < words.length)
            window = window + " ...";

        return mark(window, matchedTerm);
    }

    /** Index of the word that contains the given character position. */
    private int wordIndexAt(String text, int position) {
        int words = 0;
        boolean inWord = false;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                inWord = false;
            } else {
                if (!inWord && i < position)
                    words++;
                inWord = true;
            }
        }
        return Math.max(0, words - 1);
    }

    /** Wraps the matched words in brackets, so they stand out from the context. */
    private String mark(String text, String term) {
        if (term == null)
            return text;
        int position = text.toLowerCase().indexOf(term.toLowerCase());
        if (position < 0)
            return text;
        return text.substring(0, position)
                + "[" + text.substring(position, position + term.length()) + "]"
                + text.substring(position + term.length());
    }

    private String capitalised(String label) {
        if (label.isEmpty())
            return label;
        return Character.toUpperCase(label.charAt(0)) + label.substring(1);
    }
}
