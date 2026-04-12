import java.util.ArrayList;
import java.util.List;

/**
 * Represents an enrolled student in the CampusConnect system.
 *
 * Demonstrates:
 *  - INHERITANCE    : extends User (inherits id, name, password)
 *  - ENCAPSULATION  : private fields accessed via getters/setters
 *  - POLYMORPHISM   : overrides getRole() and displayProfile() from User
 */
public class Student extends User implements Reportable {

    // Student-specific fields (id, name, password are in User)
    private String  email;
    private String  course;
    private String  phone;
    private boolean firstLogin;
    private final List<Ticket> tickets = new ArrayList<>();

    public Student(String studentId,
                   String name,
                   String password,
                   String email,
                   String course,
                   String phone,
                   boolean firstLogin) {
        super(studentId, name, password);   // pass shared fields to User
        this.email      = email;
        this.course     = course;
        this.phone      = phone;
        this.firstLogin = firstLogin;
    }

    // ---------------------------------------------------------------
    // POLYMORPHISM — overrides User's abstract methods
    // ---------------------------------------------------------------
    @Override
    public String getRole() { return "Student"; }

    @Override
    public void displayProfile() {
        System.out.println("--------- MY PROFILE ---------");
        System.out.println("Role      : " + getRole());
        System.out.println("Name      : " + name);
        System.out.println("Student ID: " + id);
        System.out.println("Email     : " + email);
        System.out.println("Course    : " + course);
        System.out.println("--------------------------------\n");
    }

    // ---------------------------------------------------------------
    // Reportable interface implementation
    // ---------------------------------------------------------------
    @Override
    public String generateSummary() {
        return String.format("%-10s | %-25s | %-35s | %s",
                id, name, course, firstLogin ? "Temp Password" : "Active");
    }

    // ---------------------------------------------------------------
    // GETTERS (backward-compatible alias + student-specific fields)
    // ---------------------------------------------------------------
    public String getStudentId()      { return getId(); }
    public String getEmail()          { return email; }
    public String getCourse()         { return course; }
    public String getPhone()          { return phone; }
    public boolean isFirstLogin()     { return firstLogin; }
    public List<Ticket> getTickets()  { return tickets; }

    // ---------------------------------------------------------------
    // SETTERS
    // ---------------------------------------------------------------
    public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }
}
