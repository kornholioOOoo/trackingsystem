package Main;

import java.util.Scanner;
import Config.config;

public class student {
    public static void displayStudentDashboard(String studentName) {
        Scanner sc = new Scanner(System.in);
        config conf = new config();
        boolean studentLoggedIn = true;

        while (studentLoggedIn) {
            System.out.println("\n===== STUDENT DASHBOARD =====");
            System.out.println("Welcome, " + studentName + "!");
            System.out.println("1. View Grades");
            System.out.println("2. Edit Profile");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Viewing Grades...");
                    break;
                case 2:
                    System.out.println("Editing Profile...");
                    break;
                case 3:
                    System.out.println("Logging out...");
                    studentLoggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}
