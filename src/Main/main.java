/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Config.config;
import java.util.Scanner;

/**
 *
 * @author Dell
 */
public class main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        config conf = new config();
        
        System.out.println("WELCOME TO MY ACADEMIC TRACKING SYSTEM!");
        System.out.println("1. Users");
        System.out.println("2. Courses");
        System.out.println("3. Records");
        System.out.print("Enter Choice: ");
        int choice = sc.nextInt();
        
        switch(choice){
            case 1:
                System.out.println("1. Add Users");
                System.out.println("2. View Users");
                System.out.println("3. Update Users");
                System.out.println("4. Delete Users");
                System.out.println("5. Back");
                System.out.print("Enter Action: ");
                int action = sc.nextInt();
                switch(action){
                    case 1: 
                        System.out.println("Enter name: ");
                        String name = sc.next();
                        System.out.println("Enter email: ");
                        String email = sc.next();
                        System.out.println("Enter User type (Teacher, Student, Admin): ");
                        String type = sc.next();
                        System.out.println("Enter Pass: ");
                        String pass = sc.next();
                        
                        String sql = "INSERT INTO tbl_users (u_name, u_email, u_type, u_pass) VALUES"
                                + "(?, ?, ?, ?)";
                        
                        conf.addRecord(sql, name, email, type, pass);
                        
                        
                        
                        break;
                        
                }
                
                break;
                
        }
    
    }
       
        
}
