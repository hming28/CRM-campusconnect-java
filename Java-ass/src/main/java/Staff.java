/**
 * Represents a support staff member in the CampusConnect system.
 *
 * Demonstrates:
 *  - INHERITANCE    : extends User (inherits id, name, password)
 *  - ENCAPSULATION  : private fields accessed via getters/setters
 *  - POLYMORPHISM   : overrides getRole() and displayProfile()
 *
 * Subclassed by ITStaff, HostelStaff, AcademicStaff,
 * FacilityStaff, FoodBeverageStaff for further specialisation.
 */
public class Staff extends User implements Reportable {

    // Staff-specific field (id, name, password are in User)
    private String department;

    public Staff(String staffId, String name, String password, String department) {
        super(staffId, name, password);
        this.department = department;
    }

    // ---------------------------------------------------------------
    // POLYMORPHISM — overrides User's abstract methods
    // Subclasses override getRole() further (e.g. "IT Staff")
    // ---------------------------------------------------------------
    @Override
    public String getRole() { return "Staff"; }

    @Override
    public void displayProfile() {
        System.out.println("--------- MY PROFILE ---------");
        System.out.println("Role      : " + getRole());
        System.out.println("Name      : " + name);
        System.out.println("Staff ID  : " + id);
        System.out.println("Department: " + department);
        System.out.println("--------------------------------\n");
    }

    // ---------------------------------------------------------------
    // Reportable interface implementation
    // ---------------------------------------------------------------
    @Override
    public String generateSummary() {
        return String.format("%-8s | %-20s | %-20s | %s",
                id, name, getRole(), department);
    }

    // ---------------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------------
    public String getStaffId()    { return getId(); }
    public String getDepartment() { return department; }
}
