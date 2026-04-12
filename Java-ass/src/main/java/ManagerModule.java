import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ManagerModule {

    private static final Manager DEMO_MANAGER = new Manager(
            "MG3001",
            "Dr. Tan Wei Lian",
            "Manager@123"
    );

    // ---------------------------------------------------------------
    // ENTRY POINT
    // ---------------------------------------------------------------
    public static void login(Scanner scanner) {
        System.out.println("\n========== MANAGER LOGIN ==========");

        System.out.print("Manager ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Password  : ");
        String password = scanner.nextLine().trim();

        Manager manager = authenticate(id, password);
        if (manager == null) {
            System.out.println("\n  X Invalid Manager ID or Password.\n");
            return;
        }

        System.out.println("\n----------------------------------");
        System.out.println("Login Successful!");
        System.out.println("----------------------------------");
        // Polymorphism: calls Manager's override of User.displayProfile()
        manager.displayProfile();

        showManagerMenu(scanner, manager);
    }

    private static Manager authenticate(String id, String password) {
        if (DEMO_MANAGER.getManagerId().equals(id)
                && DEMO_MANAGER.getPassword().equals(password)) {
            return DEMO_MANAGER;
        }
        return null;
    }

    // ---------------------------------------------------------------
    // MANAGER MENU
    // ---------------------------------------------------------------
    private static void showManagerMenu(Scanner scanner, Manager manager) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("========== MANAGER MENU ==========");
            System.out.println("1. View Pending Applications");
            System.out.println("2. View All Tickets");
            System.out.println("3. Reassign Ticket");
            System.out.println("4. View All Feedback");
            System.out.println("5. Generate Monthly Report");
            System.out.println("6. View All Students");
            System.out.println("7. Logout");
            System.out.println("===================================");
            System.out.print("Enter Choice: ");
            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1": viewPendingApplications(scanner); break;
                case "2": viewAllTickets();                  break;
                case "3": reassignTicket(scanner);           break;
                case "4": viewAllFeedback();                 break;
                case "5": generateMonthlyReport();           break;
                case "6": viewAllStudents();                 break;
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
    // 1. VIEW PENDING APPLICATIONS  (approve / reject inline)
    // ---------------------------------------------------------------
    private static void viewPendingApplications(Scanner scanner) {
        List<Applicant> pending = Applicantmodule.applicantList.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        if (pending.isEmpty()) {
            System.out.println("No pending applications.\n");
            return;
        }

        System.out.println("======= PENDING APPLICATIONS =======");
        for (int i = 0; i < pending.size(); i++) {
            Applicant a = pending.get(i);
            System.out.println("  [" + (i + 1) + "] " + a.getFullName()
                    + " | " + a.getProgramme()
                    + " | " + a.getIntake()
                    + " | " + a.getEntryQualification());
        }
        System.out.println("====================================");
        System.out.println("\nEnter applicant number to review, or 0 to go back:");
        System.out.print("Choice: ");
        String input = scanner.nextLine().trim();

        int idx;
        try { idx = Integer.parseInt(input) - 1; }
        catch (NumberFormatException e) { idx = -1; }

        if (idx == -1) { System.out.println(); return; }
        if (idx < 0 || idx >= pending.size()) {
            System.out.println("  X Invalid choice.\n");
            return;
        }

        Applicant selected = pending.get(idx);
        System.out.println();
        System.out.println("------ APPLICANT DETAILS ------");
        System.out.println("Name        : " + selected.getFullName());
        System.out.println("Email       : " + selected.getPersonalEmail());
        System.out.println("Programme   : " + selected.getProgramme());
        System.out.println("Level       : " + selected.getLevel());
        System.out.println("Intake      : " + selected.getIntake());
        System.out.println("Qualification: " + selected.getEntryQualification());
        System.out.println("Status      : " + selected.getStatus());
        System.out.println("--------------------------------");
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        System.out.println("3. Back");
        System.out.print("Choice: ");
        String action = scanner.nextLine().trim();

        switch (action) {
            case "1": {
                // Guard — skip if a student account already exists for this applicant
                boolean alreadyEnrolled = StudentStore.studentList.stream()
                        .anyMatch(s -> s.getName().equalsIgnoreCase(selected.getFullName()));
                if (alreadyEnrolled) {
                    System.out.println("\n  X A student account already exists for "
                            + selected.getFullName() + ".\n");
                    break;
                }

                selected.setStatus("APPROVED");
                Applicantmodule.saveToFile();

                // Convert approved applicant → Student
                String studentId   = StudentStore.generateStudentId();
                String instEmail   = StudentStore.generateEmail(selected.getFullName());
                String tempPass    = "Temp@123";
                Student newStudent = new Student(
                        studentId,
                        selected.getFullName(),
                        tempPass,
                        instEmail,
                        selected.getProgramme(),
                        "N/A",
                        true
                );
                StudentStore.studentList.add(newStudent);
                StudentStore.saveToFile();

                System.out.println("\nApplication Approved!");
                System.out.println("------------------------------------------");
                System.out.printf( "Student ID    : %s%n", studentId);
                System.out.printf( "Inst. Email   : %s%n", instEmail);
                System.out.printf( "Temp Password : %s%n", tempPass);
                System.out.println("------------------------------------------\n");
                break;
            }
            case "2":
                selected.setStatus("REJECTED");
                Applicantmodule.saveToFile();
                System.out.println("\nApplication Rejected for " + selected.getFullName() + ".\n");
                break;
            default:
                System.out.println();
        }
    }

    // ---------------------------------------------------------------
    // 2. VIEW ALL TICKETS
    // ---------------------------------------------------------------
    private static void viewAllTickets() {
        if (TicketStore.all.isEmpty()) {
            System.out.println("No tickets found.\n");
            return;
        }

        System.out.println("============= ALL TICKETS =============");
        System.out.printf("%-6s | %-16s | %-10s | %-8s | %-15s | %s%n",
                "ID", "Title", "Status", "Priority", "Category", "Student");
        System.out.println("------------------------------------------------------------------------");
        for (Ticket t : TicketStore.all) {
            System.out.printf("%-6s | %-16s | %-10s | %-8s | %-15s | %s%n",
                    t.getTicketId(),
                    truncate(t.getTitle(), 28),
                    t.getStatus(),
                    t.getPriority(),
                    t.getCategory(),
                    t.getStudentId());
        }
        System.out.println("========================================\n");
    }

    // ---------------------------------------------------------------
    // 3. REASSIGN TICKET
    // Manager picks another support staff member from a menu (not free-typed ID).
    // ---------------------------------------------------------------
    private static void reassignTicket(Scanner scanner) {
        System.out.println("\n========== REASSIGN TICKET ==========");
        System.out.println("1. Process staff reassignment requests");
        System.out.println("2. Manually assign an open ticket to another staff member");
        System.out.println("=====================================");
        System.out.print("Choice (1-2): ");
        String mode = scanner.nextLine().trim();
        System.out.println();

        switch (mode) {
            case "1":
                reassignFromStaffRequests(scanner);
                break;
            case "2":
                reassignOpenTicketManually(scanner);
                break;
            default:
                System.out.println("  X Invalid choice.\n");
        }
    }

    private static void reassignFromStaffRequests(Scanner scanner) {
        List<Ticket> requests = TicketStore.all.stream()
                .filter(t -> "Reassignment Requested".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        if (requests.isEmpty()) {
            System.out.println("No reassignment requests from staff.\n");
            return;
        }

        System.out.println("===== REASSIGNMENT REQUESTS (from staff) =====");
        for (Ticket t : requests) {
            String fromName = StaffModule.getStaffDisplayName(t.getHandledBy());
            System.out.printf("%-6s | %-16s | From: %s (%s)%n",
                    t.getTicketId(), truncate(t.getTitle(), 26), fromName, t.getHandledBy());
            System.out.println();
            System.out.println("Reason: "
                    + (Validator.isMeaningfulText(t.getReassignmentReason())
                            ? t.getReassignmentReason() : "(none)"));
        }
        System.out.println("==============================================");

        System.out.print("\nEnter Ticket ID: ");
        String ticketId = scanner.nextLine().trim().toUpperCase();

        Ticket ticket = findTicketById(ticketId);
        if (ticket == null || !"Reassignment Requested".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("  X Ticket not found or status is not Reassignment Requested.\n");
            return;
        }

        String newStaffId = StaffModule.selectStaffIdFromMenu(scanner);
        applyManagerReassignment(ticket, newStaffId);
    }

    private static void reassignOpenTicketManually(Scanner scanner) {
        List<Ticket> open = TicketStore.all.stream()
                .filter(t -> !"Closed".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        if (open.isEmpty()) {
            System.out.println("No open tickets.\n");
            return;
        }

        System.out.println("----- OPEN TICKETS (not closed) -----");
        for (Ticket t : open) {
            String handler = StaffModule.getStaffDisplayName(t.getHandledBy());
            System.out.printf("%-6s | %-24s | %-14s | Now: %s (%s) | Student: %s%n",
                    t.getTicketId(), truncate(t.getTitle(), 24), t.getStatus(),
                    handler, t.getHandledBy(), t.getStudentId());
        }
        System.out.println("--------------------------------------");

        System.out.print("\nEnter Ticket ID: ");
        String ticketId = scanner.nextLine().trim().toUpperCase();

        Ticket ticket = findTicketById(ticketId);
        if (ticket == null) {
            System.out.println("  X Ticket not found.\n");
            return;
        }
        if ("Closed".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("  X Closed tickets cannot be reassigned.\n");
            return;
        }

        System.out.println("\nCurrent handler: "
                + StaffModule.getStaffDisplayName(ticket.getHandledBy())
                + " (" + ticket.getHandledBy() + ")");

        String newStaffId = StaffModule.selectStaffIdFromMenu(scanner);
        applyManagerReassignment(ticket, newStaffId);
    }

    private static void applyManagerReassignment(Ticket ticket, String newStaffId) {
        boolean sameStaff = newStaffId.equalsIgnoreCase(ticket.getHandledBy());
        ticket.setHandledBy(newStaffId);
        ticket.setStatus("Pending");
        ticket.setReassignmentReason(null);
        TicketStore.saveToFile();

        System.out.println("\nTicket " + ticket.getTicketId() + " updated.");
        System.out.println("Handler     : " + StaffModule.getStaffDisplayName(newStaffId)
                + " (" + newStaffId + ")");
        if (sameStaff) {
            System.out.println("(Same staff — e.g. manager sends it back to you after review.)");
        }
        System.out.println("Status      : Pending");
        System.out.println("----------------------------------\n");
    }

    // ---------------------------------------------------------------
    // 4. VIEW ALL FEEDBACK
    // ---------------------------------------------------------------
    private static void viewAllFeedback() {
        List<Ticket> withFeedback = TicketStore.all.stream()
                .filter(t -> t.getRating() != null)
                .collect(Collectors.toList());

        if (withFeedback.isEmpty()) {
            System.out.println("No feedback received yet.\n");
            return;
        }

        double avgRating = withFeedback.stream()
                .mapToInt(Ticket::getRating)
                .average()
                .orElse(0);

        System.out.println("============= ALL FEEDBACK =============");
        for (Ticket t : withFeedback) {
            System.out.println("Ticket ID : " + t.getTicketId());
            System.out.println("Title     : " + t.getTitle());
            System.out.println("Student   : " + t.getStudentId());
            System.out.println("Handled By: " + t.getHandledBy());
            System.out.printf( "Rating    : %d/5%n", t.getRating());
            System.out.println("Comment   : " + t.getFeedback());
            System.out.println("----------------------------------------");
        }
        System.out.printf("Average Rating: %.1f / 5.0  (%d review(s))%n%n",
                avgRating, withFeedback.size());
    }

    // ---------------------------------------------------------------
    // 5. GENERATE MONTHLY REPORT
    // ---------------------------------------------------------------
    private static void generateMonthlyReport() {
        int totalTickets   = TicketStore.all.size();
        int resolved       = (int) TicketStore.all.stream()
                .filter(t -> "Resolved".equalsIgnoreCase(t.getStatus())
                          || "Closed".equalsIgnoreCase(t.getStatus()))
                .count();
        int pending        = (int) TicketStore.all.stream()
                .filter(t -> "Pending".equalsIgnoreCase(t.getStatus()))
                .count();
        int inProgress     = (int) TicketStore.all.stream()
                .filter(t -> "In Progress".equalsIgnoreCase(t.getStatus()))
                .count();
        int reassignments  = (int) TicketStore.all.stream()
                .filter(t -> "Reassignment Requested".equalsIgnoreCase(t.getStatus()))
                .count();

        // Average response time (days between createdDate and resolvedDate)
        List<Ticket> timedTickets = TicketStore.all.stream()
                .filter(t -> t.getResolvedDate() != null && t.getCreatedDate() != null)
                .collect(Collectors.toList());
        double avgDays = timedTickets.stream()
                .mapToLong(t -> ChronoUnit.DAYS.between(t.getCreatedDate(), t.getResolvedDate()))
                .average()
                .orElse(0);

        // Application stats
        int appReceived = Applicantmodule.applicantList.size();
        int appApproved = (int) Applicantmodule.applicantList.stream()
                .filter(a -> "APPROVED".equalsIgnoreCase(a.getStatus()))
                .count();
        int appRejected = (int) Applicantmodule.applicantList.stream()
                .filter(a -> "REJECTED".equalsIgnoreCase(a.getStatus()))
                .count();
        int appPending  = (int) Applicantmodule.applicantList.stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()))
                .count();

        // Feedback stats
        List<Ticket> feedbacks = TicketStore.all.stream()
                .filter(t -> t.getRating() != null)
                .collect(Collectors.toList());
        double avgRating = feedbacks.stream()
                .mapToInt(Ticket::getRating)
                .average()
                .orElse(0);

        String month = LocalDate.now().getMonth().getDisplayName(
                java.time.format.TextStyle.FULL,
                java.util.Locale.ENGLISH)
                + " " + LocalDate.now().getYear();

        System.out.println("=========== MONTHLY REPORT ===========");
        System.out.println("  Period  : " + month);
        System.out.println("--------------------------------------");
        System.out.println("  SUPPORT TICKETS");
        System.out.printf( "  Submitted          : %d%n",  totalTickets);
        System.out.printf( "  Pending            : %d%n",  pending);
        System.out.printf( "  In Progress        : %d%n",  inProgress);
        System.out.printf( "  Resolved / Closed  : %d%n",  resolved);
        System.out.printf( "  Reassignment Reqs  : %d%n",  reassignments);
        if (timedTickets.isEmpty()) {
            System.out.println("  Avg Response Time  : N/A");
        } else {
            System.out.printf( "  Avg Response Time  : %.1f day(s)%n", avgDays);
        }
        System.out.println("--------------------------------------");
        System.out.println("  APPLICATIONS");
        System.out.printf( "  Received           : %d%n",  appReceived);
        System.out.printf( "  Approved           : %d%n",  appApproved);
        System.out.printf( "  Rejected           : %d%n",  appRejected);
        System.out.printf( "  Pending Review     : %d%n",  appPending);
        System.out.println("--------------------------------------");
        System.out.println("  ENROLLED STUDENTS  (via Reportable.generateSummary)");
        if (StudentStore.studentList.isEmpty()) {
            System.out.println("  None yet.");
        } else {
            for (Student s : StudentStore.studentList) {
                System.out.println("  " + s.generateSummary());
            }
        }
        System.out.println("--------------------------------------");
        System.out.println("  FEEDBACK");
        if (feedbacks.isEmpty()) {
            System.out.println("  Reviews            : 0");
            System.out.println("  Avg Rating         : N/A");
        } else {
            System.out.printf( "  Reviews            : %d%n", feedbacks.size());
            System.out.printf( "  Avg Rating         : %.1f / 5.0%n", avgRating);
        }
        System.out.println("======================================\n");
    }

    // ---------------------------------------------------------------
    // 6. VIEW ALL STUDENTS
    // Uses Reportable.generateSummary() — polymorphism via interface
    // ---------------------------------------------------------------
    private static void viewAllStudents() {
        if (StudentStore.studentList.isEmpty()) {
            System.out.println("No enrolled students yet.\n");
            return;
        }
        System.out.println("============= ALL STUDENTS =============");
        System.out.printf("%-10s | %-25s | %-35s | %s%n",
                "ID", "Name", "Course", "Status");
        System.out.println("------------------------------------------------------------------------");
        for (Student s : StudentStore.studentList) {
            // Reportable interface — each student knows how to summarise itself
            System.out.println(s.generateSummary());
        }
        System.out.println("========================================\n");
    }

    // ---------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------
    private static Ticket findTicketById(String ticketId) {
        for (Ticket t : TicketStore.all) {
            if (t.getTicketId().equalsIgnoreCase(ticketId)) return t;
        }
        return null;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
