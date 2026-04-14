/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main;


public class Applicant {

    private String fullName;
    private String personalEmail;
    private String password;
    private String intake;
    private String level;
    private String programme;
    private String entryQualification;
    private String status; // PENDING, APPROVED, REJECTED

    //constructor
    public Applicant() {}
    public Applicant(String fullName, String personalEmail, String password,
                     String intake, String level, String programme, String entryQualification) {
        this.fullName = fullName;
        this.personalEmail = personalEmail;
        this.password = password;
        this.intake = intake;
        this.level = level;
        this.programme = programme;
        this.entryQualification = entryQualification;
        this.status = "PENDING";
    }

    // Getters
    public String getFullName() {
        return fullName;
    }
    public String getPersonalEmail() {
        return personalEmail;
    }
    public String getPassword() {
        return password;
    }
    public String getIntake() {
        return intake; 
    }
    public String getLevel() {
        return level;
    }
    public String getProgramme() {
        return programme;
    }
    public String getEntryQualification() {
        return entryQualification;
    }
    public String getStatus() {
        return status;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Name: " + fullName + " | Email: " + personalEmail +
               " | Programme: " + programme + " | Intake: " + intake +
               " | Status: " + status;
    }
}
