/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main.staff;

public class HostelStaff extends Staff {

    //constructor
    public HostelStaff() {}
    public HostelStaff(String staffId, String name, String password) {
        super(staffId, name, password, "Hostel");
    }

    @Override
    public String getRole() {
        return "Hostel Staff";
    }
}