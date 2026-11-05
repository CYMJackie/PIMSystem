package controller;

import model.PIRManager;
import model.command.CreateContact;
import model.command.CreateTask;
import model.command.CreateEvent;
import model.command.CreateText;
import view.PIMView;
import view.command.CreateContactView;
import view.command.CreateEventView;
import view.command.CreateTextView;
import view.command.CreateView;
import view.command.CreateTaskView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
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
                case 4 -> {}
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
}
