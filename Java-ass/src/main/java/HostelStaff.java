/**
 * Specialised staff for Hostel-related support tickets.
 * Demonstrates multi-level INHERITANCE: HostelStaff → Staff → User
 */
public class HostelStaff extends Staff {

    public HostelStaff(String staffId, String name, String password) {
        super(staffId, name, password, "Hostel");
    }

    @Override
    public String getRole() { return "Hostel Staff"; }
}
