package Main;

import Config.config;
import java.util.Scanner;

public class main {

    public static void viewUsers() {
        String Query = "SELECT * FROM tbl_users";

        String[] votersHeaders = {"ID", "Name", "Email", "Type", "Status"};
        String[] votersColumns = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        config conf = new config();
        conf.viewRecords(Query, votersHeaders, votersColumns);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        config conf = new config();
        conf.connectDB();
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
                case 1:
                    System.out.print("Enter email: ");
                    String em = sc.next();
                    System.out.print("Enter Password: ");
                    String pas = sc.next();

                    int loginAttempts = 0;
                    boolean loginSuccess = false;

                    while (loginAttempts < 3 && !loginSuccess) {  
                        String qry = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";
                        java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(qry, em, pas);

                        if (result.isEmpty()) {
                            loginAttempts++;
                            System.out.println("INVALID CREDENTIALS. Attempt " + loginAttempts + " of 3.");
                            if (loginAttempts == 3) {
                                System.out.println("Too many failed attempts. Exiting program.");
                                System.exit(0); 
                            } else {
                                System.out.println("Please try again.");
                                System.out.print("Enter email: ");
                                em = sc.next(); 
                                System.out.print("Enter Password: ");
                                pas = sc.next();  
                            }
                        } else {
                            java.util.Map<String, Object> user = result.get(0);
                            String stat = user.get("u_status").toString();
                            String type = user.get("u_type").toString();
                            if (stat.equals("Pending")) {
                                System.out.println("Account is Pending, Contact the Admin!");
                                break;
                            } else {
                                System.out.println("LOGIN SUCCESS!");
                                if (type.equals("Admin")) {
                                    System.out.println("===== REGISTRAR DASHBOARD =====");

                                    boolean adminLoggedIn = true;
                                    while (adminLoggedIn) {
                                        System.out.println("\n--- ADMIN MENU ---");
                                        System.out.println("1. Manage Pending Accounts");
                                        System.out.println("2. View Users");
                                        System.out.println("3. Update Users");
                                        System.out.println("4. Delete Users");
                                        System.out.println("5. Logout");
                                        System.out.print("Enter Action: ");
                                        int action = sc.nextInt();

                                        switch (action) {
                                            case 1:
                                                viewUsers();
                                                System.out.print("Enter ID to Approve: ");
                                                int ids = sc.nextInt();

                                                String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
                                                conf.updateRecord(sql, "Approved", ids);
                                                break;

                                            case 2:
                                                viewUsers();
                                                break;

                                            case 3:
                                                viewUsers();
                                                System.out.print("Enter ID to Update: ");
                                                int id = sc.nextInt();
                                                sc.nextLine();

                                                System.out.print("Enter new name: ");
                                                String newName = sc.nextLine();

                                                System.out.print("Enter new email: ");
                                                String newEmail = sc.nextLine();
                                                
                                                while (true) {

                                                    String qy = "SELECT * FROM tbl_users WHERE u_email = ?";
                                                    java.util.List<java.util.Map<String, Object>> res = conf.fetchRecords(qy, newEmail);

                                                    if (res.isEmpty()) {
                                                        break;
                                                    } else {
                                                        System.out.print("Email already exists, Enter other Email: ");
                                                        newEmail = sc.next();
                                                    }
                                                }

                                                System.out.print("Enter new type (1. Registrar / 2. Teacher / 3. Student): ");
                                                int newType = sc.nextInt();
                                                sc.nextLine();
                                                
                                                while (newType < 1 || newType > 3) {
                                                    System.out.print("Invalid, choose between 1, 2, & 3 only: ");
                                                }
                                                String tpe = "";
                                                if (newType == 1) {
                                                    tpe = "Registrar";
                                                } else if (newType == 2) {
                                                    tpe = "Teacher";
                                                } else {
                                                    tpe = "Student";
                                                }
                                                System.out.print("Enter new password: ");
                                                String newPass = sc.nextLine();

                                                String updateSql = "UPDATE tbl_users SET u_name = ?, u_email = ?, u_type = ?, u_status = ?, u_pass = ? WHERE u_id = ?";
                                                conf.updateRecord(updateSql, newName, newEmail, tpe, "Approved", newPass, id);
                                                break;

                                            case 4:
                                                viewUsers();
                                                System.out.print("Enter ID to Delete: ");
                                                int delId = sc.nextInt();

                                                String deleteSql = "DELETE FROM tbl_users WHERE u_id = ?";
                                                conf.updateRecord(deleteSql, delId);
                                                break;

                                            case 5:
                                                System.out.println("Logging out...");
                                                adminLoggedIn = false;
                                                break;

                                            default:
                                                System.out.println("Invalid option.");
                                        }
                                    }
                                } else if (type.equals("Teacher")) {
                                    teacher.displayTeacherDashboard(user.get("u_name").toString());
                                } else if (type.equals("Student")) {
                                    student.displayStudentDashboard(user.get("u_name").toString());
                                }

                            }

                            loginSuccess = true;  
                            break;  
                        }
                    }
                    break;

                case 2:
                    System.out.print("Enter user name: ");
                    String name = sc.next();
                    System.out.print("Enter user email: ");
                    String email = sc.next();

                    while (true) {

                        String qry = "SELECT * FROM tbl_users WHERE u_email = ?";
                        java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(qry, email);

                        if (result.isEmpty()) {
                            break;
                        } else {
                            System.out.print("Email already exists, Enter other Email: ");
                            email = sc.next();
                        }
                    }

                    System.out.print("Enter user Type (1 - Registrar / 2 -Teacher / 3. Student ): ");
                    int type = sc.nextInt();
                    while (type < 1 || type > 3) {
                        System.out.print("Invalid, choose between 1, 2, & 3 only: ");
                        type = sc.nextInt();
                    }
                    String tp = "";
                    if (type == 1) {
                        tp = "Registrar";
                    } else if (type == 2) {
                        tp = "Teacher";
                    } else {
                        tp = "Student";
                    }
                    System.out.print("Enter Password: ");
                    String pass = sc.next();

                    String sql = "INSERT INTO tbl_users (u_name, u_email, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?)";
                    conf.addRecord(sql, name, email, tp, "Pending", pass);
                    break;

                case 3:
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
