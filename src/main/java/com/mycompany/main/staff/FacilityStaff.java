/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main.staff;

public class FacilityStaff extends Staff {

    //constructor
    public FacilityStaff() {}
    public FacilityStaff(String staffId, String name, String password) {
        super(staffId, name, password, "Facility");
    }

    @Override
    public String getRole() {
        return "Facility Staff";
    }
}