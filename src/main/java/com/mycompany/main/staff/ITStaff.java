/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main.staff;

public class ITStaff extends Staff {

    //constructor
    public ITStaff() {}
    public ITStaff(String staffId, String name, String password) {
        super(staffId, name, password, "IT");
    }   

    @Override
    public String getRole() {
        return "IT Staff";
    }
}