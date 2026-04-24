/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main.staff;

public class AcademicStaff extends Staff {
    
    //constructor
    public AcademicStaff(){}
    public AcademicStaff(String staffId, String name, String password) {
        super(staffId, name, password, "Academic");
    }

    @Override
    public String getRole() {
        return "Academic Staff";
    }
}
