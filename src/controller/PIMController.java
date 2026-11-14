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
            scanner.nextLine();
        }
        else scanner.nextLine();

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
        String description = scanner.nextLine();

        while (description.isBlank()) {
            textView.emptyInput();
            description = scanner.nextLine();
        }

        create.createText(manager, description);
        textView.success();
    }

    private void createTask(Scanner scanner) {
        CreateTaskView taskView = new CreateTaskView();
        CreateTask create = new CreateTask();

        taskView.inputDescriptionMessage();
        String description = scanner.nextLine();
        while (description.isBlank()) {
            taskView.emptyDescription();
            description = scanner.nextLine();
        }

        LocalDateTime parsedDeadline = null;
        taskView.inputDeadlineMessage();
        while (parsedDeadline == null) {
            String deadline = scanner.nextLine();
            parsedDeadline = parseDateTime(deadline, formatter, taskView::emptyDeadline, taskView::deadlineFormatError);
        }


        create.createTask(manager, description, parsedDeadline);
        taskView.success();
    }


    private void createEvent(Scanner scanner) {
        CreateEventView eventView = new CreateEventView();
        CreateEvent create = new CreateEvent();

        eventView.inputDescMessage();
        String description = scanner.nextLine();

        while (description.isBlank()) {
            eventView.emptyDesc();
            description = scanner.nextLine();
        }

        LocalDateTime starting = null;
        eventView.inputStartMessage();
        while (starting == null) {
            String startStr = scanner.nextLine();
            starting=parseDateTime(startStr, formatter, eventView::emptyStart, eventView::startFormatError);
        }

        LocalDateTime alarm = null;
        eventView.inputAlarmMessage();
        while (alarm == null) {
            String alarmStr = scanner.nextLine();
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
        String name = scanner.nextLine();

        while (name.isBlank()) {
            contactView.emptyName();
            name = scanner.nextLine();
        }

        contactView.inputPhoneMessage();
        String phone = scanner.nextLine();

        while (phone.isBlank()) {
            contactView.emptyNumber();
            phone = scanner.nextLine();
        }

        contactView.inputAddressMessage();
        String address = scanner.nextLine();

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
        String fileName = scanner.nextLine().replaceAll("\\s+", "");

        while (fileName.isBlank()) {
            view.emptyFileName();
            fileName = scanner.nextLine().replaceAll("\\s+", "");
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
        String fileName = scanner.nextLine().replaceAll("\\s+", "");

        while (!fileName.endsWith(".pim") || fileName.isBlank()) {
            if (fileName.isBlank()) {
                view.emptyFileName();
            } else {
                view.wrongFileType();
            }
            fileName = scanner.nextLine().replaceAll("\\s+", "");
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
        int id;

        deleteView.inputMessage();

        while (!scanner.hasNextInt()) {
            deleteView.invalidInput();
            scanner.nextLine();
        }

        id = scanner.nextInt();
        scanner.nextLine();

        if(delete.delete(manager, id))
            deleteView.success();
        else deleteView.fail();
    }

    private void print(Scanner scanner) {
        PrintView printView = new PrintView();
        Print print = new Print();
        int type = 0;

        printView.inputMessage();
        if (scanner.hasNextInt()) {
            type = scanner.nextInt();
            scanner.nextLine();
        } else scanner.nextLine();

        switch (type) {
            //Specific PIR
            case 1 -> {
                int id;
                Record record;

                printView.inputIDMessage();

                while (!scanner.hasNextInt()) {
                    printView.invalidInput();
                    scanner.nextLine();
                }

                id = scanner.nextInt();
                scanner.nextLine();

                record = print.searchByID(manager, id);

                if (record == null) {
                    printView.fail();
                } else {
                    printView.success();
                    print.print(List.of(record), formatter);
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

        //The function number was read with nextInt, so the rest of that line is still pending
        if (scanner.hasNextLine())
            scanner.nextLine();

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

            int choice = 0;
            while (choice == 0) {
                view.menu(Token.renderQuery(tokens), options);
                choice = readChoice(scanner, view, options.size());
            }

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
        while (true) {
            view.fieldMenu();
            int choice = readChoice(scanner, view, fields.length + 1);
            if (choice == 0)
                continue;
            if (choice == fields.length + 1)
                return null;
            return fields[choice - 1];
        }
    }

    private Type readType(Scanner scanner, SearchView view) {
        Type[] types = Type.values();
        while (true) {
            view.typeMenu();
            int choice = readChoice(scanner, view, types.length + 1);
            if (choice == 0)
                continue;
            if (choice == types.length + 1)
                return null;
            return types[choice - 1];
        }
    }

    private Operator readOperator(Scanner scanner, SearchView view, Field field, String query) {
        while (true) {
            view.operatorMenu(query, field);
            int choice = readChoice(scanner, view, 4);
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
        while (true) {
            view.logicalMenu();
            int choice = readChoice(scanner, view, 3);
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
        String value = scanner.nextLine().trim();
        return value.isEmpty() ? null : value;
    }

    private LocalDateTime readTime(Scanner scanner, SearchView view, Field field, String query) {
        while (true) {
            view.valuePrompt(query, field);
            String value = scanner.nextLine().trim();
            if (value.isEmpty())
                return null;
            try {
                return LocalDateTime.parse(value, Criterion.TIME_FORMAT);
            } catch (DateTimeParseException e) {
                view.timeFormatMessage(field);
            }
        }
    }

    /** Reads one menu number; 0 means "the input was not one of the offered numbers". */
    private int readChoice(Scanner scanner, SearchView view, int max) {
        String line = scanner.nextLine().trim();
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

    private void modify(Scanner scanner) {
        ModifyView modifyView = new ModifyView();
        Modify modify = new Modify();
        Record record = null;

        // Loop until a valid record is found or user inputs an exit condition (negative ID)
        while (record == null) {
            modifyView.inputMessage();

            while (!scanner.hasNextInt()) {
                modifyView.invalidInput();
                scanner.nextLine();
                modifyView.inputMessage();
            }

            int id = scanner.nextInt();
            scanner.nextLine();

            if (id < 0) {
                System.out.println("Modification cancelled.");
                return;
            }

            record = modify.searchByID(manager, id);

            if (record == null) {
                modifyView.recordNotFound();
                System.out.println("Please try entering the ID again (or enter a negative number to cancel).");
            }
        }

        // PRINT THE PIR to be modified
        modifyView.displayRecordDetails(record, formatter);

        // Determine record type and prompt for appropriate updates
        String className = record.getClass().getSimpleName();
        boolean modified = false;

        switch (className) {
            case "Text" -> {
                modifyView.inputDescriptionMessage();
                String desc = scanner.nextLine();
                if (!desc.isBlank()) {
                    modified = modify.modifyText(record, desc);
                } else {
                    modified = true;
                }
            }
            case "Task" -> {
                modifyView.inputDescriptionMessage();
                String desc = scanner.nextLine();

                LocalDateTime deadline = null;
                if (record instanceof Task taskRecord) {
                    while (true) {
                        modifyView.inputDeadlineMessage();
                        String deadlineStr = scanner.nextLine();
                        if (deadlineStr.isBlank()) {
                            deadline = taskRecord.getDeadline();
                            break;
                        }
                        try {
                            deadline = LocalDateTime.parse(deadlineStr, formatter);
                            break;
                        } catch (Exception e) {
                            ModifyView.deadlineFormatError();
                        }
                    }
                }
                modified = modify.modifyTask(record, desc.isBlank() ? null : desc, deadline);
            }
            case "Event" -> {
                modifyView.inputDescriptionMessage();
                String desc = scanner.nextLine();


                LocalDateTime start = null;
                LocalDateTime alarm = null;

                if (record instanceof Event eventRecord) {
                    while (true) {
                        modifyView.inputStartMessage();
                        String startStr = scanner.nextLine();
                        if (startStr.isBlank()) {
                            start = eventRecord.getStarting();
                            break;
                        }
                        try {
                            start = LocalDateTime.parse(startStr, formatter);
                            break;
                        } catch (Exception e) {
                            ModifyView.startingTimeFormatError();
                        }
                    }

                    while (true) {
                        modifyView.inputAlarmMessage();
                        String alarmStr = scanner.nextLine();
                        if (alarmStr.isBlank()) {
                            alarm = eventRecord.getAlarm();
                            break;
                        }
                        try {
                            alarm = LocalDateTime.parse(alarmStr, formatter);
                            break;
                        } catch (Exception e) {
                            ModifyView.alarmTimeFormatError();
                        }
                    }
                }

                modified = modify.modifyEvent(record, desc.isBlank() ? null : desc, start, alarm);
            }
            case "Contact" -> {
                modifyView.inputNameMessage();
                String name = scanner.nextLine();

                modifyView.inputPhoneMessage();
                String phone = scanner.nextLine();

                modifyView.inputAddressMessage();
                String address = scanner.nextLine();

                modified = modify.modifyContact(record,
                        name.isBlank() ? null : name,
                        phone.isBlank() ? null : phone,
                        address.isBlank() ? null : address);
            }
            default -> {
                modifyView.fail();
                return;
            }
        }

        if (modified) {
            modifyView.success();
        } else {
            modifyView.fail();
        }
    }
}
