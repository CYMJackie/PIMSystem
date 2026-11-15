package view.command;

public class LoadView {

    public void inputFileName() {
        System.out.println("============================================================================================");
        System.out.println("Enter file name or full path to load: (Press enter to confirm)");
        System.out.println("============================================================================================");
    }

    public void emptyFileName() {
        System.out.println("============================================================================================");
        System.out.println("File name cannot be empty! Please input again (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void successLoad() {
        System.out.println("============================================================================================");
        System.out.println("Records are loaded successfully. ");
        System.out.println("============================================================================================");
    }

    public void failLoad(String message) {
        System.out.println("============================================================================================");
        System.out.println("Loading failed! Error Message: " + message);
        System.out.println("============================================================================================");
    }

}
