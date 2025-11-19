package Main;

import java.sql.*;
import java.util.Scanner;
import Config.config;

public class student {

    public static void displayStudentDashboard(int studentId, String studentName) {
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
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    viewGrades(studentId, conf);
                    break;
                case 2:
                    editProfile(studentId, conf, sc);
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

    private static void viewGrades(int studentId, config conf) {
        try (Connection conn = conf.connectDB()) {
            String sql = "SELECT s.s_code, s.s_name, s.units, s.y_level, s.sem, " +
                         "g.prelim, g.midterm, g.prefi, g.final, g.remarks " +
                         "FROM tbl_grades g " +
                         "JOIN tbl_subjects s ON g.s_id = s.s_id " +
                         "WHERE g.u_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nYour Grades:");
            System.out.printf("%-10s %-25s %-5s %-5s %-5s %-8s %-8s %-8s %-8s %-10s\n",
                    "Code", "Subject Name", "Units", "Year", "Sem", "Prelim", "Midterm", "Prefi", "Final", "Remarks");
            while (rs.next()) {
                System.out.printf("%-10s %-25s %-5d %-5d %-5d %-8s %-8s %-8s %-8s %-10s\n",
                        rs.getString("s_code"),
                        rs.getString("s_name"),
                        rs.getInt("units"),
                        rs.getInt("y_level"),
                        rs.getInt("sem"),
                        rs.getString("prelim"),
                        rs.getString("midterm"),
                        rs.getString("prefi"),
                        rs.getString("final"),
                        rs.getString("remarks"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving grades: " + e.getMessage());
        }
    }

    private static void editProfile(int studentId, config conf, Scanner sc) {
        try (Connection conn = conf.connectDB()) {
            System.out.print("Enter new name: ");
            String newName = sc.nextLine();
            System.out.print("Enter new email: ");
            String newEmail = sc.nextLine();
            System.out.print("Enter new password: ");
            String newPass = sc.nextLine();

            String sql = "UPDATE tbl_users SET u_name = ?, u_email = ?, u_pass = ? WHERE u_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newName);
            ps.setString(2, newEmail);
            ps.setString(3, newPass);
            ps.setInt(4, studentId);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Failed to update profile.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }
}
