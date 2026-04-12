/**
 * Represents a system manager in the CampusConnect system.
 *
 * Demonstrates:
 *  - INHERITANCE    : extends User (inherits id, name, password)
 *  - ENCAPSULATION  : all fields private/protected, accessed via methods
 *  - POLYMORPHISM   : overrides getRole() and displayProfile() from User
 */
public class Manager extends User {

    public Manager(String managerId, String name, String password) {
        super(managerId, name, password);
    }

    // ---------------------------------------------------------------
    // POLYMORPHISM — overrides User's abstract methods
    // ---------------------------------------------------------------
    @Override
    public String getRole() { return "Manager"; }

    @Override
    public void displayProfile() {
        System.out.println("--------- MY PROFILE ---------");
        System.out.println("Role      : " + getRole());
        System.out.println("Name      : " + name);
        System.out.println("Manager ID: " + id);
        System.out.println("--------------------------------\n");
    }

    // ---------------------------------------------------------------
    // GETTER (backward-compatible alias)
    // ---------------------------------------------------------------
    public String getManagerId() { return getId(); }
}
