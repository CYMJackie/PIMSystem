package view;

import java.util.Scanner;

public class View {

    public void initialize() {
        Scanner scanner = new Scanner(System.in);
        int function, type;

        System.out.println("Welcome to Personal Information Management(PIM) System. ");
        System.out.println("============================================================================================");
        System.out.println("Functions in PIM System: ");
        System.out.println("1. Create record");
        System.out.println("2. Modify record");
        System.out.println("3. Delete record(s)");
        System.out.println("4. Search record(s)");
        System.out.println("5. Print record(s)");
        System.out.println("6. Store record(s)");
        System.out.println("7. Load record(s)");
        System.out.println("============================================================================================");
        System.out.print("Press number to operate the following function(s): ");
        function = scanner.nextInt();
    }
}