import java.util.List;
import java.util.Scanner;

public class LoginModule {

    public static void login(Scanner scanner) {
        System.out.println("\n========== STUDENT LOGIN ==========");

        System.out.print("Student ID: ");
        String studentId = scanner.nextLine().trim();

        System.out.print("Password  : ");
        String password = scanner.nextLine().trim();

        Student student = authenticate(studentId, password);
        if (student == null) {
            System.out.println("\nInvalid Student ID or Password.\n");
            return;
        }

        if (student.isFirstLogin()) {
            handleFirstLogin(scanner, student);
        } else {
            System.out.println("\n--------------------------------");
            System.out.println("Welcome, " + student.getName());
            System.out.println("--------------------------------");
        }

        showStudentMenu(scanner, student);
    }

    private static Student authenticate(String id, String password) {
        Student student = StudentStore.findById(id);
        if (student != null && student.getPassword().equals(password)) {
            return student;
        }
        return null;
    }

    private static void handleFirstLogin(Scanner scanner, Student student) {
        System.out.println("\nFirst Login Detected.");
        System.out.println("You must change your password.\n");

        while (true) {
            System.out.print("Enter New Password   : ");
            String newPassword = scanner.nextLine().trim();
            if (!Validator.isValidPassword(newPassword)) {
                Validator.printPasswordError();
                continue;
            }

            System.out.print("Confirm New Password : ");
            String confirmPassword = scanner.nextLine().trim();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("  X Passwords do not match. Please try again.\n");
                continue;
            }

            student.setPassword(newPassword);
            student.setFirstLogin(false);
            StudentStore.saveToFile();
            System.out.println("\nPassword Changed Successfully!");
            System.out.println("--------------------------------");
            System.out.println("Welcome, " + student.getName());
            System.out.println("--------------------------------\n");
            break;
        }
    }

    private static void showStudentMenu(Scanner scanner, Student student) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("========== STUDENT MENU ==========");
            System.out.println("1. Submit Support Ticket");
            System.out.println("2. View Ticket History");
            System.out.println("3. Change Password");
            System.out.println("4. View Profile");
            System.out.println("5. Logout");
            System.out.println("==================================");
            System.out.print("Select Option: ");
            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1":
                    submitTicket(scanner, student);
                    break;
                case "2":
                    viewTicketHistory(scanner, student);
                    break;
                case "3":
                    changePassword(scanner, student);
                    break;
                case "4":
                    viewProfile(student);
                    break;
                case "5":
                    System.out.println("Logging out...\n");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid option. Please select 1–5.\n");
            }
        }
    }

    private static void viewProfile(Student student) {
        // Polymorphism: calls Student's override of User.displayProfile()
        student.displayProfile();
    }

    private static void changePassword(Scanner scanner, Student student) {
        System.out.println("------- CHANGE PASSWORD -------");
        System.out.print("Current Password: ");
        String current = scanner.nextLine().trim();

        if (!student.getPassword().equals(current)) {
            System.out.println("  X Current password is incorrect.\n");
            return;
        }

        while (true) {
            System.out.print("New Password     : ");
            String newPassword = scanner.nextLine().trim();
            if (!Validator.isValidPassword(newPassword)) {
                Validator.printPasswordError();
                continue;
            }

            System.out.print("Confirm Password : ");
            String confirm = scanner.nextLine().trim();

            if (!newPassword.equals(confirm)) {
                System.out.println("  X Passwords do not match. Please try again.\n");
                continue;
            }

            student.setPassword(newPassword);
            StudentStore.saveToFile();
            System.out.println("\nPassword updated successfully.\n");
            break;
        }
    }

    private static void submitTicket(Scanner scanner, Student student) {
        System.out.println("--------- SUBMIT TICKET ---------");

        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        String category = selectCategory(scanner);
        if (category == null) return;

        String priority = selectPriority(scanner);
        if (priority == null) return;

        System.out.println("\nDescription:");
        System.out.print("> ");
        String description = scanner.nextLine().trim();

        String ticketId = TicketStore.nextId();
        Ticket ticket = new Ticket(ticketId, student.getStudentId(), title, category, priority);
        ticket.setDescription(description);
        String assignTo = StaffModule.getStaffIdForCategory(category);
        ticket.setHandledBy(assignTo);
        student.getTickets().add(ticket);
        TicketStore.all.add(ticket);
        TicketStore.saveToFile();

        System.out.println("\nTicket Created Successfully!");
        System.out.println("Ticket ID    : " + ticket.getTicketId());
        System.out.println("Status       : " + ticket.getStatus());
        System.out.println("Assigned to  : " + StaffModule.getStaffDisplayName(assignTo)
                + " (" + assignTo + ") — " + category);
        System.out.println("---------------------------------\n");
    }

    private static String selectCategory(Scanner scanner) {
        while (true) {
            System.out.println("Category:");
            System.out.println("1. IT");
            System.out.println("2. Hostel");
            System.out.println("3. Academic");
            System.out.println("4. Facility");
            System.out.println("5. Food & Beverage");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return "IT";
                case "2":
                    return "Hostel";
                case "3":
                    return "Academic";
                case "4":
                    return "Facility";
                case "5":
                    return "Food & Beverage";
                default:
                    System.out.println("  X Invalid choice. Please select 1–5.\n");
            }
        }
    }

    private static String selectPriority(Scanner scanner) {
        while (true) {
            System.out.println("\nPriority:");
            System.out.println("1. Low");
            System.out.println("2. Medium");
            System.out.println("3. High");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return "Low";
                case "2":
                    return "Medium";
                case "3":
                    return "High";
                default:
                    System.out.println("  X Invalid choice. Please select 1–3.\n");
            }
        }
    }

    private static void viewTicketHistory(Scanner scanner, Student student) {
        List<Ticket> tickets = student.getTickets();
        if (tickets.isEmpty()) {
            System.out.println("No tickets found.\n");
            return;
        }
        
        System.out.println("----------- MY TICKETS -----------");
        for (Ticket t : tickets) {
            System.out.println("Ticket ID     : " + t.getTicketId());
            System.out.println("Title         : " + t.getTitle());
            System.out.println("Category      : " + t.getCategory());
            System.out.println("Priority      : " + t.getPriority());
            System.out.println("Status        : " + t.getStatus());
            if (t.getDescription() != null && !t.getDescription().isEmpty()) {
                System.out.println("Description   : " + t.getDescription());
            }
            if (t.getHandledBy() != null && !t.getHandledBy().isEmpty()) {
                System.out.println("Handled By    : " + t.getHandledBy());
                System.out.println("Staff Name    : " + StaffModule.getStaffDisplayName(t.getHandledBy()));
            }
            if (t.getResponse() != null && !t.getResponse().isEmpty()) {
                System.out.println("Response      : " + t.getResponse());
            }
            if (Validator.isMeaningfulText(t.getReassignmentReason())) {
                System.out.println("Reassignment  : " + t.getReassignmentReason());
            }
            if (t.getCreatedDate() != null) {
                System.out.println("Created date  : " + t.getCreatedDate());
            }
            if (t.getResolvedDate() != null) {
                System.out.println("Resolved date : " + t.getResolvedDate());
            }
            if (t.getRating() != null) {
                System.out.println("Your Rating   : " + t.getRating() + "/5");
                System.out.println("Your Remark   : " + (t.getFeedback() != null ? t.getFeedback() : ""));
            }
            if ("Resolved".equalsIgnoreCase(t.getStatus()) && t.getRating() == null) {
                handleTicketClosure(scanner, t);
            }
            System.out.println("----------------------------------");
        }
        System.out.println();
    }

    private static void handleTicketClosure(Scanner scanner, Ticket ticket) {
        System.out.print("\nClose Ticket " + ticket.getTicketId() + "? (Y/N): ");
        String input = scanner.nextLine().trim();
        if (!input.equalsIgnoreCase("Y")) {
            return;
        }

        System.out.println("\n----- FEEDBACK -----");
        int rating;
        while (true) {
            System.out.print("Rating (1-5): ");
            String r = scanner.nextLine().trim();
            try {
                rating = Integer.parseInt(r);
                if (rating < 1 || rating > 5) {
                    System.out.println("  X Rating must be between 1 and 5.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("  X Please enter a number between 1 and 5.");
            }
        }

        System.out.println("Comment:");
        System.out.print("> ");
        String comment = scanner.nextLine().trim();

        ticket.setRating(rating);
        ticket.setFeedback(comment);
        ticket.setStatus("Closed");
        TicketStore.saveToFile();

        System.out.println("\nTicket Closed Successfully.");
    }
}

