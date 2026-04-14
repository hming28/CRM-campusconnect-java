/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Handles all manager-facing operations.
 * Implements UIConstants so divider strings come from one shared source.
 */
public class ManagerModule implements UIConstants {

    private static final Manager DEMO_MANAGER = new Manager(
            "MG3001", "Dr. Tan Wei Lian", "Manager@123");

    // ---------------------------------------------------------------
    // MANAGER LOGIN
    // ---------------------------------------------------------------
    public static void login(Scanner scanner) {
        System.out.println("\n" + W);
        System.out.println("                                               MANAGER LOGIN");
        System.out.println(W);

        System.out.print("  Manager ID : ");
        String id = scanner.nextLine().trim();

        System.out.print("  Password   : ");
        String password = scanner.nextLine().trim();

        Manager manager = authenticate(id, password);
        if (manager == null) {
            System.out.println("  [X] Invalid Manager ID or Password.\n");
            return;
        }

        System.out.println(D);
        System.out.println("  Login Successful!");
        System.out.println(D + "\n");

        showManagerMenu(scanner, manager);
    }

    private static Manager authenticate(String id, String password) {
        if (DEMO_MANAGER.getManagerId().equals(id) && DEMO_MANAGER.getPassword().equals(password))
            return DEMO_MANAGER;
        return null;
    }

    // ---------------------------------------------------------------
    // MANAGER MENU
    // ---------------------------------------------------------------
    private static void showManagerMenu(Scanner scanner, Manager manager) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println(W);
            System.out.println("                                                MANAGER MENU");
            System.out.println(W);
            System.out.println("  1. View Pending Applications");
            System.out.println("  2. View All Tickets");
            System.out.println("  3. Reassign Ticket");
            System.out.println("  4. View All Feedback");
            System.out.println("  5. Generate Monthly Report");
            System.out.println("  6. View All Students");
            System.out.println("  7. View Profile");
            System.out.println("  8. Logout");
            System.out.println(W);

