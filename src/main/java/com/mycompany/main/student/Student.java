/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main.student;

import com.mycompany.main.Reportable;
import com.mycompany.main.ticket.Ticket;
import com.mycompany.main.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends User implements Reportable {

    private String email;
    private String course;
    private boolean firstLogin;
    private LocalDate enrollmentDate;
    private final List<Ticket> tickets = new ArrayList<>();

    public Student() {}
    public Student(String studentId, String name, String password, String email,
                   String course, boolean firstLogin) {
        this(studentId, name, password, email, course, firstLogin, LocalDate.now());
    }

    public Student(String studentId, String name, String password, String email,
                   String course, boolean firstLogin, LocalDate enrollmentDate) {
        super(studentId, name, password);
        this.email = email;
        this.course = course;
        this.firstLogin = firstLogin;
        this.enrollmentDate = enrollmentDate;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public void displayProfile() {
        // Polymorphism: profile card display 
        String[][] fields = {
            { "STUDENT ID", id },
            { "NAME", name },
            { "EMAIL", email },
            { "COURSE", course },
            { "ROLE", getRole() }
        };
        printProfileCard(fields);
    }

    @Override
    public String generateSummary() {
        String safeName = name == null ? "" : name;
        String safeCourse = course == null ? "" : course;
        if (safeName.length() > 26) safeName = safeName.substring(0, 25) + "…";
        if (safeCourse.length() > 39) safeCourse = safeCourse.substring(0, 38) + "…";
        return String.format("%-10s | %-26s | %-39s | %-20s",
                id, safeName, safeCourse, firstLogin ? "Temp Password" : "Active");
    }

    public String getStudentId()     { return getId(); }
    public String getEmail()         { return email; }
    public String getCourse()        { return course; }
    public boolean isFirstLogin()    { return firstLogin; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public List<Ticket> getTickets() { return tickets; }

    public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}
