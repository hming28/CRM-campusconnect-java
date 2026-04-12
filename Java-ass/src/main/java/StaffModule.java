import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StaffModule {

    /** All demo staff accounts — same password for coursework demo. */
    private static final Staff[] STAFF_ACCOUNTS = {
            new ITStaff(          "ST2001", "Adam Lee",      "Staff@123"),
            new HostelStaff(      "ST2002", "Sarah Tan",     "Staff@123"),
            new AcademicStaff(    "ST2003", "Daniel Wong",   "Staff@123"),
            new FacilityStaff(   "ST2004", "Aisha Rahman",  "Staff@123"),
            new FoodBeverageStaff("ST2005", "Kevin Lim",     "Staff@123"),
    };

    /** Auto-assign: ticket category → primary staff for that domain. */
    public static String getStaffIdForCategory(String category) {
        if (category == null) return "ST2001";
        switch (category.trim()) {
            case "IT":                 return "ST2001";
            case "Hostel":             return "ST2002";
            case "Academic":           return "ST2003";
            case "Facility":         return "ST2004";
            case "Food & Beverage":  return "ST2005";
            default:                   return "ST2001";
        }
    }

    public static String getStaffDisplayName(String staffId) {
        if (staffId == null) return "";
        for (Staff s : STAFF_ACCOUNTS) {
            if (s.getStaffId().equalsIgnoreCase(staffId)) return s.getName();
        }
        return staffId;
    }

    /**
     * Manager assigns a ticket to another support staff member.
     * Shows numbered list (ID, name, department) — no need to type staff IDs by hand.
     */
    public static void printStaffAssignmentMenu() {
        System.out.println("\n--- Select staff to handle this ticket ---");
        for (int i = 0; i < STAFF_ACCOUNTS.length; i++) {
            Staff s = STAFF_ACCOUNTS[i];
            System.out.printf("  %d. %s | %s | %s%n",
                    i + 1, s.getStaffId(), s.getName(), s.getDepartment());
        }
        System.out.println("------------------------------------------");
    }

    public static String selectStaffIdFromMenu(Scanner scanner) {
        while (true) {
            printStaffAssignmentMenu();
            System.out.print("Choice (1-5): ");
            String line = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(line);
                if (n >= 1 && n <= STAFF_ACCOUNTS.length) {
                    return STAFF_ACCOUNTS[n - 1].getStaffId();
                }
            } catch (NumberFormatException ignored) { }
            System.out.println("  X Please enter a number from 1 to " + STAFF_ACCOUNTS.length + ".\n");
        }
    }

    /** Legacy tickets without Handled by — assign from category once, then save. */
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
    // ENTRY POINT
    // ---------------------------------------------------------------
    public static void login(Scanner scanner) {
        System.out.println("\n========== STAFF LOGIN ==========");

        System.out.print("Staff ID  : ");
        String id = scanner.nextLine().trim();

        System.out.print("Password  : ");
        String password = scanner.nextLine().trim();

        Staff staff = authenticate(id, password);
        if (staff == null) {
            System.out.println("\n  X Invalid Staff ID or Password.\n");
            return;
        }

        System.out.println("\n--------------------------------");
        System.out.println("Login Successful!");
        System.out.println("Welcome, " + staff.getName() + " (" + staff.getDepartment() + ")");
        System.out.println("--------------------------------\n");

        showStaffMenu(scanner, staff);
    }

    private static Staff authenticate(String id, String password) {
        for (Staff s : STAFF_ACCOUNTS) {
            if (s.getStaffId().equalsIgnoreCase(id.trim())
                    && s.getPassword().equals(password)) {
                return s;
            }
        }
        return null;
    }

    // ---------------------------------------------------------------
    // STAFF MENU
    // ---------------------------------------------------------------
    private static void showStaffMenu(Scanner scanner, Staff staff) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("========== STAFF MENU ==========");
            System.out.println("1. View Profile");
            System.out.println("2. View Pending Tickets");
            System.out.println("3. Search / Filter Tickets");
            System.out.println("4. Respond to Ticket");
            System.out.println("5. View Ticket History");
            System.out.println("6. Request Reassignment");
            System.out.println("7. Logout");
            System.out.println("=================================");
            System.out.print("Enter Choice: ");
            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1": viewProfile(staff);                          break;
                case "2": viewPendingTickets(staff);                  break;
                case "3": searchFilterTickets(scanner);                break;
                case "4": respondToTicket(scanner, staff);             break;
                case "5": viewTicketHistory(staff);                    break;
                case "6": requestReassignment(scanner, staff);         break;
                case "7":
                    System.out.println("Logging out...\n");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("  X Invalid option. Please select 1–7.\n");
            }
        }
    }

    // ---------------------------------------------------------------
    // 1. VIEW PROFILE
    // ---------------------------------------------------------------
    private static void viewProfile(Staff staff) {
        // Polymorphism: same method call, ITStaff/HostelStaff etc. show their role
        staff.displayProfile();
    }

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
            System.out.println("No tickets assigned to you in Pending / In Progress.\n");
            return;
        }

        System.out.println("----------- MY ASSIGNED TICKETS -----------");
        System.out.println("(sorted: High → Medium → Low)");
        for (Ticket t : pending) {
            System.out.println(formatTicketSummaryLine(t));
        }
        System.out.println("-------------------------------------------\n");
    }

    // ---------------------------------------------------------------
    // 3. SEARCH / FILTER TICKETS
    // ---------------------------------------------------------------
    private static void searchFilterTickets(Scanner scanner) {
        System.out.println("------- SEARCH / FILTER -------");
        System.out.println("1. Filter by Status");
        System.out.println("2. Filter by Category");
        System.out.println("3. Search by Keyword");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        System.out.println();

        List<Ticket> results;

        switch (choice) {
            case "1": {
                System.out.println("Status:");
                System.out.println("1. Pending");
                System.out.println("2. In Progress");
                System.out.println("3. Resolved");
                System.out.println("4. Closed");
                System.out.println("5. Reassignment Requested");
                System.out.print("Choice: ");
                String statusChoice = scanner.nextLine().trim();
                String[] statuses = { "Pending", "In Progress", "Resolved",
                                      "Closed", "Reassignment Requested" };
                int idx;
                try { idx = Integer.parseInt(statusChoice) - 1; }
                catch (NumberFormatException e) { idx = -1; }
                if (idx < 0 || idx >= statuses.length) {
                    System.out.println("  X Invalid choice.\n");
                    return;
                }
                String filterStatus = statuses[idx];
                results = TicketStore.all.stream()
                        .filter(t -> filterStatus.equalsIgnoreCase(t.getStatus()))
                        .collect(Collectors.toList());
                break;
            }
            case "2": {
                System.out.println("Category:");
                System.out.println("  1. IT");
                System.out.println("  2. Hostel");
                System.out.println("  3. Academic");
                System.out.println("  4. Facility");
                System.out.println("  5. Food & Beverage");
                System.out.print("Choice: ");
                String catChoice = scanner.nextLine().trim();
                String[] cats = { "IT", "Hostel", "Academic", "Facility", "Food & Beverage" };
                int idx;
                try { idx = Integer.parseInt(catChoice) - 1; }
                catch (NumberFormatException e) { idx = -1; }
                if (idx < 0 || idx >= cats.length) {
                    System.out.println("  X Invalid choice.\n");
                    return;
                }
                String filterCat = cats[idx];
                results = TicketStore.all.stream()
                        .filter(t -> filterCat.equalsIgnoreCase(t.getCategory()))
                        .collect(Collectors.toList());
                break;
            }
            case "3": {
                System.out.print("Keyword: ");
                String keyword = scanner.nextLine().trim().toLowerCase();
                results = TicketStore.all.stream()
                        .filter(t -> t.getTitle().toLowerCase().contains(keyword)
                                  || (t.getDescription() != null
                                      && t.getDescription().toLowerCase().contains(keyword)))
                        .collect(Collectors.toList());
                break;
            }
            default:
                System.out.println("  X Invalid choice.\n");
                return;
        }

        System.out.println();
        if (results.isEmpty()) {
            System.out.println("No tickets matched.\n");
            return;
        }
        results.sort(Comparator.comparingInt(StaffModule::priorityKey));
        System.out.println("---------- SEARCH RESULTS ----------");
        printTicketSummaryList(results);
        System.out.println();
    }

    // ---------------------------------------------------------------
    // 4. RESPOND TO TICKET
    // ---------------------------------------------------------------
    private static void respondToTicket(Scanner scanner, Staff staff) {
        System.out.println("-------- RESPOND TO TICKET --------");
        System.out.print("Enter Ticket ID: ");
        String ticketId = scanner.nextLine().trim().toUpperCase();

        Ticket ticket = findById(ticketId);
        if (ticket == null) {
            System.out.println("  X Ticket not found.\n");
            return;
        }
        if ("Closed".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("  X Ticket is already closed.\n");
            return;
        }

        String assigned = ticket.getHandledBy();
        if (assigned != null && !assigned.trim().isEmpty()
                && !assigned.equalsIgnoreCase(staff.getStaffId())) {
            System.out.println("  X This ticket is assigned to "
                    + getStaffDisplayName(assigned) + " (" + assigned + ").\n");
            return;
        }

        System.out.println("Title       : " + ticket.getTitle());
        System.out.println("Student     : " + ticket.getStudentId());
        System.out.println("Description : " + ticket.getDescription());
        System.out.println("Current Status: " + ticket.getStatus());

        System.out.println("\nEnter Response:");
        System.out.print("> ");
        String response = scanner.nextLine().trim();

        System.out.println("\nSelect Status:");
        System.out.println("1. In Progress");
        System.out.println("2. Resolved");
        System.out.print("Choice: ");
        String statusChoice = scanner.nextLine().trim();

        String newStatus;
        switch (statusChoice) {
            case "1": newStatus = "In Progress"; break;
            case "2": newStatus = "Resolved";    break;
            default:
                System.out.println("  X Invalid choice. Status not changed.\n");
                return;
        }

        ticket.setResponse(response);
        ticket.setStatus(newStatus);
        ticket.setHandledBy(staff.getStaffId());
        ticket.setReassignmentReason(null);
        if ("Resolved".equals(newStatus)) {
            ticket.setResolvedDate(LocalDate.now());
        }
        TicketStore.saveToFile();

        System.out.println("\nTicket Updated Successfully.");
        System.out.println("Ticket ID : " + ticket.getTicketId());
        System.out.println("New Status: " + ticket.getStatus());
        System.out.println("-----------------------------------\n");
    }

    // ---------------------------------------------------------------
    // 5. VIEW TICKET HISTORY (tickets this staff handled)
    // ---------------------------------------------------------------
    private static void viewTicketHistory(Staff staff) {
        List<Ticket> handled = TicketStore.all.stream()
                .filter(t -> staff.getStaffId().equals(t.getHandledBy()))
                .collect(Collectors.toList());

        if (handled.isEmpty()) {
            System.out.println("You have not handled any tickets yet.\n");
            return;
        }

        System.out.println("--------- MY TICKET HISTORY ---------");
        for (Ticket t : handled) {
            System.out.println("Ticket ID  : " + t.getTicketId());
            System.out.println("Title      : " + t.getTitle());
            System.out.println("Category   : " + t.getCategory());
            System.out.println("Priority   : " + t.getPriority());
            System.out.println("Student    : " + t.getStudentId());
            System.out.println("Status     : " + t.getStatus());
            System.out.println("Response   : " + t.getResponse());
            if (t.getRating() != null) {
                System.out.println("Rating     : " + t.getRating() + "/5");
                System.out.println("Feedback   : " + t.getFeedback());
            }
            System.out.println("--------------------------------------");
        }
        System.out.println();
    }

    // ---------------------------------------------------------------
    // 6. REQUEST REASSIGNMENT
    // ---------------------------------------------------------------
    private static void requestReassignment(Scanner scanner, Staff staff) {
        System.out.println("------ REQUEST REASSIGNMENT ------");
        System.out.print("Enter Ticket ID: ");
        String ticketId = scanner.nextLine().trim().toUpperCase();

        Ticket ticket = findById(ticketId);
        if (ticket == null) {
            System.out.println("  X Ticket not found.\n");
            return;
        }
        if ("Closed".equalsIgnoreCase(ticket.getStatus())
                || "Reassignment Requested".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("  X Cannot request reassignment for this ticket.\n");
            return;
        }

        String assigned = ticket.getHandledBy();
        if (assigned != null && !assigned.trim().isEmpty()
                && !assigned.equalsIgnoreCase(staff.getStaffId())) {
            System.out.println("  X Only the assigned staff member can request reassignment.\n");
            return;
        }

        if ("In Progress".equalsIgnoreCase(ticket.getStatus())
                || "Resolved".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("  X Cannot request reassignment — ticket is already being handled or resolved.\n");
            return;
        }

        String resp = ticket.getResponse();
        if (resp != null && !resp.trim().isEmpty()) {
            System.out.println("  X Cannot request reassignment — you have already submitted a response.\n");
            return;
        }

        System.out.println("Reason:");
        System.out.print("> ");
        String reason = scanner.nextLine().trim();

        ticket.setReassignmentReason(reason);
        ticket.setStatus("Reassignment Requested");
        ticket.setHandledBy(staff.getStaffId());
        TicketStore.saveToFile();

        System.out.println("\nReassignment request sent to Manager.");
        System.out.println("Ticket ID : " + ticket.getTicketId());
        System.out.println("Reason    : " + reason);
        System.out.println("----------------------------------\n");
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------
    private static Ticket findById(String ticketId) {
        for (Ticket t : TicketStore.all) {
            if (t.getTicketId().equalsIgnoreCase(ticketId)) {
                return t;
            }
        }
        return null;
    }

    private static void printTicketSummaryList(List<Ticket> tickets) {
        for (Ticket t : tickets) {
            System.out.println(formatTicketSummaryLine(t));
        }
    }

    /** High = 0 … Low = 2 so ascending sort puts High first. */
    private static int priorityKey(Ticket t) {
        if (t == null || t.getPriority() == null) return 9;
        switch (t.getPriority()) {
            case "High":   return 0;
            case "Medium": return 1;
            case "Low":    return 2;
            default:       return 3;
        }
    }

    /** Single compact line — no wide printf columns (avoids huge gaps). */
    private static String formatTicketSummaryLine(Ticket t) {
        String title = t.getTitle() == null ? "" : t.getTitle();
        if (title.length() > 32) title = title.substring(0, 29) + "...";
        return t.getTicketId()
                + " | " + title
                + " | " + t.getStatus()
                + " | " + t.getPriority()
                + " | Student: " + t.getStudentId();
    }
}
