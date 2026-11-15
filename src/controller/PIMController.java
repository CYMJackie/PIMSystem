package controller;

import model.PIRManager;
import model.command.*;
import model.record.*;
import model.Record;
import model.search.*;
import view.PIMView;
import view.command.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PIMController {
    private final PIMView view;
    private final PIRManager manager;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    public PIMController(PIRManager manager, PIMView view) {
        this.manager = manager;
        this.view = view;
    }

    public void initialize() {
        int function;
        view.initialMessage();

        //One scanner for the whole session.
        Scanner scanner = new Scanner(System.in);

        loop: //label of while loop
        while (true) {
            //input request
            view.inputMessage();

            //Error handling
            if (!scanner.hasNext()) {
                //End of input: leave instead of reporting an error forever
                view.exitMessage();
                break;
            }

            if (scanner.hasNextInt()) {
                function = scanner.nextInt();
                scanner.nextLine();
            }
            else {
                view.function_ErrorMessage();
                scanner.nextLine(); //Discard the invalid token, or it would be read again forever
                continue;
            }

            switch (function) {
                //Create record
                case 1 -> create(scanner);
                //Modify record
                case 2 -> modify(scanner);
                //Delete record
                case 3 -> delete(scanner);
                //Search record
                case 4 -> search(scanner);
                //Print record
                case 5 -> print(scanner);
                //Store record
                case 6 -> save(scanner);
                //Load record
                case 7 -> load(scanner);
                //Exit
                case 8 -> {
                    view.exitMessage();
                    break loop;
                }
                default -> view.function_ErrorMessage();
            }
        }
    }

    private void create(Scanner scanner) {
        CreateView createView = new CreateView();
        int type = 0;

        createView.inputMessage();

        //Error handling of whether "type" is integer
        if (scanner.hasNextInt()) {
            type = scanner.nextInt();
            readLine(scanner);
        }
        else if (readLine(scanner) == null) return;

        switch (type) {
            //Create Text
            case 1 -> createText(scanner);
            //Create Task
            case 2 -> createTask(scanner);
            //Create Event
            case 3 -> createEvent(scanner);
            //Create Contact
            case 4 -> createContact(scanner);
            default -> view.function_ErrorMessage();
        }
    }

    private void createText(Scanner scanner) {
        CreateTextView textView = new CreateTextView();
        CreateText create = new CreateText();

        textView.inputMessage();
        String description = readLine(scanner);

        while (description != null && description.isBlank()) {
            textView.emptyInput();
            description = readLine(scanner);
        }

        if (description == null) return;

        create.createText(manager, description);
        textView.success();
    }

    private void createTask(Scanner scanner) {
        CreateTaskView taskView = new CreateTaskView();
        CreateTask create = new CreateTask();

        taskView.inputDescriptionMessage();
        String description = readLine(scanner);
        while (description != null && description.isBlank()) {
            taskView.emptyDescription();
            description = readLine(scanner);
        }
        if (description == null) return;

        LocalDateTime parsedDeadline = null;
        taskView.inputDeadlineMessage();
        while (parsedDeadline == null) {
            String deadline = readLine(scanner);
            if (deadline == null) return;
            parsedDeadline = parseDateTime(deadline, formatter, taskView::emptyDeadline, taskView::deadlineFormatError);
        }


        create.createTask(manager, description, parsedDeadline);
        taskView.success();
    }


    private void createEvent(Scanner scanner) {
        CreateEventView eventView = new CreateEventView();
        CreateEvent create = new CreateEvent();

        eventView.inputDescMessage();
        String description = readLine(scanner);

        while (description != null && description.isBlank()) {
            eventView.emptyDesc();
            description = readLine(scanner);
        }
        if (description == null) return;

        LocalDateTime starting = null;
        eventView.inputStartMessage();
        while (starting == null) {
            String startStr = readLine(scanner);
            if (startStr == null) return;
            starting=parseDateTime(startStr, formatter, eventView::emptyStart, eventView::startFormatError);
        }

        LocalDateTime alarm = null;
        eventView.inputAlarmMessage();
        while (alarm == null) {
            String alarmStr = readLine(scanner);
            if (alarmStr == null) return;
            alarm=parseDateTime(alarmStr, formatter, eventView::emptyAlarm, eventView::alarmFormatError);
            if (alarm != null && alarm.isAfter(starting)) {
                eventView.invalidAlarm();
                alarm = null;
            }
        }

        create.createEvent(manager, description, alarm, starting);
        eventView.success();
    }

    private LocalDateTime parseDateTime(String s, DateTimeFormatter formatter, Runnable emptyError, Runnable formatError) {
        if (s.isBlank()) {
            emptyError.run();
            return null;
        }
        try {
            return LocalDateTime.parse(s, formatter);
        } catch (Exception e) {
            formatError.run();
            return null;
        }

    }

    private void createContact(Scanner scanner) {
        CreateContactView contactView = new CreateContactView();
        CreateContact create = new CreateContact();

        contactView.inputNameMessage();
        String name = readLine(scanner);

        while (name != null && name.isBlank()) {
            contactView.emptyName();
            name = readLine(scanner);
        }
        if (name == null) return;

        contactView.inputPhoneMessage();
        String phone = readLine(scanner);

        while (phone != null && phone.isBlank()) {
            contactView.emptyNumber();
            phone = readLine(scanner);
        }
        if (phone == null) return;

        contactView.inputAddressMessage();
        String address = readLine(scanner);
        if (address == null) return;

        create.createContact(manager, name, phone, address);
        contactView.success();
    }

    private void save(Scanner scanner) {
        SaveView view = new SaveView();
        Save save = new Save();

        if (manager.getRecords().isEmpty()) {
            view.emptyRecord();
            return;
        }

        view.inputFileName();
        String fileName = readLine(scanner);
        if (fileName == null) return;
        fileName = fileName.replaceAll("\\s+", "");

        while (fileName.isBlank()) {
            view.emptyFileName();
            fileName = readLine(scanner);
            if (fileName == null) return;
            fileName = fileName.replaceAll("\\s+", "");
        }

        if (!fileName.endsWith(".pim")) {
            fileName = fileName + ".pim";
        }

        try {
            save.exe(manager, fileName);
            view.successSave();
        } catch (Exception e){
            view.failSave(e.getMessage());
        }
    }

    private void load(Scanner scanner) {
        LoadView view = new LoadView();
        Load load = new Load();

        view.inputFileName();
        String fileName = readLine(scanner);
        if (fileName == null) return;
        fileName = fileName.replaceAll("\\s+", "");

        while (fileName.isBlank()) {
            view.emptyFileName();
            fileName = readLine(scanner);
            if (fileName == null) return;
            fileName = fileName.replaceAll("\\s+", "");
        }

        //Same rule as store: a name without the extension means the file with the extension
        if (!fileName.endsWith(".pim")) {
            fileName = fileName + ".pim";
        }

        try {
            load.exe(manager, fileName);
            view.successLoad();
        } catch (Exception e){
            view.failLoad(e.getMessage());
        }
    }

    private void delete(Scanner scanner) {
        DeleteView deleteView = new DeleteView();
        Delete delete = new Delete();

        deleteView.inputMessage();

        // Keep asking until a record is deleted; an empty line returns to the main menu
        while (true) {
            String line = readLine(scanner);
            if (line == null || line.isBlank()) {
                deleteView.cancelled();
                return;
            }

            int id;
            try {
                id = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                deleteView.invalidInput();
                continue;
            }

            if (id < 0) {
                deleteView.cancelled();
                return;
            }

            if (delete.delete(manager, id)) {
                deleteView.success();
                return;
            }

            deleteView.fail();
        }
    }

    private void print(Scanner scanner) {
        PrintView printView = new PrintView();
        Print print = new Print();
        int type = 0;

        printView.inputMessage();
        if (scanner.hasNextInt()) {
            type = scanner.nextInt();
            readLine(scanner);
        } else if (readLine(scanner) == null) return;

        switch (type) {
            //Specific PIR
            case 1 -> {
                int id;

                printView.inputIDMessage();

                // Keep asking until a record is printed; an empty line returns to the main menu
                while (true) {
                    String line = readLine(scanner);
                    if (line == null || line.isBlank()) {
                        printView.cancelled();
                        return;
                    }

                    try {
                        id = Integer.parseInt(line.trim());
                    } catch (NumberFormatException e) {
                        printView.invalidInput();
                        continue;
                    }

                    Record record = print.searchByID(manager, id);
                    if (record == null) {
                        printView.fail();
                        continue;
                    }

                    printView.success();
                    print.print(List.of(record), formatter);
                    return;
                }
            }
            //All PIR
            case 2 -> {
                if (!manager.getRecords().isEmpty()) {
                    printView.success();
                    print.print(manager.getRecords(), formatter);
                } else printView.emptyMessage();
            }
            default -> view.function_ErrorMessage();
        }
    }

    private void search(Scanner scanner) {
        SearchView searchView = new SearchView();
        Search search = new Search();

        List<Token> tokens = buildQuery(scanner, searchView);
        if (tokens == null) {
            searchView.cancelledMessage();
            return;
        }

        try {
            searchView.printResults(search.search(manager, tokens));
        } catch (IllegalArgumentException e) {
            //The menus only ever offer steps that fit the query, so this is a last resort
            searchView.invalidConditionMessage();
        }
    }

    /**
     * Builds the query one menu choice at a time. Every step offers only the choices
     * that are legal at that point, so the query can never be left half written.
     */
    private List<Token> buildQuery(Scanner scanner, SearchView view) {
        List<Token> tokens = new ArrayList<>();
        int openGroups = 0;

        while (true) {
            TokenType last = tokens.isEmpty() ? null : tokens.getLast().getType();
            boolean operandExpected = last == null
                    || last == TokenType.LEFT_PAREN
                    || last == TokenType.AND
                    || last == TokenType.OR
                    || last == TokenType.NOT;

            List<SearchView.Option> options = new ArrayList<>();
            if (operandExpected) {
                options.add(SearchView.Option.FIELD);
                if (last != TokenType.NOT)
                    options.add(SearchView.Option.NOT);
                options.add(SearchView.Option.OPEN_GROUP);
                if (!tokens.isEmpty())
                    options.add(SearchView.Option.REMOVE_LAST);
            } else {
                options.add(SearchView.Option.LOGICAL);
                options.add(SearchView.Option.REMOVE_LAST);
                options.add(openGroups == 0 ? SearchView.Option.RUN : SearchView.Option.CLOSE_GROUP);
            }
            options.add(SearchView.Option.CANCEL);

            // The menu is printed once per step: an invalid answer only repeats the prompt,
            // because readChoice() prints the error and the prompt again itself
            view.menu(Token.renderQuery(tokens), options);
            int choice = readChoice(scanner, view, options.size());
            while (choice == 0)
                choice = readChoice(scanner, view, options.size());
            if (choice == -1)
                return null;

            switch (options.get(choice - 1)) {
                case FIELD -> {
                    Criterion criterion = buildCriterion(scanner, view, Token.renderQuery(tokens));
                    if (criterion != null)
                        tokens.add(Token.criterion(criterion));
                }
                case NOT -> tokens.add(Token.not());
                case OPEN_GROUP -> {
                    tokens.add(Token.leftParen());
                    openGroups++;
                }
                case CLOSE_GROUP -> {
                    tokens.add(Token.rightParen());
                    openGroups--;
                }
                case LOGICAL -> {
                    Token connector = readLogical(scanner, view);
                    if (connector != null)
                        tokens.add(connector);
                }
                case REMOVE_LAST -> {
                    //Keep the open group count in step: dropping a ')' re-opens a group, dropping a '(' closes it
                    TokenType removed = tokens.removeLast().getType();
                    if (removed == TokenType.RIGHT_PAREN)
                        openGroups++;
                    else if (removed == TokenType.LEFT_PAREN)
                        openGroups--;
                }
                case RUN -> {
                    return tokens;
                }
                case CANCEL -> {
                    return null;
                }
            }
        }
    }

    /** Collects one condition; returns null when the user goes back to the main menu instead. */
    private Criterion buildCriterion(Scanner scanner, SearchView view, String query) {
        Field field = readField(scanner, view);
        if (field == null)
            return null;

        return switch (field.getKind()) {
            case TYPE -> {
                Type type = readType(scanner, view);
                yield type == null ? null : Criterion.forType(type);
            }
            case TEXT -> {
                String value = readText(scanner, view, field, query);
                yield value == null ? null : Criterion.forText(field, value);
            }
            case TIME -> {
                Operator operator = readOperator(scanner, view, field, query);
                if (operator == null) {
                    yield null;
                }
                LocalDateTime time = readTime(scanner, view, field, query);
                yield time == null ? null : Criterion.forTime(field, operator, time);
            }
        };
    }

    private Field readField(Scanner scanner, SearchView view) {
        Field[] fields = Field.values();
        view.fieldMenu();
        // An invalid answer repeats the prompt only, so the menu is not printed a second time
        while (true) {
            int choice = readChoice(scanner, view, fields.length + 1);
            if (choice == -1)
                return null;
            if (choice == 0)
                continue;
            if (choice == fields.length + 1)
                return null;
            return fields[choice - 1];
        }
    }

    private Type readType(Scanner scanner, SearchView view) {
        Type[] types = Type.values();
        view.typeMenu();
        while (true) {
            int choice = readChoice(scanner, view, types.length + 1);
            if (choice == -1)
                return null;
            if (choice == 0)
                continue;
            if (choice == types.length + 1)
                return null;
            return types[choice - 1];
        }
    }

    private Operator readOperator(Scanner scanner, SearchView view, Field field, String query) {
        view.operatorMenu(query, field);
        while (true) {
            int choice = readChoice(scanner, view, 4);
            if (choice == -1)
                return null;
            if (choice == 0)
                continue;
            return switch (choice) {
                case 1 -> Operator.LESS_THAN;
                case 2 -> Operator.GREATER_THAN;
                case 3 -> Operator.EQUAL;
                default -> null;
            };
        }
    }

    private Token readLogical(Scanner scanner, SearchView view) {
        view.logicalMenu();
        while (true) {
            int choice = readChoice(scanner, view, 3);
            if (choice == -1)
                return null;
            if (choice == 0)
                continue;
            return switch (choice) {
                case 1 -> Token.and();
                case 2 -> Token.or();
                default -> null;
            };
        }
    }

    private String readText(Scanner scanner, SearchView view, Field field, String query) {
        view.valuePrompt(query, field);
        //Whatever is typed is the value: quote marks are ordinary characters, not delimiters.
        //An empty line is the way back, because an empty value could never match a record.
        String value = readLine(scanner);
        if (value == null) return null;
        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private LocalDateTime readTime(Scanner scanner, SearchView view, Field field, String query) {
        while (true) {
            view.valuePrompt(query, field);
            String value = readLine(scanner);
            if (value == null) return null;
            value = value.trim();
            if (value.isEmpty())
                return null;
            try {
                return LocalDateTime.parse(value, Criterion.TIME_FORMAT);
            } catch (DateTimeParseException e) {
                view.timeFormatMessage(field);
            }
        }
    }

    /**
     * Reads one menu number; 0 means "the input was not one of the offered numbers",
     * -1 means the input has ended and the search has to stop.
     */
    private int readChoice(Scanner scanner, SearchView view, int max) {
        String line = readLine(scanner);
        if (line == null) return -1;
        line = line.trim();
        if (!line.isEmpty()) {
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= max)
                    return choice;
            } catch (NumberFormatException e) {
                //Falls through to the error message below
            }
        }
        view.invalidInputMessage();
        return 0;
    }

    /**
     * Reads one line of the session input, or returns null when the input has ended.
     * <p>
     * Without this check, nextLine() throws a NoSuchElementException as soon as a scripted
     * or truncated session ("java Main &lt; input.txt") runs out of lines, and the program
     * dies instead of going back to the main menu.
     */
    private String readLine(Scanner scanner) {
        //End of input: stop the current function instead of throwing NoSuchElementException
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    private void modify(Scanner scanner) {
        ModifyView modifyView = new ModifyView();
        Modify modify = new Modify();
        Record record = null;

        // Loop until a valid record is found; an empty line returns to the main menu
        while (record == null) {
            modifyView.inputMessage();

            String line = readLine(scanner);
            if (line == null || line.isBlank()) {
                modifyView.cancelled();
                return;
            }

            int id;
            try {
                id = Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                modifyView.invalidInput();
                continue;
            }

            if (id < 0) {
                modifyView.cancelled();
                return;
            }

            record = modify.searchByID(manager, id);

            if (record == null) {
                modifyView.recordNotFound();
                modifyView.tryAgain();
            }
        }

        // PRINT THE PIR to be modified
        modifyView.displayRecordDetails(record, formatter);

        // Determine record type and prompt for appropriate updates
        String className = record.getClass().getSimpleName();
        // changed: the user asked for something different; modified: that request was applied
        boolean changed = false;
        boolean modified = false;

        switch (className) {
            case "Text" -> {
                Text textRecord = (Text) record;
                modifyView.inputDescriptionMessage();
                String desc = readLine(scanner);
                if (desc == null) return;

                //Blank keeps the current text, and an unchanged text is not a modification
                if (!desc.isBlank() && !desc.equals(textRecord.getText())) {
                    changed = true;
                    modified = modify.modifyText(record, desc);
                }
            }
            case "Task" -> {
                Task taskRecord = (Task) record;
                modifyView.inputDescriptionMessage();
                String desc = readLine(scanner);
                if (desc == null) return;

                boolean newDescription = !desc.isBlank() && !desc.equals(taskRecord.getDescription());
                LocalDateTime deadline = null;

                while (true) {
                    modifyView.inputDeadlineMessage();
                    String deadlineStr = readLine(scanner);
                    if (deadlineStr == null) return;
                    if (deadlineStr.isBlank()) break;
                    //A single "-" leaves the rest of the modification and returns to the main menu
                    if (deadlineStr.trim().equals("-")) {
                        modifyView.cancelled();
                        return;
                    }
                    try {
                        LocalDateTime parsed = LocalDateTime.parse(deadlineStr, formatter);
                        if (!parsed.equals(taskRecord.getDeadline())) deadline = parsed;
                        break;
                    } catch (Exception e) {
                        modifyView.deadlineFormatError();
                    }
                }

                if (newDescription || deadline != null) {
                    changed = true;
                    modified = modify.modifyTask(record, newDescription ? desc : null, deadline);
                }
            }
            case "Event" -> {
                Event eventRecord = (Event) record;
                modifyView.inputDescriptionMessage();
                String desc = readLine(scanner);
                if (desc == null) return;

                boolean newDescription = !desc.isBlank() && !desc.equals(eventRecord.getDescription());

                LocalDateTime start = null;
                while (true) {
                    modifyView.inputStartMessage();
                    String startStr = readLine(scanner);
                    if (startStr == null) return;
                    if (startStr.isBlank()) break;
                    //A single "-" leaves the rest of the modification and returns to the main menu
                    if (startStr.trim().equals("-")) {
                        modifyView.cancelled();
                        return;
                    }
                    try {
                        LocalDateTime parsed = LocalDateTime.parse(startStr, formatter);
                        if (!parsed.equals(eventRecord.getStarting())) start = parsed;
                        break;
                    } catch (Exception e) {
                        modifyView.startingTimeFormatError();
                    }
                }

                LocalDateTime alarm = null;
                while (true) {
                    modifyView.inputAlarmMessage();
                    String alarmStr = readLine(scanner);
                    if (alarmStr == null) return;
                    if (alarmStr.isBlank()) break;
                    //A single "-" leaves the rest of the modification and returns to the main menu
                    if (alarmStr.trim().equals("-")) {
                        modifyView.cancelled();
                        return;
                    }
                    LocalDateTime parsed;
                    try {
                        parsed = LocalDateTime.parse(alarmStr, formatter);
                    } catch (Exception e) {
                        modifyView.alarmTimeFormatError();
                        continue;
                    }
                    //The alarm cannot ring after the event starts, so an invalid pair is asked again
                    LocalDateTime effectiveStart = start != null ? start : eventRecord.getStarting();
                    if (parsed.isAfter(effectiveStart)) {
                        modifyView.invalidAlarm();
                        continue;
                    }
                    if (!parsed.equals(eventRecord.getAlarm())) alarm = parsed;
                    break;
                }

                //Moving the starting time before the current alarm would break the same rule
                LocalDateTime finalStart = start != null ? start : eventRecord.getStarting();
                LocalDateTime finalAlarm = alarm != null ? alarm : eventRecord.getAlarm();
                if (finalAlarm.isAfter(finalStart)) {
                    modifyView.invalidAlarm();
                    return;
                }

                if (newDescription || start != null || alarm != null) {
                    changed = true;
                    modified = modify.modifyEvent(record, newDescription ? desc : null, start, alarm);
                }
            }
            case "Contact" -> {
                Contact contactRecord = (Contact) record;
                modifyView.inputNameMessage();
                String name = readLine(scanner);
                if (name == null) return;

                modifyView.inputPhoneMessage();
                String phone = readLine(scanner);
                if (phone == null) return;

                modifyView.inputAddressMessage();
                String address = readLine(scanner);
                if (address == null) return;

                boolean newName = !name.isBlank() && !name.equals(contactRecord.getName());
                boolean newPhone = !phone.isBlank() && !phone.equals(contactRecord.getPhone());
                boolean newAddress = !address.isBlank() && !address.equals(contactRecord.getAddress());

                if (newName || newPhone || newAddress) {
                    changed = true;
                    modified = modify.modifyContact(record,
                            newName ? name : null,
                            newPhone ? phone : null,
                            newAddress ? address : null);
                }
            }
            default -> {
                modifyView.fail();
                return;
            }
        }

        if (!changed) {
            modifyView.noChange();
        } else if (modified) {
            modifyView.success();
        } else {
            modifyView.fail();
        }
    }
}
