/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

import java.util.List;
import java.util.Scanner;

/**
 * Handles student login, menu navigation, ticket submission, and password change.
 * Implements UIConstants to use shared divider constants from one source.
 */
public class LoginModule implements UIConstants {

    // ---------------------------------------------------------------
    // STUDENT LOGIN
    // ---------------------------------------------------------------
    public static void login(Scanner scanner) {
        System.out.println("\n" + W);
        System.out.println("                                               STUDENT LOGIN");
        System.out.println(W);

        System.out.print("  Student ID : ");
        String studentId = scanner.nextLine().trim();

        System.out.print("  Password   : ");
        String password = scanner.nextLine().trim();

        Student student = authenticate(studentId, password);
        if (student == null) {
            System.out.println("  [X] Invalid Student ID or Password.\n");
            return;
        }

        if (student.isFirstLogin()) {
            handleFirstLogin(scanner, student);
        } else {
            System.out.println(D);
            System.out.println("  Welcome, " + student.getName() + "!");
            System.out.println(D);
        }

        showStudentMenu(scanner, student);
    }

    private static Student authenticate(String id, String password) {
        Student student = StudentStore.findById(id);
        if (student != null && student.getPassword().equals(password)) return student;
        return null;
    }

    // ---------------------------------------------------------------
    // FIRST LOGIN — forced password change
    // ---------------------------------------------------------------
    private static void handleFirstLogin(Scanner scanner, Student student) {
        System.out.println("\n  First Login Detected. You must change your password.\n");

        while (true) {
            System.out.print("  Enter New Password   : ");
            String newPassword = scanner.nextLine().trim();
            if (!Validator.isValidPassword(newPassword)) {
                Validator.printPasswordError();
                continue;
            }

            System.out.print("  Confirm New Password : ");
            String confirmPassword = scanner.nextLine().trim();
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("  [X] Passwords do not match. Please try again.\n");
                continue;
            }

            student.setPassword(newPassword);
            student.setFirstLogin(false);
            StudentStore.saveToFile();
            System.out.println("\n  Password Changed Successfully!");
            System.out.println(D);
            System.out.println("  Welcome, " + student.getName() + "!");
            System.out.println(D + "\n");
            break;
        }
    }

    // ---------------------------------------------------------------
    // STUDENT MENU
    // ---------------------------------------------------------------
    private static void showStudentMenu(Scanner scanner, Student student) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println(W);
            System.out.println("                                                STUDENT MENU");
            System.out.println(W);
            System.out.println("  1. Submit Support Ticket");
            System.out.println("  2. View Ticket History");
            System.out.println("  3. Change Password");
            System.out.println("  4. View Profile");
            System.out.println("  5. Logout");
            System.out.println(W);

            boolean valid = false;
            while (!valid) {
                System.out.print(">> Select Option: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1": valid = true; submitTicket(scanner, student);   break;
                    case "2": valid = true; viewTicketHistory(scanner, student); break;
                    case "3": valid = true; changePassword(scanner, student); break;
                    case "4": valid = true; viewProfile(student);             break;
                    case "5":
                        valid = true;
                        System.out.println("  Logging out...\n");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("  [X] Invalid option. Please select 1 to 5 only.");
                }
            }
        }
    }

    private static void viewProfile(Student student) { student.displayProfile(); }

    // ---------------------------------------------------------------
    // CHANGE PASSWORD  
    // ---------------------------------------------------------------
    private static void changePassword(Scanner scanner, Student student) {
        System.out.println(W);
        System.out.println("                                              CHANGE PASSWORD");
        System.out.println(W);
        System.out.println("  (Enter 0 as current password to go back)");


        System.out.print("Current Password : ");
        String current = scanner.nextLine().trim();
        if (current.equals("0")) { System.out.println("  Returning to student menu.\n"); return; }

        if (!student.getPassword().equals(current)) {
            System.out.println("  [X] Current password is incorrect.\n");
            return;
        }

        while (true) {
            System.out.print("New Password     : ");
            String newPassword = scanner.nextLine().trim(); 
            if (newPassword.equals("0")) { System.out.println("  Returning to student menu.\n"); return; }
            if (!Validator.isValidPassword(newPassword)) {
                Validator.printPasswordError();
                continue;
            }

            System.out.print("Confirm Password : ");
            String confirm = scanner.nextLine().trim();            
            if (!newPassword.equals(confirm)) {
                System.out.println("  [X] Passwords do not match. Please try again.\n");
                continue;
            }

            student.setPassword(newPassword);
            StudentStore.saveToFile();
            System.out.println("\n  Password updated successfully.\n");
            break;
        }
    }

    // ---------------------------------------------------------------
    // SUBMIT TICKET  
    // ---------------------------------------------------------------
    private static void submitTicket(Scanner scanner, Student student) {
        System.out.println(W);
        System.out.println("                                               SUBMIT TICKET");
        System.out.println(W);
        System.out.println("  (Enter 0 at any prompt to return to student menu)");
        
        String title;
        while (true) {
            System.out.print("  Title : ");
            title = scanner.nextLine().trim();
            if (title.equals("0")) { System.out.println("  Returning to student menu.\n"); return; }
            if (Validator.isValidTitle(title)) break;
            Validator.printTitleError();
        }

        String category = selectCategory(scanner);
        if (category == null) return;   // user pressed 0

        String priority = selectPriority(scanner);
        if (priority == null) return;   // user pressed 0

        System.out.println("\n  Description (0 to cancel):");
        System.out.print("  > ");
        String description = scanner.nextLine().trim();
        if (description.equals("0")) { System.out.println("  Returning to student menu.\n"); return; }

        String ticketId = TicketStore.nextId();
        Ticket ticket = new Ticket(ticketId, student.getStudentId(), title, category, priority);
        ticket.setDescription(description);
        String assignTo = StaffModule.getStaffIdForCategory(category);
        ticket.setHandledBy(assignTo);
        student.getTickets().add(ticket);
        TicketStore.all.add(ticket);
        TicketStore.saveToFile();

        System.out.println("\n" + W);
        System.out.println("                                        TICKET CREATED SUCCESSFULLY!");
        System.out.println(W);
        System.out.printf("  %-20s : %s%n", "Ticket ID",  ticket.getTicketId());
        System.out.printf("  %-20s : %s%n", "Status",     ticket.getStatus());
        System.out.printf("  %-20s : %s (%s) --> %s%n",
                "Assigned to",
                StaffModule.getStaffDisplayName(assignTo), assignTo, category);
        System.out.println(W + "\n");
    }

    // ---------------------------------------------------------------
    // CATEGORY SELECTION 
    // ---------------------------------------------------------------
    private static String selectCategory(Scanner scanner) {
        System.out.println("\n" + D);
        System.out.println("  SELECT CATEGORY  (0 = back to student menu)");
        System.out.println(D);
        System.out.println("  1. IT");
        System.out.println("  2. Hostel");
        System.out.println("  3. Academic");
        System.out.println("  4. Facility");
        System.out.println("  5. Food & Beverage");
        System.out.println(D);

        while (true) {
            System.out.print(">> Choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "0": System.out.println("  Returning to student menu.\n"); return null;
                case "1": return "IT";
                case "2": return "Hostel";
                case "3": return "Academic";
                case "4": return "Facility";
                case "5": return "Food & Beverage";
                default:  System.out.println("  [X] Invalid choice. Please select 1 to 5 only.");
            }
        }
    }

    // ---------------------------------------------------------------
    // PRIORITY SELECTION  
    // ---------------------------------------------------------------
    private static String selectPriority(Scanner scanner) {
        System.out.println("\n" + D);
        System.out.println("  SELECT PRIORITY  (0 = back to student menu)");
        System.out.println(D);
        System.out.println("  1. Low");
        System.out.println("  2. Medium");
        System.out.println("  3. High");
        System.out.println(D);

        while (true) {
            System.out.print(">> Choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "0": System.out.println("  Returning to student menu.\n"); return null;
                case "1": return "Low";
                case "2": return "Medium";
                case "3": return "High";
                default:  System.out.println("  [X] Invalid choice. Please select 1 to 3 only.");
            }
        }
    }

    // ---------------------------------------------------------------
    // VIEW TICKET HISTORY  
    // ---------------------------------------------------------------
    private static void viewTicketHistory(Scanner scanner, Student student) {
        List<Ticket> tickets = student.getTickets();
        if (tickets.isEmpty()) {
            System.out.println("  No tickets found.\n");
            return;
        }

        // Table header — to fit "Reassignment Requested"
        System.out.println(W);
        System.out.println("                                                 MY TICKETS");
        System.out.println(W);
        System.out.printf(" | %-10s | %-30s | %-18s | %-10s | %-24s |%n",
                "Ticket ID", "Title", "Category", "Priority", "Status");
        System.out.println(" |" + D.substring(2) + "|");
        for (Ticket t : tickets) {
            System.out.printf(" | %-10s | %-30s | %-18s | %-10s | %-24s |%n",
                    t.getTicketId(),
                    truncate(t.getTitle(), 30),
                    t.getCategory(),
                    t.getPriority(),
                    t.getStatus());
        }
        System.out.println(W + "\n");

        // Detailed view per ticket
        for (Ticket t : tickets) {
            System.out.println(D);
            System.out.printf("  %-20s : %s%n", "Ticket ID",    t.getTicketId());
            System.out.printf("  %-20s : %s%n", "Title",        t.getTitle());
            System.out.printf("  %-20s : %s%n", "Category",     t.getCategory());
            System.out.printf("  %-20s : %s%n", "Priority",     t.getPriority());
            System.out.printf("  %-20s : %s%n", "Status",       t.getStatus());
            if (t.getDescription() != null && !t.getDescription().isEmpty())
                System.out.printf("  %-20s : %s%n", "Description", t.getDescription());
            if (t.getHandledBy() != null && !t.getHandledBy().isEmpty()) {
                System.out.printf("  %-20s : %s%n", "Handled By", t.getHandledBy());
                System.out.printf("  %-20s : %s%n", "Staff Name", StaffModule.getStaffDisplayName(t.getHandledBy()));
            }
            if (t.getResponse() != null && !t.getResponse().isEmpty())
                System.out.printf("  %-20s : %s%n", "Response", t.getResponse());
            if (Validator.isMeaningfulText(t.getReassignmentReason()))
                System.out.printf("  %-20s : %s%n", "Reassignment", t.getReassignmentReason());
            if (t.getCreatedDate() != null)
                System.out.printf("  %-20s : %s%n", "Created Date",  t.getCreatedDate());
            if (t.getResolvedDate() != null)
                System.out.printf("  %-20s : %s%n", "Resolved Date", t.getResolvedDate());
            if (t.getRating() != null) {
                System.out.printf("  %-20s : %d/5%n", "Your Rating", t.getRating());
                System.out.printf("  %-20s : %s%n",   "Your Remark", (t.getFeedback() != null ? t.getFeedback() : ""));
            }
            if ("Resolved".equalsIgnoreCase(t.getStatus()) && t.getRating() == null)
                handleTicketClosure(scanner, t);
        }
        System.out.println(D + "\n");
    }

    private static void handleTicketClosure(Scanner scanner, Ticket ticket) {
        System.out.print("\n  Close Ticket " + ticket.getTicketId() + "? (Y/N): ");
        String input = scanner.nextLine().trim();
        if (!input.equalsIgnoreCase("Y")) return;

        System.out.println(D);
        System.out.println("  FEEDBACK");
        System.out.println(D);

        int rating;
        while (true) {
            System.out.print("  Rating (1-5): ");
            String r = scanner.nextLine().trim();
            try {
                rating = Integer.parseInt(r);
                if (rating < 1 || rating > 5) { System.out.println("  [X] Rating must be between 1 and 5."); continue; }
                break;
            } catch (NumberFormatException e) {
                System.out.println("  [X] Please enter a number between 1 and 5.");
            }
        }

        System.out.println("  Comment:");
        System.out.print("  > ");
        String comment = scanner.nextLine().trim();

        ticket.setRating(rating);
        ticket.setFeedback(comment);
        ticket.setStatus("Closed");
        TicketStore.saveToFile();
        System.out.println("\n  Ticket Closed Successfully.");
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
