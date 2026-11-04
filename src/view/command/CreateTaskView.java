package view.command;

public class CreateTaskView {

    public void inputDescriptionMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the description for the Task (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void inputDeadlineMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the deadline(yyyy-MM-dd HH:mm:ss) for the Task (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void emptyDescription() {
        System.out.println("============================================================================================");
        System.out.println("The description is empty, please enter again (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void emptyDeadline() {
        System.out.println("============================================================================================");
        System.out.println("The deadline is empty or in wrong format, please enter again (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void success() {
        System.out.println("============================================================================================");
        System.out.println("The record is created!");
        System.out.println("============================================================================================");
    }
}
