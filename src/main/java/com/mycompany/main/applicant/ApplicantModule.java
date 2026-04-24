/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main.applicant;

import com.mycompany.main.student.Student;
import com.mycompany.main.student.StudentStore;
import com.mycompany.main.UIConstants;
import com.mycompany.main.Validator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all applicant-facing operations: registration and status check.
 * Implements UIConstants so divider strings are inherited from one source.
 */
public class ApplicantModule implements UIConstants {

    public static ArrayList<Applicant> applicantList = new ArrayList<>();

    private static final String DATA_DIR  = "data";
    private static final String FILE_PATH = DATA_DIR + File.separator + "applicants.txt";

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

    private static final String[] QUALIFICATIONS = {
        "SPM", "STPM", "Foundation", "Diploma"
    };

    // ---------------------------------------------------------------
    // REGISTRATION  
    // ---------------------------------------------------------------
    public static void register(Scanner scanner) { 
        System.out.println("\n" + W);
        System.out.println("                                           APPLICANT REGISTRATION");
        System.out.println(W);
        System.out.println("  (Enter 0 at any prompt to return to the main menu)");

        // --- Full Name ---
        String fullName;
        while (true) {
            System.out.print("  Full Name       : ");
            fullName = scanner.nextLine().trim();
            if (fullName.equals("0")) { System.out.println("  Returning to main menu.\n"); return; }
            if (Validator.isValidName(fullName)) break;
            Validator.printNameError();
        }

        // --- Personal Email ---
        String email;
        while (true) {
            System.out.print("  Personal Email  : ");
            email = scanner.nextLine().trim().toLowerCase();
            if (email.equals("0")) { System.out.println("  Returning to main menu.\n"); return; }
            if (!Validator.isValidEmail(email)) { Validator.printEmailError(); continue; }
            if (isEmailUnavailableForNewApplication(email)) {
                System.out.println("  [X] This email already has a pending or approved application.");
                continue;
            }
            break;
        }
        final String regEmail = email;

        // --- Password ---
        String password;
        while (true) {
            System.out.print("  Create Password : ");
            password = scanner.nextLine().trim();
            if (password.equals("0")) { System.out.println("  Returning to main menu.\n"); return; }
            if (Validator.isValidPassword(password)) {
                System.out.println("  Applicant account registered successfully.");
                break;
            }
            Validator.printPasswordError();
        }

        // --- Intake ---
        String intake = selectIntake(scanner);
        if (intake == null) return;

        // --- Qualification ---
        String qualification = selectQualification(scanner);
        if (qualification == null) return;

        // --- Level (auto) ---
        String level = determineLevelFromQualification(qualification);
        System.out.printf("  Programme Level : %s (based on your qualification)%n", level);

        // --- Programme ---
        String programme = selectProgramme(scanner, level);
        if (programme == null) return;

        // Remove old rejected record for same email
        applicantList.removeIf(a -> a.getPersonalEmail().equalsIgnoreCase(regEmail)
                && "REJECTED".equalsIgnoreCase(a.getStatus()));

        Applicant applicant = new Applicant(fullName, regEmail, password,
                                            intake, level, programme, qualification);
        applicantList.add(applicant);
        saveToFile();

        System.out.println("\n" + W);
        System.out.println("                                    APPLICATION SUBMITTED SUCCESSFULLY!");
        System.out.println(W);
        System.out.printf("  %-20s : %s%n", "Application Status", "PENDING APPROVAL");
        System.out.println("  Please wait for management review.");
        System.out.println(W + "\n");
    }

