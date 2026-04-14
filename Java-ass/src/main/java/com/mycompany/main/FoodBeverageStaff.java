/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main;

public class FoodBeverageStaff extends Staff {

    //constructor
    public FoodBeverageStaff() {}
    public FoodBeverageStaff(String staffId, String name, String password) {
        super(staffId, name, password, "Food & Beverage");
    }

    @Override
    public String getRole() {
        return "F&B Staff";
    }
}