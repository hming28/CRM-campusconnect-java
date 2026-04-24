/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main.staff;

import com.mycompany.main.Reportable;
import com.mycompany.main.User;

public class Staff extends User implements Reportable {

    private String department;

    public Staff() {}
    public Staff(String staffId, String name, String password, String department) {
        super(staffId, name, password);
        this.department = department;
    }

    @Override
    public String getRole() {
        return "Staff";
    }

    @Override
    public void displayProfile() {
        // Polymorphism: profile card display
        String[][] fields = {
            { "STAFF ID",   id },
            { "NAME",       name },
            { "DEPARTMENT", department },
            { "ROLE",       getRole() }
        };
        printProfileCard(fields);
    }
    
    @Override
    public String generateSummary() {
        return String.format("%-8s | %-20s | %s",
                id, name, department);
    }

    public String getStaffId()    { return getId(); }
    public String getDepartment() { return department; }
}
