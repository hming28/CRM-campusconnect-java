/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

import java.util.ArrayList;
import java.util.List;

public class Student extends User implements Reportable {

    private String email;
    private String course;
    private String phone;
    private boolean firstLogin;
    private final List<Ticket> tickets = new ArrayList<>();

    public Student() {}
    public Student(String studentId, String name, String password, String email,
                   String course, String phone, boolean firstLogin) {
        super(studentId, name, password);
        this.email = email;
        this.course = course;
        this.phone = phone;
        this.firstLogin = firstLogin;
    }

    @Override
    public String getRole() { return "Student"; }

    @Override
    public void displayProfile() {
        // Polymorphism: profile card display 
        String[][] fields = {
            { "STUDENT ID", id },
            { "NAME",       name },
            { "EMAIL",      email },
            { "COURSE",     course },
            { "ROLE",       getRole() }
        };
        printProfileCard(fields);
    }

    @Override
    public String generateSummary() {
        return String.format("%-10s | %-25s | %-35s | %s",
                id, name, course, firstLogin ? "Temp Password" : "Active");
    }

    public String getStudentId()     { return getId(); }
    public String getEmail()         { return email; }
    public String getCourse()        { return course; }
    public String getPhone()         { return phone; }
    public boolean isFirstLogin()    { return firstLogin; }
    public List<Ticket> getTickets() { return tickets; }

    public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }
}
