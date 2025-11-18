package Main;

import Config.config;
import java.util.Scanner;

public class main {

    // ---- VIEW USERS ----
    public static void viewUsers() {
        String Query = "SELECT * FROM tbl_users";
        String[] votersHeaders = {"ID", "Name", "Email", "Type", "Status"};
        String[] votersColumns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        config conf = new config();
        conf.viewRecords(Query, votersHeaders, votersColumns);
    }

    // ---- VIEW SUBJECTS ----
    public static void viewSubjects() {
        config conf = new config();
        String query = "SELECT s_id, s_code, s_name, units, y_level, sem FROM tbl_subjects";
        String[] headers = {"Subject ID", "Code", "Name", "Units", "Year Level", "Semester"};
        String[] columns = {"s_id", "s_code", "s_name", "units", "y_level", "sem"};
        conf.viewRecords(query, headers, columns);
    }

    // ---- VIEW GRADES ----
    public static void viewGrades() {
        config conf = new config();
        String query = "SELECT * FROM tbl_grades";
        String[] headers = {"Grade ID", "User ID", "Subject ID", "Prelim", "Midterm", "Pre-Final", "Final", "Remarks"};
        String[] columns = {"g_id", "u_id", "s_id", "prelim", "midterm", "prefi", "final", "remarks"};
        conf.viewRecords(query, headers, columns);
    }

    // ---- MAIN PROGRAM ----
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        config conf = new config();
        config.connectDB();

        int choice;
        char cont;

