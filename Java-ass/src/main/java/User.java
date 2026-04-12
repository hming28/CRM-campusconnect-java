/**
 * Abstract base class for all users in the CampusConnect system.
 * Demonstrates ABSTRACTION and INHERITANCE — every user type
 * shares common identity fields and must implement their own
 * role label and profile display.
 */
public abstract class User {

    // Common identity fields shared by Student, Staff, and Manager
    protected String id;
    protected String name;
    protected String password;

    public User(String id, String name, String password) {
        this.id       = id;
        this.name     = name;
        this.password = password;
    }

    // ---------------------------------------------------------------
    // CONCRETE COMMON METHODS (inherited by all subclasses)
    // ---------------------------------------------------------------
    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    // ---------------------------------------------------------------
    // ABSTRACT METHODS — subclasses MUST override (POLYMORPHISM)
    // ---------------------------------------------------------------

    /** Returns a role label, e.g. "Student", "IT Staff", "Manager". */
    public abstract String getRole();

    /**
     * Prints the user's profile to the console.
     * Each subclass formats it differently — same method call,
     * different behaviour: runtime polymorphism.
     */
    public abstract void displayProfile();
}
