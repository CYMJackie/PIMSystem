package view.command;

import model.Record;
import model.command.Print;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ModifyView {
    public void inputMessage() {
        System.out.print("Enter the ID of the record you want to modify (press enter without typing anything to return to the menu): ");
    }

    public void invalidInput() {
        System.out.println("Invalid input. Please enter a valid integer ID.");
    }

    public void recordNotFound() {
        System.out.println("Record with the specified ID was not found.");
    }

    public void cancelled() {
        System.out.println("Modification cancelled.");
    }

    public void tryAgain() {
        System.out.println("Please try entering the ID again (or press enter without typing anything to return to the menu).");
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
        System.out.print("Enter new deadline (yyyy-MM-dd HH:mm:ss, leave blank to keep current): ");
    }

    public void deadlineFormatError() {
        System.out.println("Invalid deadline format! Please enter a valid deadline format.");
    }

    public void inputStartMessage() {
        System.out.print("Enter new starting time (yyyy-MM-dd HH:mm:ss, leave blank to keep current): ");
    }

    public void startingTimeFormatError() {
        System.out.println("Invalid start time format! Please enter a valid start time format.");
    }

    public void inputAlarmMessage() {
        System.out.print("Enter new alarm time (yyyy-MM-dd HH:mm:ss, leave blank to keep current): ");
    }

    public void alarmTimeFormatError() {
        System.out.println("Invalid alarm time format! Please enter a valid alarm time format.");
    }

    public void invalidAlarm() {
        System.out.println("The alarm cannot be later than the starting time! Please enter the alarm again (or leave it blank to keep the current one).");
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

    public void noChange() {
        System.out.println("No changes were made.");
    }

    public void fail() {
        System.out.println("Failed to modify record.");
    }
}
