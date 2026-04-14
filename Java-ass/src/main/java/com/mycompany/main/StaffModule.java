/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Handles all staff-facing operations.
 * Implements UIConstants so divider strings come from one source.
 */
public class StaffModule implements UIConstants {

    private static final Staff[] STAFF_ACCOUNTS = {
            new ITStaff("ST2001", "Adam Lee", "Staff@123"),
            new HostelStaff("ST2002", "Sarah Tan", "Staff@123"),
            new AcademicStaff("ST2003", "Daniel Wong", "Staff@123"),
            new FacilityStaff("ST2004", "Aisha Rahman", "Staff@123"),
            new FoodBeverageStaff("ST2005", "Kevin Lim", "Staff@123"),
    };

    // ---------------------------------------------------------------
    // CATEGORY → STAFF ASSIGNMENT
    // ---------------------------------------------------------------
    public static String getStaffIdForCategory(String category) {
        if (category == null) return "ST2001";
        switch (category.trim()) {
            case "IT":              return "ST2001";
            case "Hostel":          return "ST2002";
            case "Academic":        return "ST2003";
            case "Facility":        return "ST2004";
            case "Food & Beverage": return "ST2005";
            default:                return "ST2001";
        }
    }

    public static String getStaffDisplayName(String staffId) {
        if (staffId == null) return "";
        for (Staff s : STAFF_ACCOUNTS)
            if (s.getStaffId().equalsIgnoreCase(staffId)) return s.getName();
        return staffId;
    }

    public static void printStaffAssignmentMenu() {
        System.out.println(D);
        System.out.println("  SELECT STAFF TO HANDLE THIS TICKET");
        System.out.println(D);
        for (int i = 0; i < STAFF_ACCOUNTS.length; i++) {
            Staff s = STAFF_ACCOUNTS[i];
            System.out.printf("  %d. %-8s | %-20s | %s%n",
                    i + 1, s.getStaffId(), s.getName(), s.getDepartment());
        }
        System.out.println(D);
    }

