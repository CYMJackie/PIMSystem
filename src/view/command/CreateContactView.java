package view.command;

public class CreateContactView {
    public void inputNameMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the name for the PIR (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void inputPhoneMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the phone number for the PIR (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void inputAddressMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the address for the PIR (Optional, enter nothing to skip, Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void emptyName() {
        System.out.println("============================================================================================");
        System.out.println("The name can't be empty! Please input the name again(Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void emptyNumber() {
        System.out.println("============================================================================================");
        System.out.println("The phone number can't be empty! Please input the phone number again(Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void success() {
        System.out.println("============================================================================================");
        System.out.println("The record is created!");
        System.out.println("============================================================================================");
    }
}
