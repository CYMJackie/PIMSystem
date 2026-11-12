package view.command;

public class SaveView {

    public void inputFileName() {
        System.out.println("============================================================================================");
        System.out.println("Enter the file name to save all records to a \".pim\" file, do not need to enter \".pim\" extension (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void successSave() {
        System.out.println("============================================================================================");
        System.out.println("Records are successfully saved in current project directory. ");
        System.out.println("============================================================================================");
    }

    public void failSave(String message) {
        System.out.println("============================================================================================");
        System.out.println("Saving file failed! Error message: " + message);
        System.out.println("============================================================================================");
    }

    public void emptyRecord() {
        System.out.println("============================================================================================");
        System.out.println("No record in system to save! ");
        System.out.println("============================================================================================");
    }
}
