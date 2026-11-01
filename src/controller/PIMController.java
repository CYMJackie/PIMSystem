package controller;

import model.PIRManager;
import view.PIMView;
import view.command.Create;

import java.util.Scanner;

public class PIMController {
    private PIMView view;
    private PIRManager manager;

    public PIMController(PIRManager manager, PIMView view) {
        this.manager = manager;
        this.view = view;
    }

    public void initialize() {
        int function, type = 0;

        view.initialMessage();
        loop: //label of while loop
        while (true) {
            //input request
            view.inputMessage();
            Scanner scanner = new Scanner(System.in);

            //Error handling
            if (scanner.hasNextInt())
                function = scanner.nextInt();
            else {
                view.function_ErrorMessage();
                continue;
            }


            switch (function) {
                //Create record
                case 1 -> {
                    Create create = new Create();
                    create.inputMessage();

                    //Error handling of whether "type" is integer
                    if (scanner.hasNextInt())
                        type = scanner.nextInt();

                    switch (type) {
                        //Create Text
                        case 1 -> {}
                        //Create Task
                        case 2 -> {}
                        //Create Event
                        case 3 -> {}
                        //Create Contact
                        case 4 -> {}
                        default -> view.function_ErrorMessage();
                    }
                }
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
}
