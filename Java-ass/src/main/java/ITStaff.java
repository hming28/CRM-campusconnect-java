/**
 * Specialised staff for IT-related support tickets.
 * Demonstrates multi-level INHERITANCE: ITStaff → Staff → User
 */
public class ITStaff extends Staff {

    public ITStaff(String staffId, String name, String password) {
        super(staffId, name, password, "IT");
    }

    @Override
    public String getRole() { return "IT Staff"; }
}
