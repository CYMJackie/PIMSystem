package view;

public class PIMView {

    public void initialMessage() {
        System.out.println("Welcome to Personal Information Management(PIM) System. ");

    }

    public void inputMessage() {
        System.out.println("============================================================================================");
        System.out.println("Functions in PIM System: ");
        System.out.println("1. Create record");
        System.out.println("2. Modify record");
        System.out.println("3. Delete record(s)");
        System.out.println("4. Search record(s)");
        System.out.println("5. Print record(s)");
        System.out.println("6. Store record(s)");
        System.out.println("7. Load record(s)");
        System.out.println("8. Exit");
        System.out.println("============================================================================================");
        System.out.print("Press number to operate the above function(s): ");
    }

    public void function_ErrorMessage() {
        System.out.println("The input is invalid. Please input again!");
    }

    public void exitMessage() {
        System.out.println("Thank You! See you next time.");
    }

}