package controller;

import model.PIRManager;
import model.command.*;
import model.Record;
import view.PIMView;
import view.command.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
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
                case 2 -> {}
                //Delete record
                case 3 -> delete(scanner);
                //Search record
                case 4 -> {}
                //Print record
                case 5 -> print(scanner);
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

    private void delete(Scanner scanner) {
        DeleteView deleteView = new DeleteView();
        Delete delete = new Delete();
        int id = -1;

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
                Record record = null;

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
}
