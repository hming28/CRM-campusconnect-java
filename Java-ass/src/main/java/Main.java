import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ApplicantModule.loadFromFile();
        StudentStore.loadFromFile();   // must load before tickets
        TicketStore.loadFromFile();    // restores tickets into student lists too
        StaffModule.ensurePrimaryAssignment(); // legacy rows: set Handled by from category

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            displayMainMenu();
            System.out.print("Select Option: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    ApplicantModule.register(scanner);
                    break;
                case "2":
                    ApplicantModule.checkApplicationStatus(scanner);
                    break;
                case "3":
                    LoginModule.login(scanner);
                    break;
                case "4":
                    StaffModule.login(scanner);
                    break;
                case "5":
                    ManagerModule.login(scanner);
                    break;
                case "6":
                    System.out.println("\nThank you for using CampusConnect. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid option. Please select 1–6.\n");
            }
        }

        scanner.close();
    }

    public static void displayMainMenu() {
        System.out.println("========================================");
        System.out.println("         CAMPUSCONNECT SYSTEM           ");
        System.out.println("========================================");
        System.out.println("1. Applicant Registration");
        System.out.println("2. Check Application Status");
        System.out.println("3. Student Login");
        System.out.println("4. Staff Login");
        System.out.println("5. Manager Login");
        System.out.println("6. Exit");
        System.out.println("========================================");
    }
}
