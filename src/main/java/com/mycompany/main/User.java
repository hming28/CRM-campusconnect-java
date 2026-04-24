/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

/**
 * Abstract base class for all system users (Student, Staff, Manager).
 *
 * OOP roles:
 *   Abstraction  — declares abstract getRole() and displayProfile()
 *   Inheritance  — Student, Staff, Manager all extend this class
 *   Polymorphism — each subclass overrides displayProfile() differently
 *
 * Implements UIConstants so that the profile-card helper can use the
 * shared divider strings without redefining them locally — keeping
 * dividers in one authoritative place (UIConstants interface).
 */
public abstract class User implements UIConstants {

    protected String id;
    protected String name;
    protected String password;
    
    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getPassword() { return password; }
    public void setPassword(String password) 
    { this.password = password; 
    }

    // ---------------------------------------------------------------
    // CONSTRUCTORS
    // ---------------------------------------------------------------
    public User() {}

    public User(String id, String name, String password) {
        this.id       = id;
        this.name     = name;
        this.password = password;
    }

    // ---------------------------------------------------------------
    // PROFILE CARD — shared ASCII card renderer used by all subclasses
    // ---------------------------------------------------------------
    /**
     * Prints an ASCII profile card.
     * fields is a String[][] where each element is { "LABEL", "value" }.
     *
     * Example output:
     *   +--------------------+------------------------------+
     *   |      .---.      | MY PROFILE                   |
     *   |     /     \     |                              |
     *   |    |       |    | STAFF ID   : ST2001          |
     *   |     \     /     | NAME       : Adam Lee        |
     *   |    _ '---' _    | DEPARTMENT : IT              |
     *   |  /           \  | ROLE       : IT Staff        |
     *   | |             | |                              |
     *   +--------------------+------------------------------+
     */
    protected static void printProfileCard(String[][] fields) {
        String[] avatar = {
            "      .---.      ",
            "     /     \\     ",
            "    |       |    ",
            "     \\     /     ",
            "    _ '---' _    ",
            "  /           \\  ",
            " |             | "
        };

        // Build right-column content lines
        String[] rightLines = new String[avatar.length];
        rightLines[0] = " MY PROFILE";
        rightLines[1] = "";
        int fieldIdx = 0;
        for (int i = 2; i < avatar.length && fieldIdx < fields.length; i++, fieldIdx++) {
            rightLines[i] = String.format(" %-12s : %s", fields[fieldIdx][0], fields[fieldIdx][1]);
        }
        for (int i = 2; i < avatar.length; i++) {
            if (rightLines[i] == null) rightLines[i] = "";
        }

        // Auto-size right column width
        int rightWidth = 38;
        for (String r : rightLines) {
            if (r != null && r.length() > rightWidth) rightWidth = r.length();
        }

        String divider = "+" + "-".repeat(avatar[0].length() + 2)
                       + "+" + "-".repeat(rightWidth + 2) + "+";

        System.out.println(divider);
        for (int i = 0; i < avatar.length; i++) {
            String right = (rightLines[i] != null) ? rightLines[i] : "";
            System.out.printf("| %s | %-" + rightWidth + "s |%n", avatar[i], right);
        }
        System.out.println(divider);
        System.out.println();
    }

    // ---------------------------------------------------------------
    // CONCRETE SHARED METHODS
    // ---------------------------------------------------------------
    

    // ---------------------------------------------------------------
    // ABSTRACT METHODS — each subclass must implement (Polymorphism)
    // ---------------------------------------------------------------

    /** Returns a human-readable role label, e.g. "Student", "IT Staff", "Manager". */
    public abstract String getRole();

    /**
     * Prints the user's profile to the console.
     * Each subclass formats differently — runtime polymorphism in action.
     */
    public abstract void displayProfile();
}