            boolean valid = false;
            while (!valid) {
                System.out.print(">> Enter Choice: ");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1": valid = true; viewPendingApplications(scanner); break;
                    case "2": valid = true; viewAllTickets();                  break;
                    case "3": valid = true; reassignTicket(scanner);           break;
                    case "4": valid = true; viewAllFeedback();                 break;
                    case "5": valid = true; generateMonthlyReport();           break;
                    case "6": valid = true; viewAllStudents();                 break;
                    case "7": valid = true; manager.displayProfile();          break;
                    case "8":
                        valid = true;
                        System.out.println("  Logging out...\n");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("  [X] Invalid option. Please select 1 to 8 only.");
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // 1. VIEW PENDING APPLICATIONS
    // ---------------------------------------------------------------
    private static void viewPendingApplications(Scanner scanner) {
        List<Applicant> pending = ApplicantModule.applicantList.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        if (pending.isEmpty()) { System.out.println("  No pending applications.\n"); return; }

        System.out.println(W);
        System.out.println("                                            PENDING APPLICATIONS");
        System.out.println(W);
        System.out.printf(" | %-3s | %-23s | %-39s | %-14s | %-13s |%n",
                "#", "Full Name", "Course", "Intake", "Qualification");
        System.out.println(" |" + D.substring(2) + "|");
        for (int i = 0; i < pending.size(); i++) {
            Applicant a = pending.get(i);
            System.out.printf(" | %-3d | %-23s | %-39s | %-14s | %-13s |%n",
                    i + 1,
                    truncate(a.getFullName(), 28),
                    truncate(a.getProgramme(), 42),
                    a.getIntake(),
                    a.getEntryQualification());
        }
        System.out.println(W);
        System.out.print("\n  Enter applicant number to review, or 0 to go back: ");
        String input = scanner.nextLine().trim();

        int idx;
        try { idx = Integer.parseInt(input) - 1; }
        catch (NumberFormatException e) { idx = -1; }

        if (idx == -1) { System.out.println(); return; }
        if (idx < 0 || idx >= pending.size()) { System.out.println("  [X] Invalid choice.\n"); return; }

        Applicant selected = pending.get(idx);
        System.out.println("\n" + W);
        System.out.println("                                             APPLICANT DETAILS");
        System.out.println(W);
        System.out.printf("  %-16s : %s%n", "Name",          selected.getFullName());
        System.out.printf("  %-16s : %s%n", "Email",         selected.getPersonalEmail());
        System.out.printf("  %-16s : %s%n", "Programme",     selected.getProgramme());
        System.out.printf("  %-16s : %s%n", "Level",         selected.getLevel());
        System.out.printf("  %-16s : %s%n", "Intake",        selected.getIntake());
        System.out.printf("  %-16s : %s%n", "Qualification", selected.getEntryQualification());
        System.out.printf("  %-16s : %s%n", "Status",        selected.getStatus());
        System.out.println(W);
        System.out.println("  1. Approve    2. Reject    3. Back");
        System.out.println(W);

        String action = null;
        while (action == null) {
            System.out.print(">> Choice: ");
            String raw = scanner.nextLine().trim();
            switch (raw) {
                case "1": case "2": case "3": action = raw; break;
                default: System.out.println("  [X] Invalid choice. Please select 1 to 3 only.");
            }
        }

        switch (action) {
            case "1": {
                boolean alreadyEnrolled = StudentStore.studentList.stream()
                        .anyMatch(s -> s.getName().equalsIgnoreCase(selected.getFullName()));
                if (alreadyEnrolled) {
                    System.out.println("\n  [X] A student account already exists for "
                            + selected.getFullName() + ".\n");
                    break;
                }
                selected.setStatus("APPROVED");
                ApplicantModule.saveToFile();

                String studentId   = StudentStore.generateStudentId();
                String instEmail   = StudentStore.generateEmail(selected.getFullName());
                String tempPass    = "Temp@123";
                Student newStudent = new Student(studentId, selected.getFullName(), tempPass,
                        instEmail, selected.getProgramme(), "N/A", true);
                StudentStore.studentList.add(newStudent);
                StudentStore.saveToFile();

                System.out.println("\n" + W);
                System.out.println("                                           APPLICATION APPROVED!");
                System.out.println(W);
                System.out.printf("  %-16s : %s%n", "Student ID",    studentId);
                System.out.printf("  %-16s : %s%n", "Inst. Email",   instEmail);
                System.out.printf("  %-16s : %s%n", "Temp Password", tempPass);
                System.out.println(W + "\n");
                break;
            }
            case "2":
                selected.setStatus("REJECTED");
                ApplicantModule.saveToFile();
                System.out.println("\n  Application Rejected for " + selected.getFullName() + ".\n");
                break;
            default:
                System.out.println();
        }
    }

    // ---------------------------------------------------------------
    // 2. VIEW ALL TICKETS  
    // ---------------------------------------------------------------
    private static void viewAllTickets() {
        if (TicketStore.all.isEmpty()) { System.out.println("  No tickets found.\n"); return; }

        System.out.println(W);
        System.out.println("                                                ALL TICKETS");
        System.out.println(W);
        System.out.printf(" | %-5s | %-24s | %-12s | %-24s | %-8s | %-16s |%n",
                "ID", "Title", "Handled By", "Status", "Priority", "Category");
        System.out.println(" |" + D.substring(2) + "|");
        for (Ticket t : TicketStore.all) {
            System.out.printf(" | %-5s | %-24s | %-12s | %-24s | %-8s | %-16s |%n",
                    t.getTicketId(),
                    truncate(t.getTitle(), 26),
                    StaffModule.getStaffDisplayName(t.getHandledBy()),
                    t.getStatus(),
                    t.getPriority(),
                    t.getCategory());
        }
        System.out.println(W + "\n");
    }

    // ---------------------------------------------------------------
    // 3. REASSIGN TICKET
    // ---------------------------------------------------------------
    private static void reassignTicket(Scanner scanner) {
        System.out.println(W);
        System.out.println("                                              REASSIGN TICKET");
        System.out.println(W);
        System.out.println("  1. Process staff reassignment requests");
        System.out.println("  2. Manually assign an open ticket to another staff member");
        System.out.println(W);

        String mode = null;
        while (mode == null) {
            System.out.print(">> Choice (1-2): ");
            String raw = scanner.nextLine().trim();
            System.out.println();
            switch (raw) {
                case "1": case "2": mode = raw; break;
                default: System.out.println("  [X] Invalid choice. Please select 1 to 2 only.");
            }
        }
        switch (mode) {
            case "1": reassignFromStaffRequests(scanner); break;
            case "2": reassignOpenTicketManually(scanner); break;
        }
    }

    private static void reassignFromStaffRequests(Scanner scanner) {
        List<Ticket> requests = TicketStore.all.stream()
                .filter(t -> "Reassignment Requested".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        if (requests.isEmpty()) { System.out.println("  No reassignment requests from staff.\n"); return; }

        System.out.println(W);
        System.out.println("                                     REASSIGNMENT REQUESTS (from staff)");
        System.out.println(W);
        System.out.printf(" | %-7s | %-24s | %-14s | %-50s |%n",
                "ID", "Title", "From Staff", "Reason");
        System.out.println(" |" + D.substring(2) + "|");
        for (Ticket t : requests) {
            String fromName = StaffModule.getStaffDisplayName(t.getHandledBy());
            String reason   = Validator.isMeaningfulText(t.getReassignmentReason())
                    ? t.getReassignmentReason() : "(none)";
            System.out.printf(" | %-7s | %-24s | %-14s | %-50s |%n",
                    t.getTicketId(), truncate(t.getTitle(), 24), fromName, truncate(reason, 50));
        }
        System.out.println(W);

        System.out.print("\n  Enter Ticket ID: ");
        String ticketId = scanner.nextLine().trim().toUpperCase();

        Ticket ticket = findTicketById(ticketId);
        if (ticket == null || !"Reassignment Requested".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("  [X] Ticket not found or status is not Reassignment Requested.\n"); return;
        }
        applyManagerReassignment(ticket, StaffModule.selectStaffIdFromMenu(scanner));
    }

    private static void reassignOpenTicketManually(Scanner scanner) {
        List<Ticket> open = TicketStore.all.stream()
                .filter(t -> !"Closed".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        if (open.isEmpty()) { System.out.println("  No open tickets.\n"); return; }

        System.out.println(W);
        System.out.println("                                         OPEN TICKETS (not closed)");
        System.out.println(W);
        System.out.printf(" | %-8s | %-24s | %-20s | %-24s | %-16s |%n",
                "ID", "Title", "Current Handler", "Status", "Student");
        System.out.println(" |" + D.substring(2) + "|");
        for (Ticket t : open) {
            System.out.printf(" | %-8s | %-24s | %-20s | %-24s | %-16s |%n",
                    t.getTicketId(),
                    truncate(t.getTitle(), 24),
                    StaffModule.getStaffDisplayName(t.getHandledBy()),
                    t.getStatus(),
                    t.getStudentId());
        }
        System.out.println(W);

        System.out.print("\n  Enter Ticket ID: ");
        String ticketId = scanner.nextLine().trim().toUpperCase();

        Ticket ticket = findTicketById(ticketId);
        if (ticket == null)                                { System.out.println("  [X] Ticket not found.\n");                    return; }
        if ("Closed".equalsIgnoreCase(ticket.getStatus())) { System.out.println("  [X] Closed tickets cannot be reassigned.\n"); return; }

        System.out.printf("%n  Current handler : %s (%s)%n",
                StaffModule.getStaffDisplayName(ticket.getHandledBy()), ticket.getHandledBy());
        applyManagerReassignment(ticket, StaffModule.selectStaffIdFromMenu(scanner));
    }

    private static void applyManagerReassignment(Ticket ticket, String newStaffId) {
        boolean sameStaff = newStaffId.equalsIgnoreCase(ticket.getHandledBy());
        ticket.setHandledBy(newStaffId);
        ticket.setStatus("Pending");
        ticket.setReassignmentReason(null);
        TicketStore.saveToFile();

        System.out.println("\n" + W);
        System.out.println("                                             TICKET REASSIGNED");
        System.out.println(W);
        System.out.printf("  %-16s : %s%n", "Ticket ID",   ticket.getTicketId());
        System.out.printf("  %-16s : %s (%s)%n", "New Handler",
                StaffModule.getStaffDisplayName(newStaffId), newStaffId);
        System.out.printf("  %-16s : %s%n", "Status", "Pending");
        if (sameStaff) System.out.println("  (Same staff — manager sent it back for review.)");
        System.out.println(W + "\n");
    }

    // ---------------------------------------------------------------
    // 4. VIEW ALL FEEDBACK  
    // ---------------------------------------------------------------
    private static void viewAllFeedback() {
        List<Ticket> withFeedback = TicketStore.all.stream()
                .filter(t -> t.getRating() != null)
                .collect(Collectors.toList());

        if (withFeedback.isEmpty()) { System.out.println("  No feedback received yet.\n"); return; }

        double avgRating = withFeedback.stream().mapToInt(Ticket::getRating).average().orElse(0);

        System.out.println(W);
        System.out.println("                                                ALL FEEDBACK");
        System.out.println(W);
        System.out.printf(" | %-10s | %-20s | %-10s | %-13s | %-6s | %-30s |%n",
                "Ticket ID", "Title", "Student", "Handled By", "Rating", "Comment");
        System.out.println(" |" + D.substring(2) + "|");
        for (Ticket t : withFeedback) {
            System.out.printf(" | %-10s | %-20s | %-10s | %-13s | %-6s | %-30s |%n",
                    t.getTicketId(),
                    truncate(t.getTitle(), 20),
                    t.getStudentId(),
                    t.getHandledBy(),
                    t.getRating() + "/5",
                    truncate(t.getFeedback(), 30));
        }
        System.out.println(W);
        System.out.printf("%n  Average Rating  : %.1f / 5.0   (%d review(s))%n%n",
                avgRating, withFeedback.size());
    }

    // ---------------------------------------------------------------
    // 5. GENERATE MONTHLY REPORT
    // ---------------------------------------------------------------
    private static void generateMonthlyReport() {
        int totalTickets  = TicketStore.all.size();
        int resolved      = (int) TicketStore.all.stream()
                .filter(t -> "Resolved".equalsIgnoreCase(t.getStatus())
                          || "Closed".equalsIgnoreCase(t.getStatus())).count();
        int pending       = (int) TicketStore.all.stream()
                .filter(t -> "Pending".equalsIgnoreCase(t.getStatus())).count();
        int inProgress    = (int) TicketStore.all.stream()
                .filter(t -> "In Progress".equalsIgnoreCase(t.getStatus())).count();
        int reassignments = (int) TicketStore.all.stream()
                .filter(t -> "Reassignment Requested".equalsIgnoreCase(t.getStatus())).count();

        List<Ticket> timedTickets = TicketStore.all.stream()
                .filter(t -> t.getResolvedDate() != null && t.getCreatedDate() != null)
                .collect(Collectors.toList());
        double avgDays = timedTickets.stream()
                .mapToLong(t -> ChronoUnit.DAYS.between(t.getCreatedDate(), t.getResolvedDate()))
                .average().orElse(0);

        int appReceived = ApplicantModule.applicantList.size();
        int appApproved = (int) ApplicantModule.applicantList.stream()
                .filter(a -> "APPROVED".equalsIgnoreCase(a.getStatus())).count();
        int appRejected = (int) ApplicantModule.applicantList.stream()
                .filter(a -> "REJECTED".equalsIgnoreCase(a.getStatus())).count();
        int appPendingC = (int) ApplicantModule.applicantList.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus())).count();

        List<Ticket> feedbacks = TicketStore.all.stream()
                .filter(t -> t.getRating() != null).collect(Collectors.toList());
        double avgRating = feedbacks.stream().mapToInt(Ticket::getRating).average().orElse(0);

        String month = LocalDate.now().getMonth().getDisplayName(
                java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)
                + " " + LocalDate.now().getYear();

        System.out.println("\n" + W);
        System.out.println("                                               MONTHLY REPORT");
        System.out.println(W);
        System.out.printf("%n  %-20s : %s%n%n", "Period", month);

        System.out.println(W);
        System.out.println("  SUPPORT TICKETS");
        System.out.println(D);
        System.out.printf("  %-24s : %d%n", "Submitted",          totalTickets);
        System.out.printf("  %-24s : %d%n", "Pending",            pending);
        System.out.printf("  %-24s : %d%n", "In Progress",        inProgress);
        System.out.printf("  %-24s : %d%n", "Resolved / Closed",  resolved);
        System.out.printf("  %-24s : %d%n", "Reassignment Reqs",  reassignments);
        if (timedTickets.isEmpty())
            System.out.printf("  %-24s : N/A%n", "Avg Response Time");
        else
            System.out.printf("  %-24s : %.1f day(s)%n", "Avg Response Time", avgDays);
        System.out.println(W);

        System.out.println("\n" + W);
        System.out.println("  APPLICATIONS");
        System.out.println(D);
        System.out.printf("  %-24s : %d%n", "Received",       appReceived);
        System.out.printf("  %-24s : %d%n", "Approved",       appApproved);
        System.out.printf("  %-24s : %d%n", "Rejected",       appRejected);
        System.out.printf("  %-24s : %d%n", "Pending Review", appPendingC);
        System.out.println(W);

        System.out.println("\n" + W);
        System.out.println("  ENROLLED STUDENTS");
        System.out.println(D);
        if (StudentStore.studentList.isEmpty()) {
            System.out.println("  None yet.");
        } else {
            System.out.printf(" | %-10s | %-26s | %-39s | %-20s |%n",
                    "Student ID", "Name", "Course", "Password Status");
            System.out.println(" |" + D.substring(2) + "|");
            for (Student s : StudentStore.studentList) {
                System.out.printf(" | %-10s | %-26s | %-39s | %-20s |%n",
                        s.getStudentId(),
                        truncate(s.getName(), 26),
                        truncate(s.getCourse(), 38),
                        s.isFirstLogin() ? "Temp Password" : "Active");
            }
        }
        System.out.println(W);

        System.out.println("\n" + W);
        System.out.println("  FEEDBACK");
        System.out.println(D);
        if (feedbacks.isEmpty()) {
            System.out.printf("  %-24s : 0%n",   "Reviews");
            System.out.printf("  %-24s : N/A%n", "Avg Rating");
        } else {
            System.out.printf("  %-24s : %d%n",         "Reviews",    feedbacks.size());
            System.out.printf("  %-24s : %.1f / 5.0%n", "Avg Rating", avgRating);
        }
        System.out.println(W + "\n");
    }

    // ---------------------------------------------------------------
    // 6. VIEW ALL STUDENTS  
    // ---------------------------------------------------------------
    private static void viewAllStudents() {
        if (StudentStore.studentList.isEmpty()) {
            System.out.println("  No enrolled students yet.\n"); return;
        }

        System.out.println(W);
        System.out.println("                                                ALL STUDENTS");
        System.out.println(W);
        System.out.printf(" | %-10s | %-26s | %-39s | %-20s |%n",
                "Student ID", "Name", "Course", "Password Status");
        System.out.println(" |" + D.substring(2) + "|");
        for (Student s : StudentStore.studentList) {
            System.out.printf(" | %-10s | %-26s | %-39s | %-20s |%n",
                    s.getStudentId(),
                    truncate(s.getName(), 26),
                    truncate(s.getCourse(), 39),
                    s.isFirstLogin() ? "Temp Password" : "Active");
        }
        System.out.println(W + "\n");
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------
    private static Ticket findTicketById(String ticketId) {
        for (Ticket t : TicketStore.all)
            if (t.getTicketId().equalsIgnoreCase(ticketId)) return t;
        return null;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
