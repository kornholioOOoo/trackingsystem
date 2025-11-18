package Main;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import Config.config;

public class teacher {

    public static void displayTeacherDashboard(int teacherId, String teacherName) {
        Scanner sc = new Scanner(System.in);
        config conf = new config();
        boolean teacherLoggedIn = true;

        while (teacherLoggedIn) {
            System.out.println("\n===== TEACHER DASHBOARD =====");
            System.out.println("Welcome, " + teacherName + "!");
            System.out.println("1. View Students");
            System.out.println("2. Add Grades");
            System.out.println("3. View Subjects Handled");
            System.out.println("4. Edit Profile");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: // View Students
                    System.out.println("\n===== STUDENTS IN YOUR SUBJECTS =====");
                    String qryStudents = "SELECT DISTINCT u.u_id, u.u_name, u.u_email, u.u_type, u.u_status " +
                            "FROM tbl_users u " +
                            "JOIN tbl_grades g ON u.u_id = g.u_id " +
                            "JOIN tbl_connect c ON g.s_id = c.s_id " +
                            "WHERE c.u_id = ?";
                    List<Map<String, Object>> students = conf.fetchRecords(qryStudents, teacherId);
                    if (students.isEmpty()) {
                        System.out.println("No students found for your subjects.");
                    } else {
                        System.out.printf("%-5s %-20s %-25s %-10s %-10s\n", "ID", "Name", "Email", "Type", "Status");
                        for (Map<String, Object> s : students) {
                            System.out.printf("%-5s %-20s %-25s %-10s %-10s\n",
                                    s.get("u_id"), s.get("u_name"), s.get("u_email"),
                                    s.get("u_type"), s.get("u_status"));
                        }
                    }
                    break;

                case 2: // Add Grades
                    System.out.println("\n===== ADD GRADES =====");

                    // Show subjects assigned to teacher
                    String qrySubjects = "SELECT s.s_id, s.s_name FROM tbl_subjects s " +
                            "JOIN tbl_connect c ON s.s_id = c.s_id " +
                            "WHERE c.u_id = ?";
                    List<Map<String, Object>> subjects = conf.fetchRecords(qrySubjects, teacherId);

                    if (subjects.isEmpty()) {
                        System.out.println("You have no subjects assigned yet.");
                        break;
                    }

                    System.out.println("Your Subjects:");
                    for (Map<String, Object> s : subjects) {
                        System.out.println(s.get("s_id") + ". " + s.get("s_name"));
                    }

                    System.out.print("Enter Subject ID to add grades: ");
                    int sId = sc.nextInt();

                    // Show students for that subject
                    String qryEnrolled = "SELECT u.u_id, u.u_name FROM tbl_users u " +
                            "JOIN tbl_grades g ON u.u_id = g.u_id " +
                            "WHERE g.s_id = ?";
                    List<Map<String, Object>> studentsInSubject = conf.fetchRecords(qryEnrolled, sId);

                    if (studentsInSubject.isEmpty()) {
                        System.out.println("No students enrolled in this subject yet.");
                        break;
                    }

                    System.out.println("Students enrolled:");
                    for (Map<String, Object> stu : studentsInSubject) {
                        System.out.println(stu.get("u_id") + ". " + stu.get("u_name"));
                    }

                    System.out.print("Enter Student ID to add grade: ");
                    int uId = sc.nextInt();

                    System.out.print("Enter Prelim: ");
                    double prelim = sc.nextDouble();
                    System.out.print("Enter Midterm: ");
                    double midterm = sc.nextDouble();
                    System.out.print("Enter Pre-Final: ");
                    double preFinal = sc.nextDouble();
                    System.out.print("Enter Final: ");
                    double finalGrade = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("Enter Remarks: ");
                    String remarks = sc.nextLine();

                    String insertGrade = "INSERT INTO tbl_grades(u_id, s_id, prelim, midterm, prefi, final, remarks) VALUES(?,?,?,?,?,?,?)";
                    conf.addRecord(insertGrade, uId, sId, prelim, midterm, preFinal, finalGrade, remarks);
                    System.out.println("Grade added successfully!");
                    break;

                case 3: // View Subjects Handled
                    System.out.println("\n===== SUBJECTS ASSIGNED TO YOU =====");
                    String qryHandled = "SELECT s.s_id, s.s_code, s.s_name, s.units, s.y_level, s.sem, s.status " +
                            "FROM tbl_subjects s " +
                            "JOIN tbl_connect c ON s.s_id = c.s_id " +
                            "WHERE c.u_id = ?";
                    List<Map<String, Object>> handledSubjects = conf.fetchRecords(qryHandled, teacherId);

                    if (handledSubjects.isEmpty()) {
                        System.out.println("No subjects assigned.");
                    } else {
                        System.out.printf("%-5s %-10s %-25s %-5s %-10s %-10s %-10s\n", 
                                "ID", "Code", "Name", "Units", "Year Level", "Semester", "Status");
                        for (Map<String, Object> sub : handledSubjects) {
                            System.out.printf("%-5s %-10s %-25s %-5s %-10s %-10s %-10s\n",
                                    sub.get("s_id"), sub.get("s_code"), sub.get("s_name"),
                                    sub.get("units"), sub.get("y_level"), sub.get("sem"), sub.get("status"));
                        }
                    }
                    break;

                case 4: // Edit Profile
                    System.out.println("\n===== EDIT PROFILE =====");
                    System.out.print("Enter new name: ");
                    String newName = sc.nextLine();
                    System.out.print("Enter new email: ");
                    String newEmail = sc.nextLine();
                    System.out.print("Enter new password: ");
                    String newPass = sc.nextLine();
                    String hashedPass = config.hashPassword(newPass);

                    String updateProfile = "UPDATE tbl_users SET u_name = ?, u_email = ?, u_pass = ? WHERE u_id = ?";
                    conf.updateRecord(updateProfile, newName, newEmail, hashedPass, teacherId);
                    System.out.println("Profile updated successfully!");
                    break;

                case 5: // Logout
                    System.out.println("Logging out...");
                    teacherLoggedIn = false;
                    break;

                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}
