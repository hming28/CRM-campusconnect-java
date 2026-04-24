/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

import com.mycompany.main.student.LoginModule;
import com.mycompany.main.ticket.TicketStore;
import com.mycompany.main.manager.ManagerModule;
import com.mycompany.main.student.StudentStore;
import com.mycompany.main.staff.StaffModule;
import com.mycompany.main.applicant.ApplicantModule;
import java.util.Scanner;

/**
 * Entry point for CampusConnect CRM system.
 * Implements UIConstants so the main menu shares the same divider
 * constants as every other module — no local redefinition needed.
 */
public class Main implements UIConstants {

    public static void main(String[] args) {
        ApplicantModule.loadFromFile();
        StudentStore.loadFromFile();
        TicketStore.loadFromFile();
        StaffModule.ensurePrimaryAssignment();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            displayMainMenu();
            boolean validInput = false;
            while (!validInput) {
                System.out.print(">> Select Option: ");
                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1": validInput = true; ApplicantModule.register(scanner);            break;
                    case "2": validInput = true; ApplicantModule.checkApplicationStatus(scanner); break;
                    case "3": validInput = true; LoginModule.login(scanner);                    break;
                    case "4": validInput = true; StaffModule.login(scanner);                    break;
                    case "5": validInput = true; ManagerModule.login(scanner);                  break;
                    case "0":
                        validInput = true;
                        System.out.println("\nThank you for using CampusConnect. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("  [X] Invalid option. Please select 0 to 5 only.");
                }
            }
        }
        scanner.close();
    }

    public static void displayMainMenu() {
        System.out.println(W);
        System.out.println("                                            CAMPUSCONNECT SYSTEM");
        System.out.println(W);
        System.out.println("  1. Applicant Registration");
        System.out.println("  2. Check Application Status");
        System.out.println("  3. Student Login");
        System.out.println("  4. Staff Login");
        System.out.println("  5. Manager Login");
        System.out.println("  0. Exit");
        System.out.println(W);
    }
}
