/**
 * Specialised staff for Food & Beverage-related support tickets.
 * Demonstrates multi-level INHERITANCE: FoodBeverageStaff → Staff → User
 */
public class FoodBeverageStaff extends Staff {

    public FoodBeverageStaff(String staffId, String name, String password) {
        super(staffId, name, password, "Food & Beverage");
    }

    @Override
    public String getRole() { return "F&B Staff"; }
}
