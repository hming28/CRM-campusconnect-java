import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Applicantmodule {

    public static ArrayList<Applicant> applicantList = new ArrayList<>();

    // Data folder sits next to the source files: Java-ass/data/applicants.txt
    private static final String DATA_DIR  = "data";
    private static final String FILE_PATH = DATA_DIR + File.separator + "applicants.txt";

    // ---------------------------------------------------------------
    // DIPLOMA PROGRAMMES
    // ---------------------------------------------------------------
    private static final String[] DIPLOMA_PROGRAMMES = {
        "Diploma in Accounting",
        "Diploma in Banking and Finance",
        "Diploma in Business Administration",
        "Diploma in Early Childhood Education",
        "Diploma in Hotel Management",
        "Diploma in Data Science",
        "Diploma in Marketing",
        "Diploma in Software Engineering"
    };

    // ---------------------------------------------------------------
    // BACHELOR PROGRAMMES
    // ---------------------------------------------------------------
    private static final String[] BACHELOR_PROGRAMMES = {
        "Bachelor of Accounting",
        "Bachelor of Banking and Finance",
        "Bachelor of Business Administration",
        "Bachelor of Early Childhood Education",
        "Bachelor of Hotel Management",
        "Bachelor of Data Science",
        "Bachelor of Marketing",
        "Bachelor of Software Engineering"
    };

    // ---------------------------------------------------------------
    // ENTRY QUALIFICATIONS
    // SPM             → Diploma only
    // STPM/Foundation/Diploma → Bachelor only
    // ---------------------------------------------------------------
    private static final String[] QUALIFICATIONS = {
        "SPM", "STPM", "Foundation", "Diploma"
    };

    // ---------------------------------------------------------------
    // MAIN REGISTRATION METHOD
    // ---------------------------------------------------------------
    public static void register(Scanner scanner) {
        System.out.println("\n========== APPLICANT REGISTRATION ==========");

        // Full Name — retry until valid
        String fullName;
        while (true) {
            System.out.print("Full Name: ");
            fullName = scanner.nextLine().trim();
            if (Validator.isValidName(fullName)) break;
            Validator.printNameError();
        }

        // Personal Email — retry until valid and already registered not
        String email;
        while (true) {
            System.out.print("Personal Email: ");
            email = scanner.nextLine().trim().toLowerCase();
            if (!Validator.isValidEmail(email)) {
                Validator.printEmailError();
                continue;
            }
            if (isEmailUnavailableForNewApplication(email)) {
                System.out.println("  X This email already has a pending or approved application.");
                continue;
            }
            break;
        }
        final String regEmail = email;

        // Password — retry until valid
        String password;
        while (true) {
            System.out.print("Create Password: ");
            password = scanner.nextLine().trim();
            if (Validator.isValidPassword(password)) {
                System.out.println("Successful Register applicant account.");
                break;
            }
            Validator.printPasswordError();
        }

        // Intake Selection
        String intake = selectIntake(scanner);
        if (intake == null) return;

        // Entry Qualification — determines level automatically
        String qualification = selectQualification(scanner);
        if (qualification == null) return;

        // Level is auto-determined from qualification (no manual selection)
        String level = determineLevelFromQualification(qualification);
        System.out.println("Programme Level : " + level + " (based on your qualification)");

        // Programme Selection
        String programme = selectProgramme(scanner, level);
        if (programme == null) return;

        // Rejected applicants may re-apply with the same email — remove old rejected row first
        applicantList.removeIf(a -> a.getPersonalEmail().equalsIgnoreCase(regEmail)
                && "REJECTED".equalsIgnoreCase(a.getStatus()));

        Applicant applicant = new Applicant(fullName, regEmail, password,
                                            intake, level, programme, qualification);
        applicantList.add(applicant);
        saveToFile();

        System.out.println("--------------------------------------------");
        System.out.println("Application Submitted Successfully!");
        System.out.println("Application Status: PENDING APPROVAL");
        System.out.println("Please wait for management review.");
        System.out.println("--------------------------------------------\n");
    }

    // ---------------------------------------------------------------
    // FILE PERSISTENCE — SAVE
    // Writes every applicant to data/applicants.txt, one per line.
    // Format:  name|email|password|intake|level|programme|qualification|status
    // ---------------------------------------------------------------
    public static void saveToFile() {
        new File(DATA_DIR).mkdirs(); // create data/ folder if it doesn't exist yet
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Applicant a : applicantList) {
                writer.write(
                    a.getFullName()           + "|" +
                    a.getPersonalEmail()      + "|" +
                    a.getPassword()           + "|" +
                    a.getIntake()             + "|" +
                    a.getLevel()              + "|" +
                    a.getProgramme()          + "|" +
                    a.getEntryQualification() + "|" +
                    a.getStatus()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [Warning] Could not save applicants: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // FILE PERSISTENCE — LOAD
    // Reads data/applicants.txt and rebuilds the applicantList.
    // Safe to call even if the file does not exist yet.
    // ---------------------------------------------------------------
    public static void loadFromFile() {
        applicantList.clear();
        File file = new File(FILE_PATH);
        System.out.println("[Data] Applicants file: " + file.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 8) continue;
                Applicant a = new Applicant(
                    parts[0], // fullName
                    parts[1], // email
                    parts[2], // password
                    parts[3], // intake
                    parts[4], // level
                    parts[5], // programme
                    parts[6]  // entryQualification
                );
                a.setStatus(parts[7]);
                applicantList.add(a);
            }
        } catch (java.io.FileNotFoundException ignored) {
            // data/applicants.txt does not exist yet — will be created on first registration
        } catch (IOException e) {
            System.out.println("  [Warning] Could not load applicants: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // INTAKE SELECTION — retry until valid
    // ---------------------------------------------------------------
    private static String selectIntake(Scanner scanner) {
        while (true) {
            System.out.println("\nSelect Intake:");
            System.out.println("1. May/June 2026");
            System.out.println("2. Sept/Nov 2026");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": return "May/June 2026";
                case "2": return "Sept/Nov 2026";
                default:  System.out.println("  X Invalid choice. Please enter 1 or 2.");
            }
        }
    }

    // ---------------------------------------------------------------
    // LEVEL AUTO-DETERMINATION FROM QUALIFICATION
    // SPM → Diploma  |  STPM / Foundation / Diploma → Bachelor
    // ---------------------------------------------------------------
    private static String determineLevelFromQualification(String qualification) {
        if ("SPM".equalsIgnoreCase(qualification)) return "Diploma";
        return "Bachelor";
    }

    // ---------------------------------------------------------------
    // PROGRAMME SELECTION — retry until valid
    // ---------------------------------------------------------------
    private static String selectProgramme(Scanner scanner, String level) {
        String[] programmes = level.equals("Diploma") ? DIPLOMA_PROGRAMMES : BACHELOR_PROGRAMMES;
        while (true) {
            System.out.println("\nSelect Programme:");
            for (int i = 0; i < programmes.length; i++) {
                System.out.println((i + 1) + ". " + programmes[i]);
            }
            System.out.print("Choice: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= programmes.length) {
                    return programmes[choice - 1];
                }
            } catch (NumberFormatException e) {
                // fall through to error
            }
            System.out.println("  X Invalid choice. Please enter a number between 1 and " + programmes.length + ".");
        }
    }

    // ---------------------------------------------------------------
    // DUPLICATE EMAIL GUARD
    // Same email allowed again only after manager rejection (REJECTED).
    // PENDING / APPROVED still block a new registration.
    // ---------------------------------------------------------------
    private static boolean isEmailUnavailableForNewApplication(String email) {
        for (Applicant a : applicantList) {
            if (!a.getPersonalEmail().equalsIgnoreCase(email)) continue;
            String st = a.getStatus();
            if ("PENDING".equalsIgnoreCase(st) || "APPROVED".equalsIgnoreCase(st)) return true;
        }
        return false;
    }

    // ---------------------------------------------------------------
    // CHECK APPLICATION STATUS
    // Applicant enters their personal email to see their status.
    // If APPROVED, their generated student login credentials are shown.
    // ---------------------------------------------------------------
    public static void checkApplicationStatus(Scanner scanner) {
        System.out.println("\n===== CHECK APPLICATION STATUS =====");
        System.out.print("Enter your registered email: ");
        String email = scanner.nextLine().trim().toLowerCase();

        Applicant found = null;
        for (int i = applicantList.size() - 1; i >= 0; i--) {
            Applicant a = applicantList.get(i);
            if (a.getPersonalEmail().equalsIgnoreCase(email)) {
                found = a;
                break;
            }
        }

        if (found == null) {
            System.out.println("  X No application found for this email.\n");
            return;
        }

        System.out.println("\n------- APPLICATION STATUS -------");
        System.out.println("Name      : " + found.getFullName());
        System.out.println("Programme : " + found.getProgramme());
        System.out.println("Intake    : " + found.getIntake());
        System.out.println("Status    : " + found.getStatus());
        System.out.println("----------------------------------");

        if ("APPROVED".equalsIgnoreCase(found.getStatus())) {
            Student student = findStudentByName(found.getFullName());
            if (student != null) {
                System.out.println("\nCongratulations! Your login credentials:");
                System.out.println("------------------------------------------");
                System.out.printf( "Student ID    : %s%n", student.getStudentId());
                System.out.printf( "Inst. Email   : %s%n", student.getEmail());
                System.out.printf( "Temp Password : %s%n", "Temp@123");
                System.out.println("------------------------------------------\n");
            }
        } else if ("REJECTED".equalsIgnoreCase(found.getStatus())) {
            System.out.println("\n  Your application was not accepted because of the requirement.");
            System.out.println("  Please contact the admission office for more info..\n");
        } else {
            System.out.println("\nYour application is under review. Please check back later.\n");
        }
    }

    private static Student findStudentByName(String name) {
        for (Student s : StudentStore.studentList) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    // ---------------------------------------------------------------
    // ENTRY QUALIFICATION SELECTION — retry until valid
    // ---------------------------------------------------------------
    private static String selectQualification(Scanner scanner) {
        while (true) {
            System.out.println("\nSelect Entry Qualification:");
            for (int i = 0; i < QUALIFICATIONS.length; i++) {
                System.out.println((i + 1) + ". " + QUALIFICATIONS[i]);
            }
            System.out.print("Choice: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= QUALIFICATIONS.length) {
                    return QUALIFICATIONS[choice - 1];
                }
            } catch (NumberFormatException e) {
                // fall through to error
            }
            System.out.println("  X Invalid choice. Please enter a number between 1 and " + QUALIFICATIONS.length + ".");
        }
    }
}