    // ---------------------------------------------------------------
    // FILE PERSISTENCE — SAVE
    // ---------------------------------------------------------------
    public static void saveToFile() {
        new File(DATA_DIR).mkdirs();
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
                    a.getStatus()             + "|" +
                    (a.getSubmissionDate() == null ? "" : a.getSubmissionDate().toString())
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [X] Could not save applicants: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // FILE PERSISTENCE — LOAD
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
                    parts[0], parts[1], parts[2],
                    parts[3], parts[4], parts[5], parts[6]
                );
                a.setStatus(parts[7]);
                if (parts.length >= 9 && !parts[8].trim().isEmpty()) {
                    try {
                        a.setSubmissionDate(LocalDate.parse(parts[8].trim()));
                    } catch (Exception ignored) {}
                } else {
                    // Legacy rows without stored date should not be counted into a specific month
                    a.setSubmissionDate(null);
                }
                applicantList.add(a);
            }
        } catch (java.io.FileNotFoundException ignored) {
        } catch (IOException e) {
            System.out.println("  [X] Could not load applicants: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // INTAKE SELECTION  (0 = back to main menu)
    // ---------------------------------------------------------------
    private static String selectIntake(Scanner scanner) {
        System.out.println("\n" + D);
        System.out.println("  SELECT INTAKE  (0 = back to main menu)");
        System.out.println(D);
        System.out.println("  1. May/June 2026");
        System.out.println("  2. Sept/Nov 2026");
        System.out.println(D);
        while (true) {
            System.out.print(">> Choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "0": System.out.println("  Returning to main menu.\n"); return null;
                case "1": return "May/June 2026";
                case "2": return "Sept/Nov 2026";
                default:  System.out.println("  [X] Invalid choice. Please select 1 to 2 only.");
            }
        }
    }

    private static String determineLevelFromQualification(String qualification) {
        return "SPM".equalsIgnoreCase(qualification) ? "Diploma" : "Bachelor";
    }

    // ---------------------------------------------------------------
    // PROGRAMME SELECTION  (0 = back)
    // ---------------------------------------------------------------
    private static String selectProgramme(Scanner scanner, String level) {
        String[] programmes = level.equals("Diploma") ? DIPLOMA_PROGRAMMES : BACHELOR_PROGRAMMES;
        System.out.println("\n" + D);
        System.out.printf("  SELECT %s PROGRAMME  (0 = back to main menu)%n", level.toUpperCase());
        System.out.println(D);
        for (int i = 0; i < programmes.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, programmes[i]);
        }
        System.out.println(D);
        while (true) {
            System.out.print(">> Choice: ");
            String raw = scanner.nextLine().trim();
            if (raw.equals("0")) { System.out.println("  Returning to main menu.\n"); return null; }
            try {
                int choice = Integer.parseInt(raw);
                if (choice >= 1 && choice <= programmes.length) return programmes[choice - 1];
            } catch (NumberFormatException ignored) {}
            System.out.println("  [X] Invalid choice. Please select 1 to "
                    + programmes.length + " only.");
        }
    }

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
    // ---------------------------------------------------------------
    public static void checkApplicationStatus(Scanner scanner) {
        System.out.println("\n" + W);
        System.out.println("                                          CHECK APPLICATION STATUS");
        System.out.println(W);
        System.out.print("  Registered Email : ");
        String email = scanner.nextLine().trim().toLowerCase();
        System.out.print("  Password         : ");
        String password = scanner.nextLine().trim();

        Applicant found = null;
        for (int i = applicantList.size() - 1; i >= 0; i--) {
            Applicant a = applicantList.get(i);
            if (a.getPersonalEmail().equalsIgnoreCase(email)
                    && a.getPassword().equals(password)) {
                found = a;
                break;
            }
        }

        if (found == null) {
            System.out.println("  [X] Invalid email or password.\n");
            return;
        }

        System.out.println("\n" + W);
        System.out.println("                                             APPLICATION STATUS");
        System.out.println(W);
        System.out.printf("  %-16s : %s%n", "Name",      found.getFullName());
        System.out.printf("  %-16s : %s%n", "Programme", found.getProgramme());
        System.out.printf("  %-16s : %s%n", "Intake",    found.getIntake());
        System.out.printf("  %-16s : %s%n", "Status",    found.getStatus());
        System.out.println(W);

        if ("APPROVED".equalsIgnoreCase(found.getStatus())) {
            Student student = findStudentByName(found.getFullName());
            if (student != null) {
                System.out.println("\n  Congratulations! Your login credentials:");
                System.out.println(D);
                System.out.printf("  %-16s : %s%n", "Student ID",    student.getStudentId());
                System.out.printf("  %-16s : %s%n", "Inst. Email",   student.getEmail());
                System.out.printf("  %-16s : %s%n", "Temp Password", "Temp@123");
                System.out.println(D + "\n");
            }
        } else if ("REJECTED".equalsIgnoreCase(found.getStatus())) {
            System.out.println("\n  [X] Your application was not accepted.");
            System.out.println("      Please contact the admission office for more information.\n");
        } else {
            System.out.println("\n  Your application is under review. Please check back later.\n");
        }
    }

    private static Student findStudentByName(String name) {
        for (Student s : StudentStore.studentList) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    // ---------------------------------------------------------------
    // QUALIFICATION SELECTION  (0 = back)
    // ---------------------------------------------------------------
    private static String selectQualification(Scanner scanner) {
        System.out.println("\n" + D);
        System.out.println("  SELECT ENTRY QUALIFICATION  (0 = back to main menu)");
        System.out.println(D);
        for (int i = 0; i < QUALIFICATIONS.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, QUALIFICATIONS[i]);
        }
        System.out.println(D);
        while (true) {
            System.out.print(">> Choice: ");
            String raw = scanner.nextLine().trim();
            if (raw.equals("0")) {
                System.out.println("  Returning to main menu.\n"); return null; 
            }
            try {
                int choice = Integer.parseInt(raw);
                if (choice >= 1 && choice <= QUALIFICATIONS.length) return QUALIFICATIONS[choice - 1];
            } catch (NumberFormatException ignored) {}
            System.out.println("  [X] Invalid choice. Please select 1 to "
                    + QUALIFICATIONS.length + " only.");
        }
    }
}
