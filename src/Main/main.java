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
                case 1:
                    System.out.print("Enter email: ");
                    String em = sc.next();
                    System.out.print("Enter Password: ");
                    String pas = sc.next();

                    // ✅ Hash password before login check
                    String hashedLoginPass = config.hashPassword(pas);

                    int loginAttempts = 0;
                    boolean loginSuccess = false;

                    while (loginAttempts < 3 && !loginSuccess) {
                        String qry = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";
                        java.util.List<java.util.Map<String, Object>> result = conf.fetchRecords(qry, em, hashedLoginPass);

                        if (result.isEmpty()) {
                            loginAttempts++;
                            System.out.println("INVALID CREDENTIALS. Attempt " + loginAttempts + " of 3");

                            if (loginAttempts == 3) {
                                System.out.println("Too many failed attempts. Exiting program.");
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
                                System.out.println("Account is Pending. Contact the Admin.");
                                break;
                            }

                            System.out.println("LOGIN SUCCESS!");

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
                                        case 1:
                                            viewUsers();
                                            System.out.print("Enter ID to Approve: ");
                                            int ids = sc.nextInt();
                                            String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
                                            conf.updateRecord(sql, "Approved", ids);
                                            break;

                                        case 2:
                                            System.out.println("[Manage Subjects Section]");
                                            break;

                                        case 3:
                                            System.out.println("[Manage Grades Section]");
                                            break;

                                        case 4:
                                            viewUsers();
                                            break;

                                        case 5:
                                            viewUsers();
                                            System.out.print("Enter ID to Update: ");
                                            int id = sc.nextInt();
                                            sc.nextLine();

                                            System.out.print("Enter new name: ");
                                            String newName = sc.nextLine();

                                            System.out.print("Enter new email: ");
                                            String newEmail = sc.nextLine();

                                            System.out.print("Enter new type (1. Registrar / 2. Teacher / 3. Student): ");
                                            int newType = sc.nextInt();
                                            sc.nextLine();

                                            String tpe = (newType == 1) ? "Registrar" : (newType == 2) ? "Teacher" : "Student";

                                            System.out.print("Enter new password: ");
                                            String newPass = sc.nextLine();

                                            // ✅ Hash password on update
                                            String hashedNewPass = config.hashPassword(newPass);

                                            String updateSql = "UPDATE tbl_users SET u_name = ?, u_email = ?, u_type = ?, u_status = ?, u_pass = ? WHERE u_id = ?";
                                            conf.updateRecord(updateSql, newName, newEmail, tpe, "Approved", hashedNewPass, id);
                                            break;

                                        case 6:
                                            viewUsers();
                                            System.out.print("Enter ID to Delete: ");
                                            int delId = sc.nextInt();
                                            String deleteSql = "DELETE FROM tbl_users WHERE u_id = ?";
                                            conf.updateRecord(deleteSql, delId);
                                            break;

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

                            loginSuccess = true;
                            System.out.println("Logging out...");
                        }
                    }
                    break;

                case 2:
                    System.out.print("Enter user name: ");
                    String name = sc.next();
                    System.out.print("Enter user email: ");
                    String email = sc.next();

                    System.out.print("Enter user Type (1 - Registrar / 2 -Teacher / 3. Student ): ");
                    int type = sc.nextInt();
                    String tp = (type == 1) ? "Registrar" : (type == 2) ? "Teacher" : "Student";

                    System.out.print("Enter Password: ");
                    String pass = sc.next();

                    // ✅ Hash password before storing
                    String hashedPass = config.hashPassword(pass);

                    String sql2 = "INSERT INTO tbl_users (u_name, u_email, u_type, u_status, u_pass) VALUES (?, ?, ?, ?, ?)";
                    conf.addRecord(sql2, name, email, tp, "Pending", hashedPass);

                    System.out.println("Registration successful! Waiting for approval.");
                    break;

                case 3:
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

            // ✅ Show this ONLY after logout / finish action.
            System.out.print("Do you want to continue? (Y/N): ");
            cont = sc.next().charAt(0);

        } while (cont == 'Y' || cont == 'y');

        System.out.println("Thank you! Program ended.");
    }
}
