/**
 * Specialised staff for Facility-related support tickets.
 * Demonstrates multi-level INHERITANCE: FacilityStaff → Staff → User
 */
public class FacilityStaff extends Staff {

    public FacilityStaff(String staffId, String name, String password) {
        super(staffId, name, password, "Facility");
    }

    @Override
    public String getRole() { return "Facility Staff"; }
}
