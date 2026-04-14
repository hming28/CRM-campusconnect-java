/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

public class Manager extends User {

    public Manager() {}
    public Manager(String managerId, String name, String password) {
        super(managerId, name, password);
    }

    @Override
    public String getRole() { return "Manager"; }

    @Override
    public void displayProfile() {
        // Polymorphism: profile card display
        String[][] fields = {
            { "MANAGER ID", id },
            { "NAME",       name },
            { "ROLE",       getRole() }
        };
        printProfileCard(fields);
    }

    public String getManagerId() { return getId(); }
}
