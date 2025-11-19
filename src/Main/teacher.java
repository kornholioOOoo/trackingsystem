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
            System.out.println("3. View grades");
            System.out.println("4. Update grades");
            System.out.println("5. View Subjects Handled");
            System.out.println("6. Edit Profile");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: 
                    System.out.println("\n===== STUDENTS IN YOUR SUBJECTS =====");
                    String qryStudents = "SELECT DISTINCT stu.u_id, stu.u_name, stu.u_email, stu.u_type, stu.u_status " +
                        "FROM tbl_grades g " +
                        "JOIN tbl_users stu ON stu.u_id = g.u_id " +
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

                case 2:
                    System.out.println("\n===== ADD GRADES =====");

                  
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

                  
              
                    double average = (prelim + midterm + preFinal + finalGrade) / 4;
                    String remarks = (average <= 3.0) ? "Passed" : "Failed";

                    String updateGrade = "UPDATE tbl_grades SET prelim=?, midterm=?, prefi=?, final=?, remarks=? WHERE u_id=? AND s_id=?";
                    conf.updateRecord(updateGrade, prelim, midterm, preFinal, finalGrade, remarks, uId, sId);

                    System.out.println("Grade updated successfully! Remarks: " + remarks);

                    break;
                
                    
                case 3:
                    System.out.println("\n===== SUBJECTS ASSIGNED TO YOU =====");

                    // Show subjects assigned to teacher
                    String qryHandle = "SELECT s.s_id, s.s_code, s.s_name, s.units, s.y_level, s.sem " +
                                        "FROM tbl_subjects s " +
                                        "JOIN tbl_connect c ON s.s_id = c.s_id " +
                                        "WHERE c.u_id = ?";
                    List<Map<String, Object>> handledSubjects = conf.fetchRecords(qryHandle, teacherId);

                    if (handledSubjects.isEmpty()) {
                        System.out.println("No subjects assigned.");
                        break;
                    }

                    System.out.printf("%-5s %-10s %-25s %-5s %-10s %-10s\n", "ID", "Code", "Name", "Units", "Year Level", "Semester");
                    for (Map<String, Object> sub : handledSubjects) {
                        System.out.printf("%-5s %-10s %-25s %-5s %-10s %-10s\n",
                                sub.get("s_id"), sub.get("s_code"), sub.get("s_name"),
                                sub.get("units"), sub.get("y_level"), sub.get("sem"));
                    }

                    System.out.println("\nDo you want to view grades for a subject? (Y/N): ");
                    String viewChoice = sc.nextLine();
                    if (viewChoice.equalsIgnoreCase("Y")) {
                        System.out.print("Enter Subject ID to view grades: ");
                        int gradeSubId = sc.nextInt();
                        sc.nextLine();

                        String qryGrades = "SELECT u.u_id, u.u_name, g.prelim, g.midterm, g.prefi, g.final, g.remarks " +
                                           "FROM tbl_grades g " +
                                           "JOIN tbl_users u ON g.u_id = u.u_id " +
                                           "WHERE g.s_id = ?";
                        List<Map<String, Object>> grades = conf.fetchRecords(qryGrades, gradeSubId);

                        if (grades.isEmpty()) {
                            System.out.println("No grades recorded for this subject yet.");
                        } else {
                            System.out.printf("%-5s %-20s %-8s %-8s %-8s %-8s %-10s\n", "ID", "Name", "Prelim", "Midterm", "Pre-Final", "Final", "Remarks");
                            for (Map<String, Object> g : grades) {
                                System.out.printf("%-5s %-20s %-8s %-8s %-8s %-8s %-10s\n",
                                        g.get("u_id"), g.get("u_name"),
                                        g.get("prelim"), g.get("midterm"), g.get("prefi"), g.get("final"), g.get("remarks"));
                            }
                        }
                    }
                    break;

                    
                case 4: 
                    System.out.println("\n===== SUBJECTS ASSIGNED TO YOU =====");

                  
                    String qryhandle = "SELECT s.s_id, s.s_code, s.s_name, s.units, s.y_level, s.sem " +
                                        "FROM tbl_subjects s " +
                                        "JOIN tbl_connect c ON s.s_id = c.s_id " +
                                        "WHERE c.u_id = ?";
                    List<Map<String, Object>> handledSubject = conf.fetchRecords(qryhandle, teacherId);

                    if (handledSubject.isEmpty()) {
                        System.out.println("No subjects assigned.");
                        break;
                    }

                    System.out.printf("%-5s %-10s %-25s %-5s %-10s %-10s\n", "ID", "Code", "Name", "Units", "Year Level", "Semester");
                    for (Map<String, Object> sub : handledSubject) {
                        System.out.printf("%-5s %-10s %-25s %-5s %-10s %-10s\n",
                                sub.get("s_id"), sub.get("s_code"), sub.get("s_name"),
                                sub.get("units"), sub.get("y_level"), sub.get("sem"));
                    }

                    System.out.println("\nDo you want to view or edit grades for a subject? (Y/N): ");
                    String viewCh = sc.nextLine();
                    if (viewCh.equalsIgnoreCase("Y")) {
                        System.out.print("Enter Subject ID to view/edit grades: ");
                        int gradeSubId = sc.nextInt();
                        sc.nextLine();

                       
                        String qryGrades = "SELECT u.u_id, u.u_name, g.g_id, g.prelim, g.midterm, g.prefi, g.final, g.remarks " +
                                           "FROM tbl_grades g " +
                                           "JOIN tbl_users u ON g.u_id = u.u_id " +
                                           "WHERE g.s_id = ?";
                        List<Map<String, Object>> grades = conf.fetchRecords(qryGrades, gradeSubId);

                        if (grades.isEmpty()) {
                            System.out.println("No grades recorded for this subject yet.");
                        } else {
                            System.out.printf("%-5s %-20s %-8s %-8s %-8s %-8s %-10s\n", "ID", "Name", "Prelim", "Midterm", "Pre-Final", "Final", "Remarks");
                            for (Map<String, Object> g : grades) {
                                System.out.printf("%-5s %-20s %-8s %-8s %-8s %-8s %-10s\n",
                                        g.get("u_id"), g.get("u_name"),
                                        g.get("prelim"), g.get("midterm"), g.get("prefi"), g.get("final"), g.get("remarks"));
                            }

                            System.out.print("\nDo you want to edit a grade? (Y/N): ");
                            String editChoice = sc.nextLine();
                            if (editChoice.equalsIgnoreCase("Y")) {
                                System.out.print("Enter Grade ID to edit: ");
                                int gId = sc.nextInt();

                                System.out.print("Enter new Prelim: ");
                                double newPrelim = sc.nextDouble();
                                System.out.print("Enter new Midterm: ");
                                double newMidterm = sc.nextDouble();
                                System.out.print("Enter new Pre-Final: ");
                                double newPreFinal = sc.nextDouble();
                                System.out.print("Enter new Final: ");
                                double newFinal = sc.nextDouble();
                                sc.nextLine();

                               
                                double averag = (newPrelim + newMidterm + newPreFinal + newFinal) / 4;
                                String newRemarks = (averag <= 3.0) ? "Passed" : "Failed";

                               
                                String updategrade = "UPDATE tbl_grades SET prelim=?, midterm=?, prefi=?, final=?, remarks=? WHERE g_id=?";
                                conf.updateRecord(updategrade, newPrelim, newMidterm, newPreFinal, newFinal, newRemarks, gId);

                                System.out.println("Grade updated successfully! New remarks: " + newRemarks);
                            }
                        }
                    }
                    break;

                    
                    
                case 5:
                    System.out.println("\n===== SUBJECTS ASSIGNED TO YOU =====");

                    String qryHandled = "SELECT s.s_id, s.s_code, s.s_name, s.units, s.y_level, s.sem " +
                                        "FROM tbl_subjects s " +
                                        "JOIN tbl_connect c ON s.s_id = c.s_id " +
                                        "WHERE c.u_id = ?";

                    List<Map<String, Object>> handledsubject = conf.fetchRecords(qryHandled, teacherId);

                    if (handledsubject.isEmpty()) {
                        System.out.println("No subjects assigned.");
                    } else {
                        System.out.printf("%-5s %-10s %-25s %-5s %-10s %-10s\n", 
                                "ID", "Code", "Name", "Units", "Year Level", "Semester");

                        for (Map<String, Object> sub : handledsubject) {
                            System.out.printf("%-5s %-10s %-25s %-5s %-10s %-10s\n",
                                    sub.get("s_id"), sub.get("s_code"), sub.get("s_name"),
                                    sub.get("units"), sub.get("y_level"), sub.get("sem"));
                        }
                    }
                    break;


                case 6: 
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

                case 7: 
                    System.out.println("Logging out...");
                    teacherLoggedIn = false;
                    break;

                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}
