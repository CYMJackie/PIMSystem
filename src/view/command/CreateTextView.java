package view.command;

public class CreateTextView {
    public void inputMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the description for the PIR (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void emptyInput() {
        System.out.println("============================================================================================");
        System.out.println("The description can't be empty! Please input relative description(Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void success() {
        System.out.println("============================================================================================");
        System.out.println("The record is created!");
        System.out.println("============================================================================================");
    }
}
