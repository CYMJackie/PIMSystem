package view.command;

import model.Record;
import model.command.Print;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ModifyView {
    public void inputMessage() {
        System.out.print("Enter the ID of the record you want to modify: ");
    }

    public void invalidInput() {
        System.out.println("Invalid input. Please enter a valid integer ID.");
    }

    public void recordNotFound() {
        System.out.println("Record with the specified ID was not found.");
    }

    /**
     * Delegates printing of the record directly to the View layer.
     */
    public void displayRecordDetails(Record record, DateTimeFormatter formatter) {
        Print printCommand = new Print();
        System.out.println("\n--- Current Record Details ---");
        printCommand.print(List.of(record), formatter);
        System.out.println("------------------------------");
    }

    public void inputDescriptionMessage() {
        System.out.print("Enter new description (leave blank to keep current): ");
    }

    public void inputDeadlineMessage() {
        System.out.print("Enter new deadline (uuuu-MM-dd HH:mm:ss, leave blank to keep current): ");
    }

    public void inputStartMessage() {
        System.out.print("Enter new starting time (uuuu-MM-dd HH:mm:ss, leave blank to keep current): ");
    }

    public void inputAlarmMessage() {
        System.out.print("Enter new alarm time (uuuu-MM-dd HH:mm:ss, leave blank to keep current): ");
    }

    public void inputNameMessage() {
        System.out.print("Enter new name (leave blank to keep current): ");
    }

    public void inputPhoneMessage() {
        System.out.print("Enter new phone number (leave blank to keep current): ");
    }

    public void inputAddressMessage() {
        System.out.print("Enter new address (leave blank to keep current): ");
    }

    public void success() {
        System.out.println("Record successfully modified.");
    }

    public void fail() {
        System.out.println("Failed to modify record.");
    }
}
