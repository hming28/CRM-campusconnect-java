import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketStore {

    public static final List<Ticket> all = new ArrayList<>();

    private static final String DATA_DIR  = "data";
    private static final String FILE_PATH = DATA_DIR + File.separator + "tickets.txt";

    private static int counter = 1;

    public static String nextId() {
        return String.format("T%04d", counter++);
    }

    // ---------------------------------------------------------------
    // SAVE  →  data/tickets.txt  (human-readable blocks)
    // ---------------------------------------------------------------
    public static void saveToFile() {
        new File(DATA_DIR).mkdirs();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Ticket t : all) {
                writeTicketBlock(w, t);
            }
        } catch (IOException e) {
            System.out.println("  [Warning] Could not save tickets: " + e.getMessage());
        }
    }

    private static void writeTicketBlock(BufferedWriter w, Ticket t) throws IOException {
        w.write("Ticket ID     : " + safe(t.getTicketId()));
        w.newLine();
        w.write("Student ID    : " + safe(t.getStudentId()));
        w.newLine();
        w.write("Title         : " + safe(t.getTitle()));
        w.newLine();
        w.write("Category      : " + safe(t.getCategory()));
        w.newLine();
        w.write("Priority      : " + safe(t.getPriority()));
        w.newLine();
        w.write("Description   : " + safe(t.getDescription()));
        w.newLine();
        w.write("Status        : " + safe(t.getStatus()));
        w.newLine();
        w.write("Handled by    : " + safe(t.getHandledBy()));
        w.newLine();
        w.write("Staff name    : " + StaffModule.getStaffDisplayName(t.getHandledBy()));
        w.newLine();
        w.write("Response      : " + safe(t.getResponse()));
        w.newLine();
        w.write("Reassignment  : " + safe(t.getReassignmentReason()));
        w.newLine();
        w.write("Rating        : " + (t.getRating() == null ? "" : String.valueOf(t.getRating())));
        w.newLine();
        w.write("Feedback      : " + safe(t.getFeedback()));
        w.newLine();
        w.write("Created date  : " + (t.getCreatedDate() == null ? "" : t.getCreatedDate().toString()));
        w.newLine();
        w.write("Resolved date : " + (t.getResolvedDate() == null ? "" : t.getResolvedDate().toString()));
        w.newLine();
        w.write("--------------------------------");
        w.newLine();
        w.newLine();
    }

    // ---------------------------------------------------------------
    // LOAD  ←  data/tickets.txt
    // Supports: (1) human-readable blocks ending with ---------------
    //           (2) legacy single-line pipe format
    // ---------------------------------------------------------------
    public static void loadFromFile() {
        all.clear();
        File file = new File(FILE_PATH);
        System.out.println("[Data] Tickets file    : " + file.getAbsolutePath());
        if (!file.exists()) {
            counter = 1;
            return;
        }

        try {
            String content = readWholeFile(file);
            if (content.trim().isEmpty()) {
                counter = 1;
                return;
            }

            // Legacy: lines that look like pipe-delimited ticket rows
            if (!content.contains("Ticket ID") && content.contains("|")) {
                loadLegacyPipeFormat(content);
            } else {
                loadReadableBlocks(content);
            }
        } catch (IOException e) {
            System.out.println("  [Warning] Could not load tickets: " + e.getMessage());
        }

        int max = 0;
        for (Ticket t : all) {
            try {
                int num = Integer.parseInt(t.getTicketId().substring(1));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        counter = max + 1;
    }

    private static String readWholeFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    private static void loadLegacyPipeFormat(String content) {
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length < 14) continue;
            Ticket t = buildTicketFromParts(p);
            if (t != null) {
                all.add(t);
                Student owner = StudentStore.findById(p[1]);
                if (owner != null) owner.getTickets().add(t);
            }
        }
    }

    private static void loadReadableBlocks(String content) {
        String[] blocks = content.split("(?m)^-{10,}\\s*$");
        for (String block : blocks) {
            block = block.trim();
            if (block.isEmpty()) continue;
            Map<String, String> map = new HashMap<>();
            for (String line : block.split("\n")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int idx = line.indexOf(':');
                if (idx < 0) continue;
                String key = line.substring(0, idx).trim();
                String val = line.substring(idx + 1).trim();
                map.put(key, val);
            }
            if (map.isEmpty()) continue;
            Ticket t = buildTicketFromMap(map);
            if (t != null) {
                all.add(t);
                Student owner = StudentStore.findById(t.getStudentId());
                if (owner != null) owner.getTickets().add(t);
            }
        }
    }

    private static Ticket buildTicketFromParts(String[] p) {
        try {
            Ticket t = new Ticket(p[0], p[1], p[2], p[3], p[4]);
            if (!p[5].isEmpty())  t.setDescription(p[5]);
            if (!p[6].isEmpty())  t.setStatus(p[6]);
            if (!p[7].isEmpty())  t.setHandledBy(p[7]);
            if (!p[8].isEmpty())  t.setResponse(p[8]);
            if (!p[9].isEmpty() && !"null".equalsIgnoreCase(p[9].trim())) {
                t.setReassignmentReason(p[9]);
            }
            if (!p[10].isEmpty()) t.setRating(Integer.parseInt(p[10]));
            if (!p[11].isEmpty()) t.setFeedback(p[11]);
            if (!p[12].isEmpty()) t.setCreatedDate(LocalDate.parse(p[12]));
            if (!p[13].isEmpty()) t.setResolvedDate(LocalDate.parse(p[13]));
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    private static Ticket buildTicketFromMap(Map<String, String> map) {
        try {
            String id = map.getOrDefault("Ticket ID", "").trim();
            String sid = map.getOrDefault("Student ID", "").trim();
            if (id.isEmpty() || sid.isEmpty()) return null;

            Ticket t = new Ticket(
                    id,
                    sid,
                    map.getOrDefault("Title", ""),
                    map.getOrDefault("Category", ""),
                    map.getOrDefault("Priority", "")
            );
            if (map.containsKey("Description"))
                t.setDescription(emptyToNull(map.get("Description")));
            String st = map.get("Status");
            if (st != null && !st.trim().isEmpty()) t.setStatus(st.trim());
            if (map.containsKey("Handled by"))
                t.setHandledBy(emptyToNull(map.get("Handled by")));
            if (map.containsKey("Response"))
                t.setResponse(emptyToNull(map.get("Response")));
            if (map.containsKey("Reassignment"))
                t.setReassignmentReason(emptyToNull(map.get("Reassignment")));
            String ratingStr = map.getOrDefault("Rating", "").trim();
            if (!ratingStr.isEmpty()) t.setRating(Integer.parseInt(ratingStr));
            if (map.containsKey("Feedback"))
                t.setFeedback(emptyToNull(map.get("Feedback")));
            String cd = map.getOrDefault("Created date", "").trim();
            if (!cd.isEmpty()) t.setCreatedDate(LocalDate.parse(cd));
            String rd = map.getOrDefault("Resolved date", "").trim();
            if (!rd.isEmpty()) t.setResolvedDate(LocalDate.parse(rd));
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty() || "null".equalsIgnoreCase(t)) return null;
        return s;
    }

    private static String safe(String s) {
        if (s == null) return "";
        if ("null".equalsIgnoreCase(s.trim())) return "";
        return s;
    }
}
