import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StudentStore {

    public static ArrayList<Student> studentList = new ArrayList<>();

    private static final String DATA_DIR  = "data";
    private static final String FILE_PATH = DATA_DIR + File.separator + "students.txt";

    // Derived from existing records on load — no separate counter file needed.
    // "2600003" → extract 3 → next ID will be 2600004.
    private static int idCounter = 1;

    // ---------------------------------------------------------------
    // ID GENERATION  →  26XXXXX  (e.g. 2600001, 2600002 …)
    // ---------------------------------------------------------------
    public static String generateStudentId() {
        return String.format("26%05d", idCounter++);
    }

    // ---------------------------------------------------------------
    // EMAIL GENERATION
    // "Lee Hao Ming" → leehm-wp26@student.tarc.edu.my
    // ---------------------------------------------------------------
    public static String generateEmail(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        String base = parts[0].toLowerCase();
        StringBuilder initials = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                initials.append(Character.toLowerCase(parts[i].charAt(0)));
            }
        }
        return base + initials + "-wp26@student.tarc.edu.my";
    }

    public static Student findById(String studentId) {
        for (Student s : studentList) {
            if (s.getStudentId().equals(studentId)) return s;
        }
        return null;
    }

    // ---------------------------------------------------------------
    // SAVE  →  data/students.txt
    // Format per line: id|name|password|email|course|firstLogin
    // ---------------------------------------------------------------
    public static void saveToFile() {
        new File(DATA_DIR).mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Student s : studentList) {
                writer.write(
                    s.getStudentId() + "|" +
                    s.getName()      + "|" +
                    s.getPassword()  + "|" +
                    s.getEmail()     + "|" +
                    s.getCourse()    + "|" +
                    s.isFirstLogin()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [Warning] Could not save students: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // LOAD  ←  data/students.txt
    // Counter is derived from the highest existing ID — no counter file.
    // ---------------------------------------------------------------
    public static void loadFromFile() {
        studentList.clear();
        File file = new File(FILE_PATH);
        System.out.println("[Data] Students file   : " + file.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 6) continue;
                Student s = new Student(
                    parts[0],                       // studentId
                    parts[1],                       // name
                    parts[2],                       // password
                    parts[3],                       // email
                    parts[4],                       // course
                    "N/A",                          // phone (not stored)
                    Boolean.parseBoolean(parts[5])  // firstLogin
                );
                studentList.add(s);
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            System.out.println("  [Warning] Could not load students: " + e.getMessage());
        }

        // Derive next counter from highest existing student number
        // e.g. "2600003" → substring(2) → "00003" → 3 → next = 4
        int max = 0;
        for (Student s : studentList) {
            try {
                int num = Integer.parseInt(s.getStudentId().substring(2));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        idCounter = max + 1;
    }
}
