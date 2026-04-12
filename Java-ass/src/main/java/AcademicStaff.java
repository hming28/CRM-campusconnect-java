/**
 * Specialised staff for Academic-related support tickets.
 * Demonstrates multi-level INHERITANCE: AcademicStaff → Staff → User
 */
public class AcademicStaff extends Staff {

    public AcademicStaff(String staffId, String name, String password) {
        super(staffId, name, password, "Academic");
    }

    @Override
    public String getRole() { return "Academic Staff"; }
}