    public static String selectStaffIdFromMenu(Scanner scanner) {
        printStaffAssignmentMenu();
        while (true) {
            System.out.print(">> Choice (1-5): ");
            String line = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(line);
                if (n >= 1 && n <= STAFF_ACCOUNTS.length) return STAFF_ACCOUNTS[n - 1].getStaffId();
            } catch (NumberFormatException ignored) {}
            System.out.println("  [X] Please enter a number from 1 to " + STAFF_ACCOUNTS.length + " only.\n");
        }
    }

    public static void ensurePrimaryAssignment() {
        boolean changed = false;
        for (Ticket t : TicketStore.all) {
            if (t.getHandledBy() == null || t.getHandledBy().trim().isEmpty()) {
                t.setHandledBy(getStaffIdForCategory(t.getCategory()));
                changed = true;
            }
        }
        if (changed) TicketStore.saveToFile();
    }

    // ---------------------------------------------------------------
    // STAFF LOGIN
    // ---------------------------------------------------------------
    public static void login(Scanner scanner) {
        System.out.println("\n" + W);
        System.out.println("                                                STAFF LOGIN");
        System.out.println(W);

        System.out.print("  Staff ID  : ");
        String id = scanner.nextLine().trim();

        System.out.print("  Password  : ");
        String password = scanner.nextLine().trim();

        Staff staff = authenticate(id, password);
        if (staff == null) {
            System.out.println("  [X] Invalid Staff ID or Password.\n");
            return;
        }

        System.out.println(D);
        System.out.println("  Login Successful! Welcome, " + staff.getName()
                + " (" + staff.getDepartment() + ")");
        System.out.println(D + "\n");

        showStaffMenu(scanner, staff);
    }

    private static Staff authenticate(String id, String password) {
        for (Staff s : STAFF_ACCOUNTS)
            if (s.getStaffId().equalsIgnoreCase(id.trim()) && s.getPassword().equals(password))
                return s;
        return null;
    }

    // ---------------------------------------------------------------
    // STAFF MENU
    // ---------------------------------------------------------------
    private static void showStaffMenu(Scanner scanner, Staff staff) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println(W);
            System.out.println("                                                 STAFF MENU");
            System.out.println(W);
            System.out.println("  1. View Profile");
            System.out.println("  2. View Pending Tickets");
            System.out.println("  3. Search / Filter Tickets");
            System.out.println("  4. Respond to Ticket");
            System.out.println("  5. View Ticket History");
            System.out.println("  6. Request Reassignment");
            System.out.println("  7. Logout");
            System.out.println(W);

            boolean valid = false;
            while (!valid) {
                System.out.print(">> Enter Choice: ");
                String choice = scanner.nextLine().trim();
                System.out.println();

                switch (choice) {
                    case "1": valid = true; viewProfile(staff);                  break;
                    case "2": valid = true; viewPendingTickets(staff);            break;
                    case "3": valid = true; searchFilterTickets(scanner);         break;
                    case "4": valid = true; respondToTicket(scanner, staff);      break;
                    case "5": valid = true; viewTicketHistory(staff);             break;
                    case "6": valid = true; requestReassignment(scanner, staff);  break;
                    case "7":
                        valid = true;
                        System.out.println("  Logging out...\n");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("  [X] Invalid option. Please select 1 to 7 only.");
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // 1. VIEW PROFILE
    // ---------------------------------------------------------------
    private static void viewProfile(Staff staff) { staff.displayProfile(); }

    // ---------------------------------------------------------------
    // 2. VIEW PENDING TICKETS 
    // ---------------------------------------------------------------
    private static void viewPendingTickets(Staff staff) {
        List<Ticket> pending = TicketStore.all.stream()
                .filter(t -> staff.getStaffId().equalsIgnoreCase(t.getHandledBy()))
                .filter(t -> "Pending".equalsIgnoreCase(t.getStatus())
                          || "In Progress".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        pending.sort(Comparator.comparingInt(StaffModule::priorityKey));

        if (pending.isEmpty()) {
            System.out.println("  No tickets assigned to you in Pending / In Progress.\n");
            return;
        }

        System.out.println(W);
        System.out.println("                                            MY ASSIGNED TICKETS");
        System.out.println("                                ( Sorted from high priority to low priority )");
        System.out.println(W);
        printTicketTable(pending);
        System.out.println(W + "\n");
    }

    // ---------------------------------------------------------------
    // 3. SEARCH / FILTER  
    // ---------------------------------------------------------------
private static void searchFilterTickets(Scanner scanner) {
        while (true) {
            System.out.println(W);
            System.out.println("                                          SEARCH / FILTER TICKETS");
            System.out.println(W);
            System.out.println("  0. Back to Staff Menu");
            System.out.println("  1. Filter by Status");
            System.out.println("  2. Filter by Category");
            System.out.println("  3. Search by Keyword");
            System.out.println(W);

            System.out.print(">> Choice: ");
            String choice = scanner.nextLine().trim();
            System.out.println();

            List<Ticket> results = null;

            switch (choice) {
                case "0":
                    System.out.println("  Returning to staff menu...\n");
                    return;

                case "1": {
                    System.out.println(D);
                    System.out.println("  SELECT STATUS  (0 = back)");
                    System.out.println(D);
                    System.out.println("  1. Pending\n  2. In Progress\n  3. Resolved\n  4. Closed\n  5. Reassignment Requested");
                    System.out.println(D);

                    String[] statuses = {"Pending", "In Progress", "Resolved", "Closed", "Reassignment Requested"};
                    String filterStatus = null;
                    while (filterStatus == null) {
                        System.out.print(">> Select Status: ");
                        String sc = scanner.nextLine().trim();
                        if (sc.equals("0")) break;
                        int idx;
                        try { idx = Integer.parseInt(sc) - 1; }
                        catch (NumberFormatException e) { idx = -1; }

                        if (idx >= 0 && idx < statuses.length) filterStatus = statuses[idx];
                        else System.out.println("  [X] Invalid choice. Select 1-5.");
                    }
                    if (filterStatus == null) continue; // Go back to main search menu
                    
                    final String fs = filterStatus;
                    results = TicketStore.all.stream()
                            .filter(t -> fs.equalsIgnoreCase(t.getStatus()))
                            .collect(Collectors.toList());
                    break;
                }

                case "2": {
                    System.out.println(D);
                    System.out.println("  SELECT CATEGORY  (0 = back)");
                    System.out.println(D);
                    System.out.println("  1. IT\n  2. Hostel\n  3. Academic\n  4. Facility\n  5. Food & Beverage");
                    System.out.println(D);

                    String[] cats = {"IT", "Hostel", "Academic", "Facility", "Food & Beverage"};
                    String filterCat = null;
                    while (filterCat == null) {
                        System.out.print(">> Select Category: ");
                        String cc = scanner.nextLine().trim();
                        if (cc.equals("0")) break;
                        int idx;
                        try { idx = Integer.parseInt(cc) - 1; }
                        catch (NumberFormatException e) { idx = -1; }

                        if (idx >= 0 && idx < cats.length) filterCat = cats[idx];
                        else System.out.println("  [X] Invalid choice. Select 1-5.");
                    }
                    if (filterCat == null) continue;

                    final String fc = filterCat;
                    results = TicketStore.all.stream()
                            .filter(t -> fc.equalsIgnoreCase(t.getCategory()))
                            .collect(Collectors.toList());
                    break;
                }

                case "3": {
                    System.out.print("  Keyword (0 to cancel): ");
                    String keyword = scanner.nextLine().trim().toLowerCase();
                    if (keyword.equals("0")) continue;
                    results = TicketStore.all.stream()
                            .filter(t -> t.getTitle().toLowerCase().contains(keyword)
                                      || (t.getDescription() != null && t.getDescription().toLowerCase().contains(keyword)))
                            .collect(Collectors.toList());
                    break;
                }

                default:
                    System.out.println("  [X] Invalid choice. Please select 0 to 3 only.\n");
                    continue;
            }

            // Logic for handling the results
            if (results != null) {
                if (results.isEmpty()) {
                    System.out.println("\n  [!] No tickets matched your criteria. Try again.\n");
                } else {
                    results.sort(Comparator.comparingInt(StaffModule::priorityKey));
                    System.out.println(W);
                    System.out.println("                                             SEARCH RESULTS");
                    System.out.println(W);
                    printTicketTable(results);
                    System.out.println(W + "\n");
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // 4. RESPOND TO TICKET
    // ---------------------------------------------------------------
    private static void respondToTicket(Scanner scanner, Staff staff) {
        System.out.println(W);
        System.out.println("                                             RESPOND TO TICKET");
        System.out.println(W);
        System.out.print("  Enter Ticket ID: ");
        String ticketId = scanner.nextLine().trim().toUpperCase();

        Ticket ticket = findById(ticketId);
        if (ticket == null)                                { System.out.println("  [X] Ticket not found.\n");         return; }
        if ("Closed".equalsIgnoreCase(ticket.getStatus())) { System.out.println("  [X] Ticket is already closed.\n"); return; }

        String assigned = ticket.getHandledBy();
        if (assigned != null && !assigned.trim().isEmpty()
                && !assigned.equalsIgnoreCase(staff.getStaffId())) {
            System.out.println("  [X] This ticket is assigned to "
                    + getStaffDisplayName(assigned) + " (" + assigned + ").\n");
            return;
        }

        System.out.println(D);
        System.out.printf("  %-18s : %s%n", "Title",          ticket.getTitle());
        System.out.printf("  %-18s : %s%n", "Student",        ticket.getStudentId());
        System.out.printf("  %-18s : %s%n", "Description",    ticket.getDescription());
        System.out.printf("  %-18s : %s%n", "Current Status", ticket.getStatus());
        System.out.println(D);

        System.out.println("  Enter Response:");
        System.out.print("  > ");
        String response = scanner.nextLine().trim();

        System.out.println(D);
        System.out.println("  SELECT NEW STATUS");
        System.out.println(D);
        System.out.println("  1. In Progress");
        System.out.println("  2. Resolved");
        System.out.println(D);

        String newStatus = null;
        while (newStatus == null) {
            System.out.print(">> Choice: ");
            String sc = scanner.nextLine().trim();
            switch (sc) {
                case "1": newStatus = "In Progress"; break;
                case "2": newStatus = "Resolved";    break;
                default:  System.out.println("  [X] Invalid choice. Please select 1 to 2 only.");
            }
        }

        ticket.setResponse(response);
        ticket.setStatus(newStatus);
        ticket.setHandledBy(staff.getStaffId());
        ticket.setReassignmentReason(null);
        if ("Resolved".equals(newStatus)) ticket.setResolvedDate(LocalDate.now());
        TicketStore.saveToFile();

        System.out.println("\n" + W);
        System.out.println("                                        TICKET UPDATED SUCCESSFULLY!");
        System.out.println(W);
        System.out.printf("  %-18s : %s%n", "Ticket ID",  ticket.getTicketId());
        System.out.printf("  %-18s : %s%n", "New Status", ticket.getStatus());
        System.out.println(W + "\n");
    }

    // ---------------------------------------------------------------
    // 5. VIEW TICKET HISTORY 
    // ---------------------------------------------------------------
    private static void viewTicketHistory(Staff staff) {
        List<Ticket> handled = TicketStore.all.stream()
                .filter(t -> staff.getStaffId().equals(t.getHandledBy()))
                .collect(Collectors.toList());

        if (handled.isEmpty()) {
            System.out.println("  You have not handled any tickets yet.\n");
            return;
        }

        System.out.println(W);
        System.out.println("                                             MY TICKET HISTORY");
        System.out.println(W);
        printTicketTable(handled);
        System.out.println(W);

        for (Ticket t : handled) {
            System.out.println(D);
            System.out.printf("  %-18s : %s%n", "Ticket ID", t.getTicketId());
            System.out.printf("  %-18s : %s%n", "Title",     t.getTitle());
            System.out.printf("  %-18s : %s%n", "Category",  t.getCategory());
            System.out.printf("  %-18s : %s%n", "Priority",  t.getPriority());
            System.out.printf("  %-18s : %s%n", "Student",   t.getStudentId());
            System.out.printf("  %-18s : %s%n", "Status",    t.getStatus());
            System.out.printf("  %-18s : %s%n", "Response",  t.getResponse());
            if (t.getRating() != null) {
                System.out.printf("  %-18s : %d/5%n", "Rating",   t.getRating());
                System.out.printf("  %-18s : %s%n",   "Feedback", t.getFeedback());
            }
        }
        System.out.println(D + "\n");
    }

    // ---------------------------------------------------------------
    // 6. REQUEST REASSIGNMENT
    // ---------------------------------------------------------------
    private static void requestReassignment(Scanner scanner, Staff staff) {
        System.out.println(W);
        System.out.println("                                            REQUEST REASSIGNMENT");
        System.out.println(W);
        System.out.print("  Enter Ticket ID: ");
        String ticketId = scanner.nextLine().trim().toUpperCase();

        Ticket ticket = findById(ticketId);
        if (ticket == null) { System.out.println("  [X] Ticket not found.\n"); return; }
        if ("Closed".equalsIgnoreCase(ticket.getStatus())
                || "Reassignment Requested".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("  [X] Cannot request reassignment for this ticket.\n"); return;
        }

        String assigned = ticket.getHandledBy();
        if (assigned != null && !assigned.trim().isEmpty()
                && !assigned.equalsIgnoreCase(staff.getStaffId())) {
            System.out.println("  [X] Only the assigned staff member can request reassignment.\n"); return;
        }
        if ("In Progress".equalsIgnoreCase(ticket.getStatus())
                || "Resolved".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("  [X] Cannot request reassignment: ticket is already being handled or resolved.\n"); return;
        }
        String resp = ticket.getResponse();
        if (resp != null && !resp.trim().isEmpty()) {
            System.out.println("  [X] Cannot request reassignment: you have already submitted a response.\n"); return;
        }

        System.out.println("  Reason:");
        System.out.print("  > ");
        String reason = scanner.nextLine().trim();

        ticket.setReassignmentReason(reason);
        ticket.setStatus("Reassignment Requested");
        ticket.setHandledBy(staff.getStaffId());
        TicketStore.saveToFile();

        System.out.println("\n" + W);
        System.out.println("                                    REASSIGNMENT REQUEST SENT TO MANAGER");
        System.out.println(W);
        System.out.printf("  %-18s : %s%n", "Ticket ID", ticket.getTicketId());
        System.out.printf("  %-18s : %s%n", "Reason",    reason);
        System.out.println(W + "\n");
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------
    private static Ticket findById(String ticketId) {
        for (Ticket t : TicketStore.all)
            if (t.getTicketId().equalsIgnoreCase(ticketId)) return t;
        return null;
    }

    private static int priorityKey(Ticket t) {
        if (t == null || t.getPriority() == null) return 9;
        switch (t.getPriority()) {
            case "High":   return 0;
            case "Medium": return 1;
            case "Low":    return 2;
            default:       return 3;
        }
    }

    /**
     * Prints a uniform ticket table with Status column wide enough
     * to hold "Reassignment Requested" (22 chars) 
     */
    private static void printTicketTable(List<Ticket> tickets) {
        System.out.printf(" | %-5s | %-24s | %-12s | %-8s | %-16s | %-24s |%n",
                "ID", "Title", "Handled By", "Priority", "Category", "Status");
        System.out.println(" |" + D.substring(2) + "|");
        for (Ticket t : tickets) {
            System.out.printf(" | %-5s | %-24s | %-12s | %-8s | %-16s | %-24s |%n",
                    t.getTicketId(),
                    truncate(t.getTitle(), 24),
                    getStaffDisplayName(t.getHandledBy()),
                    t.getPriority(),
                    t.getCategory(),
                    t.getStatus());
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
