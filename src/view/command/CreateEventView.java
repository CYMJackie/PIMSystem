package view.command;

public class CreateEventView {
    public void inputDescMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the description for the PIR (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void inputStartMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the starting time (yyyy-MM-dd HH:mm:ss) for the PIR (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void inputAlarmMessage() {
        System.out.println("============================================================================================");
        System.out.println("Input the alarm time (yyyy-MM-dd HH:mm:ss) for the PIR (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void emptyDesc() {
        System.out.println("============================================================================================");
        System.out.println("The description can't be empty! Please input the description again (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }


    public void emptyStart() {
        System.out.println("============================================================================================");
        System.out.println("The starting time can't be empty! Please input the starting time again (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void emptyAlarm() {
        System.out.println("============================================================================================");
        System.out.println("The alarm time can't be empty! Please input the alarm time again(Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void startFormatError() {
        System.out.println("============================================================================================");
        System.out.println("The format of starting time should be yyyy-MM-dd HH:mm:ss! Please input the starting time again (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }


    public void alarmFormatError() {
        System.out.println("============================================================================================");
        System.out.println("The format of alarm time should be yyyy-MM-dd HH:mm:ss! Please input the alarm time again (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void invalidAlarm() {
        System.out.println("============================================================================================");
        System.out.println("The alarm time should before the starting time! Please input the alarm time again (Press enter to confirm): ");
        System.out.println("============================================================================================");
    }

    public void success() {
        System.out.println("============================================================================================");
        System.out.println("The record is created!");
        System.out.println("============================================================================================");
    }

}
