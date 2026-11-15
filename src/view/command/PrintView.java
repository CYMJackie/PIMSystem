package view.command;

public class PrintView {
    public void inputMessage() {
        System.out.println("============================================================================================");
        System.out.println("Function to print the PIR(s) (Press enter to confirm): ");
        System.out.println("1. Specific PIR");
        System.out.println("2. All PIRs");
        System.out.println("============================================================================================");
    }

    public void inputIDMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the ID for the PIR (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void invalidInput() {
        System.out.println("============================================================================================");
        System.out.println("The ID should be number(s)! Please input relative ID (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void success() {
        System.out.println("============================================================================================");
        System.out.println("The record(s) is printed below: ");
        System.out.println("============================================================================================");
    }

    public void fail() {
        System.out.println("============================================================================================");
        System.out.println("The record doesn't exist! Please input relative ID (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void emptyMessage() {
        System.out.println("============================================================================================");
        System.out.println("No record is here!");
        System.out.println("============================================================================================");
    }
}