        do {
            System.out.println("===== WELCOME TO MY ACADEMIC TRACKING SYSTEM! =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {

                // ------------------------------- LOGIN -------------------------------
                case 1:
                    System.out.print("Enter email: ");
                    String em = sc.next();
                    System.out.print("Enter Password: ");
                    String pas = sc.next();

                    String hashedLoginPass = config.hashPassword(pas);
                    int loginAttempts = 0;
                    boolean loginSuccess = false;

                    while (loginAttempts < 3 && !loginSuccess) {

                        String qry = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";
                        java.util.List<java.util.Map<String, Object>> result =
                                conf.fetchRecords(qry, em, hashedLoginPass);

                        if (result.isEmpty()) {
                            loginAttempts++;
                            System.out.println("INVALID CREDENTIALS. Attempt " + loginAttempts + " of 3");

                            if (loginAttempts == 3) {
                                System.out.println("Too many failed attempts. Exiting...");
                                System.exit(0);
                            }

                            System.out.print("Enter email: ");
                            em = sc.next();
                            System.out.print("Enter Password: ");
                            pas = sc.next();
                            hashedLoginPass = config.hashPassword(pas);

                        } else {

                            java.util.Map<String, Object> user = result.get(0);
                            String stat = user.get("u_status").toString();
                            String type = user.get("u_type").toString();

                            if (stat.equals("Pending")) {
                                System.out.println("Account is Pending. Contact Admin.");
                                break;
                            }

                            System.out.println("LOGIN SUCCESS!");

                            // ------------------------- REGISTRAR DASHBOARD -------------------------
                            if (type.equals("Registrar")) {
                                boolean adminLoggedIn = true;

                                while (adminLoggedIn) {

                                    System.out.println("\n===== REGISTRAR DASHBOARD =====");
                                    System.out.println("1. Manage Pending Accounts");
                                    System.out.println("2. Manage Subjects");
                                    System.out.println("3. Manage Grades");
                                    System.out.println("4. View Users");
                                    System.out.println("5. Update Users");
                                    System.out.println("6. Delete Users");
                                    System.out.println("7. Logout");
                                    System.out.print("Enter Action: ");
                                    int action = sc.nextInt();

                                    switch (action) {

                                        // ------ MANAGE PENDING ACCOUNTS ------
                                        case 1:
                                            viewUsers();
                                            System.out.print("Enter ID to Approve: ");
                                            int ids = sc.nextInt();
                                            String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
                                            conf.updateRecord(sql, "Approved", ids);
                                            break;

                                        // ======================= MANAGE SUBJECTS =======================
                                        case 2:
                                            boolean subjectMenu = true;

                                            while (subjectMenu) {
                                                System.out.println("\n===== MANAGE SUBJECTS =====");
                                                System.out.println("1. Assign Subject to Teacher");
                                                System.out.println("2. View Subjects");
                                                System.out.println("3. Add Subject");
                                                System.out.println("4. Update Subject");
                                                System.out.println("5. Delete Subject");
                                                System.out.println("6. Back");
                                                System.out.print("Choose: ");
                                                int sOption = sc.nextInt();
                                                sc.nextLine();

                                                switch (sOption) {

                                                    // -------- ASSIGN SUBJECT TO TEACHER --------
                                                    case 1:
                                                        System.out.println("\n===== ASSIGN SUBJECT TO TEACHER =====");

                                                        // Show teachers
                                                        String teacherQuery = "SELECT u_id, u_name, u_email FROM tbl_users WHERE u_type='Teacher' AND u_status='Approved'";
                                                        String[] tHead = {"Teacher ID", "Name", "Email"};
                                                        String[] tCol = {"u_id", "u_name", "u_email"};
                                                        conf.viewRecords(teacherQuery, tHead, tCol);

                                                        System.out.print("Enter Teacher ID: ");
                                                        int tid = sc.nextInt();

                                                        // Show subjects
                                                        viewSubjects();
                                                        System.out.print("Enter Subject ID to assign: ");
                                                        int subId = sc.nextInt();

                                                        // Save assignment
                                                        String assignSQL = "INSERT INTO tbl_connect (u_id, s_id) VALUES (?, ?)";
                                                        conf.addRecord(assignSQL, tid, subId);

                                                        System.out.println("Subject assigned successfully!");
                                                        break;

                                                    // -------- VIEW SUBJECTS --------
                                                    case 2:
                                                        viewSubjects();
                                                        break;

                                                    // -------- ADD SUBJECT --------
                                                    case 3:
                                                        System.out.print("Enter subject code: ");
                                                        String scode = sc.nextLine();
                                                        System.out.print("Enter subject name: ");
                                                        String sname = sc.nextLine();
                                                        System.out.print("Enter units: ");
                                                        int units = sc.nextInt();
                                                        sc.nextLine();
                                                        System.out.print("Enter year level: ");
                                                        String y = sc.nextLine();
                                                        System.out.print("Enter semester: ");
                                                        String sem = sc.nextLine();
                                                       

                                                        String insertSub =
                                                                "INSERT INTO tbl_subjects (s_code, s_name, units, y_level, sem) VALUES (?, ?, ?, ?, ?)";
                                                        conf.addRecord(insertSub, scode, sname, units, y, sem);
                                                        System.out.println("Subject Added!");
                                                        break;

                                                    // -------- UPDATE SUBJECT --------
                                                    case 4:
                                                        viewSubjects();
                                                        System.out.print("Enter Subject ID to Update: ");
                                                        int sid = sc.nextInt();
                                                        sc.nextLine();

                                                        System.out.print("Enter new subject name: ");
                                                        String ns = sc.nextLine();
                                                        System.out.print("Enter new units: ");
                                                        int nu = sc.nextInt();
                                                        sc.nextLine();
                                                        System.out.print("Enter new year level: ");
                                                        String ny = sc.nextLine();
                                                        System.out.print("Enter new semester: ");
                                                        String nsem = sc.nextLine();
                                                        System.out.print("Enter new status: ");
                                                        String nstat = sc.nextLine();

                                                        String updateSub =
                                                                "UPDATE tbl_subjects SET s_name = ?, units = ?, y_level = ?, sem = ?, status = ? WHERE s_id = ?";
                                                        conf.updateRecord(updateSub, ns, nu, ny, nsem, nstat, sid);
                                                        System.out.println("Subject Updated!");
                                                        break;

                                                    // -------- DELETE SUBJECT --------
                                                    case 5:
                                                        viewSubjects();
                                                        System.out.print("Enter Subject ID to Delete: ");
                                                        int dsid = sc.nextInt();
                                                        String deleteSub = "DELETE FROM tbl_subjects WHERE s_id = ?";
                                                        conf.updateRecord(deleteSub, dsid);
                                                        System.out.println("Subject Deleted!");
                                                        break;

                                                    // -------- BACK --------
                                                    case 6:
                                                        subjectMenu = false;
                                                        break;
                                                }
                                            }
                                            break;

                                        // ======================= MANAGE GRADES =======================
                                        case 3:
                                            boolean gradeMenu = true;

                                            while (gradeMenu) {
                                                System.out.println("\n===== MANAGE GRADES =====");
                                                System.out.println("1. View Grades");
                                                System.out.println("2. Add Grades");
                                                System.out.println("3. Update Grades");
                                                System.out.println("4. Delete Grades");
                                                System.out.println("5. Back");
                                                System.out.print("Choose: ");
                                                int gOption = sc.nextInt();
                                                sc.nextLine();

                                                switch (gOption) {

                                                    case 1:
                                                        viewGrades();
                                                        break;

                                                    case 2:
                                                        System.out.print("Enter student user ID: ");
                                                        int guid = sc.nextInt();
                                                        System.out.print("Enter subject ID: ");
                                                        int gsid = sc.nextInt();
                                                        System.out.print("Enter prelim: ");
                                                        double pre = sc.nextDouble();
                                                        System.out.print("Enter midterm: ");
                                                        double mid = sc.nextDouble();
                                                        System.out.print("Enter pre-final: ");
                                                        double pf = sc.nextDouble();
                                                        System.out.print("Enter final: ");
                                                        double fin = sc.nextDouble();
                                                        sc.nextLine();
                                                        System.out.print("Enter remarks: ");
                                                        String rem = sc.nextLine();

                                                        String insGrade =
                                                                "INSERT INTO tbl_grades(u_id, s_id, prelim, midterm, prefi, final, remarks) VALUES(?,?,?,?,?,?,?)";
                                                        conf.addRecord(insGrade, guid, gsid, pre, mid, pf, fin, rem);
                                                        System.out.println("Grades Added!");
                                                        break;

                                                    case 3:
                                                        viewGrades();
                                                        System.out.print("Enter Grade ID to Update: ");
                                                        int gid = sc.nextInt();
                                                        System.out.print("Enter new prelim: ");
                                                        double npre = sc.nextDouble();
                                                        System.out.print("Enter new midterm: ");
                                                        double nmid = sc.nextDouble();
                                                        System.out.print("Enter new pre-final: ");
                                                        double npf = sc.nextDouble();
                                                        System.out.print("Enter new final: ");
                                                        double nfin = sc.nextDouble();
                                                        sc.nextLine();
                                                        System.out.print("Enter new remarks: ");
                                                        String nrem = sc.nextLine();

                                                        String updGrades =
                                                                "UPDATE tbl_grades SET prelim=?, midterm=?, prefi=?, final=?, remarks=? WHERE g_id=?";
                                                        conf.updateRecord(updGrades, npre, nmid, npf, nfin, nrem, gid);
                                                        System.out.println("Grades Updated!");
                                                        break;

                                                    case 4:
                                                        viewGrades();
                                                        System.out.print("Enter Grade ID to Delete: ");
                                                        int delG = sc.nextInt();
                                                        String deleteG = "DELETE FROM tbl_grades WHERE g_id=?";
                                                        conf.updateRecord(deleteG, delG);
                                                        System.out.println("Grades Deleted!");
                                                        break;

                                                    case 5:
                                                        gradeMenu = false;
                                                        break;
                                                }
                                            }
                                            break;

                                        // ------ VIEW USERS ------
                                        case 4:
                                            viewUsers();
                                            break;

                                        // ------ UPDATE USERS ------
                                        case 5:
                                            viewUsers();
                                            System.out.print("Enter ID to Update: ");
                                            int id = sc.nextInt();
                                            sc.nextLine();

                                            System.out.print("Enter new name: ");
                                            String newName = sc.nextLine();
                                            System.out.print("Enter new email: ");
                                            String newEmail = sc.nextLine();
                                            System.out.print("Enter new type (1 = Registrar, 2 = Teacher, 3 = Student): ");
                                            int newType = sc.nextInt();
                                            sc.nextLine();
                                            String tpe = (newType == 1) ? "Registrar"
                                                    : (newType == 2) ? "Teacher"
                                                    : "Student";
                                            System.out.print("Enter new password: ");
                                            String newPass = sc.nextLine();
                                            String hashedNewPass = config.hashPassword(newPass);

                                            String updateSql =
                                                    "UPDATE tbl_users SET u_name = ?, u_email = ?, u_type = ?, u_status = ?, u_pass = ? WHERE u_id = ?";
                                            conf.updateRecord(updateSql, newName, newEmail, tpe, "Approved", hashedNewPass, id);
                                            break;

                                        // ------ DELETE USERS ------
                                        case 6:
                                            viewUsers();
                                            System.out.print("Enter ID to Delete: ");
                                            int delId = sc.nextInt();
                                            String deleteSql = "DELETE FROM tbl_users WHERE u_id = ?";
                                            conf.updateRecord(deleteSql, delId);
                                            break;

                                        // ------ LOGOUT ------
                                        case 7:
                                            System.out.println("Logging out...");
                                            adminLoggedIn = false;
                                            break;

                                        default:
                                            System.out.println("Invalid option.");
                                    }
                                }
                                break;
                            }

                            // ====================== TEACHER DASHBOARD ======================
                            else if (type.equals("Teacher")) {
                                int teacherId = Integer.parseInt(user.get("u_id").toString());
                                String teacherName = user.get("u_name").toString();
                                teacher.displayTeacherDashboard(teacherId, teacherName);
                                loginSuccess = true;
                                break;
                            }

                            // ====================== STUDENT DASHBOARD ======================
                            else if (type.equals("Student")) {
                                int studentId = Integer.parseInt(user.get("u_id").toString());
                                String studentName = user.get("u_name").toString();
                                student.displayStudentDashboard(studentId, studentName);
                                loginSuccess = true;
                                break;
                            }
                        }
                    }
                    break;

                // ------------------------------- REGISTER -------------------------------
                case 2:
                    System.out.print("Enter user name: ");
                    String name = sc.next();
                    System.out.print("Enter user email: ");
                    String email = sc.next();
                    System.out.print("Enter user Type (1 = Registrar, 2 = Teacher, 3 = Student): ");
                    int typeReg = sc.nextInt();
                    String tp = (typeReg == 1) ? "Registrar"
                            : (typeReg == 2) ? "Teacher"
                            : "Student";
                    System.out.print("Enter Password: ");
                    String pass = sc.next();
                    String hashedPass = config.hashPassword(pass);

                    String sql2 =
                            "INSERT INTO tbl_users (u_name, u_email, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?)";
                    conf.addRecord(sql2, name, email, tp, "Pending", hashedPass);

                    System.out.println("Registration successful! Waiting for approval.");
                    break;

                // ------------------------------- EXIT -------------------------------
                case 3:
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

            System.out.print("Do you want to continue? (Y/N): ");
            cont = sc.next().charAt(0);

        } while (cont == 'Y' || cont == 'y');

        System.out.println("Thank you! Program ended.");
    }
}
