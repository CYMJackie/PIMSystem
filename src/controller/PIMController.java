package controller;

import model.PIRManager;
import model.command.CreateContact;
import model.command.CreateText;
import view.PIMView;
import view.command.CreateContactView;
import view.command.CreateTextView;
import view.command.CreateView;

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
            case 2 -> {}
            //Create Event
            case 3 -> {}
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

    }

    private void createEvent(Scanner scanner) {

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
