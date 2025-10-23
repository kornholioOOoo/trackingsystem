package Main;

import java.util.Scanner;
import Config.config;

public class teacher {
    public static void displayTeacherDashboard(String teacherName) {
        Scanner sc = new Scanner(System.in);
        config conf = new config();
        boolean teacherLoggedIn = true;

        while (teacherLoggedIn) {
            System.out.println("\n===== TEACHER DASHBOARD =====");
            System.out.println("Welcome, " + teacherName + "!");
            System.out.println("1. View Students");
            System.out.println("2. Add Grades");
            System.out.println("3. View Subjects");
            System.out.println("4. Edit Profile");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Viewing Students...");
                    break;
                case 2:
                    System.out.println("Adding Grades...");
                    break;
                case 3:
                    System.out.println("Viewing Subjects...");
                    break;
                case 4:
                    System.out.println("Editing Profile...");
                    break;
                case 5:
                    System.out.println("Logging out...");
                    teacherLoggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}
