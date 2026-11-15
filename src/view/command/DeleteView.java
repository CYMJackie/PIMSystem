package view.command;

public class DeleteView {
    public void inputMessage() {
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
        System.out.println("The record is deleted!");
        System.out.println("============================================================================================");
    }

    public void cancelled() {
        System.out.println("============================================================================================");
        System.out.println("Deletion cancelled.");
        System.out.println("============================================================================================");
    }

    public void fail() {
        System.out.println("============================================================================================");
        System.out.println("The record doesn't exist! Please input relative ID (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }
}
