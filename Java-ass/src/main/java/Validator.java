/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

public class Validator {

    // Name: alphabets + spaces, 2–60 chars
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        String t = name.trim();
        if (t.length() < 2 || t.length() > 60) return false;
        return t.matches("[a-zA-Z ]+");
    }

    // Email: standard format
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    // Password: >=8 chars, upper + lower + digit
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        return hasUpper && hasLower && hasDigit;
    }

    // Ticket title: 3–80 chars, at least one letter
    public static boolean isValidTitle(String title) {
        if (title == null || title.trim().isEmpty()) return false;
        String t = title.trim();
        if (t.length() < 3 || t.length() > 80) return false;
        return t.matches(".*[a-zA-Z].*");
    }

    // ---------------------------------------------------------------
    // ERROR MESSAGES
    // ---------------------------------------------------------------
    public static void printNameError() {
        System.out.println("  [X] Name must contain alphabets only (no numbers or symbols),");
        System.out.println("      and must be between 2 and 60 characters.");
    }

    public static void printEmailError() {
        System.out.println("  [X] Invalid email format. Example: hello@gmail.com");
    }

    public static void printPasswordError() {
        System.out.println("  [X] Password must be at least 8 characters,");
        System.out.println("      include uppercase, lowercase, and a number.");
        System.out.println("      Example: Qwerty123");
    }

    public static void printTitleError() {
        System.out.println("  [X] Title must be 3 to 80 characters and contain at least one letter.");
    }

    /** True if s is non-null, non-blank, and not the literal word \"null\". */
    public static boolean isMeaningfulText(String s) {
        if (s == null) return false;
        String t = s.trim();
        return !t.isEmpty() && !"null".equalsIgnoreCase(t);
    }
}
