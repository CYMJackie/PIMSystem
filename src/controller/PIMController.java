package controller;

import model.PIRManager;
import model.command.CreateContact;
import model.command.CreateTask;
import model.command.CreateEvent;
import model.command.CreateText;
import model.command.Search;
import model.search.Criterion;
import model.search.Field;
import model.search.Operator;
import model.search.SearchResult;
import model.search.Token;
import model.search.TokenType;
import model.search.Type;
import view.PIMView;
import view.command.CreateContactView;
import view.command.CreateEventView;
import view.command.CreateTextView;
import view.command.CreateView;
import view.command.CreateTaskView;
import view.command.SearchView;
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

    public PIMController(PIRManager manager, PIMView view) {
        this.manager = manager;
        this.view = view;
    }

    public void initialize() {
        int function, type = 0;
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
                break loop;
            }

            if (scanner.hasNextInt())
                function = scanner.nextInt();
            else {
                view.function_ErrorMessage();
                scanner.nextLine(); //Discard the invalid token, or it would be read again forever
                continue;
            }

            switch (function) {
                //Create record
                case 1 -> create(scanner, type);
                //Modify record
                case 2 -> {}
                //Delete record
                case 3 -> {}
                //Search record
                case 4 -> search(scanner);
                //Print record
                case 5 -> {}
                //Store record
                case 6 -> {}
                //Load record
                case 7 -> {}
                //Exit
                case 8 -> {
                    view.exitMessage();
                    break loop;
                }
                default -> view.function_ErrorMessage();
            }
        }
    }

    private void create(Scanner scanner, int type) {
        CreateView createView = new CreateView();

        createView.inputMessage();

        //Error handling of whether "type" is integer
        if (scanner.hasNextInt())
            type = scanner.nextInt();

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

        if (scanner.hasNextLine())
            scanner.nextLine();

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

        if (scanner.hasNextLine())
            scanner.nextLine();

        taskView.inputDescriptionMessage();
        String description = scanner.nextLine();
        while (description.isBlank()) {
            taskView.emptyDescription();
            description = scanner.nextLine();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                .withResolverStyle(ResolverStyle.STRICT);

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                .withResolverStyle(ResolverStyle.STRICT);

        if (scanner.hasNextLine())
            scanner.nextLine();

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

        if (scanner.hasNextLine())
            scanner.nextLine();

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
            TokenType last = tokens.isEmpty() ? null : tokens.get(tokens.size() - 1).getType();
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
                    TokenType removed = tokens.remove(tokens.size() - 1).getType();
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
}
