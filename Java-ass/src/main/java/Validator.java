public class Validator {

    // ---------------------------------------------------------------
    // VALIDATE FULL NAME
    // - Only alphabets and spaces allowed (no numbers or symbols)
    // - Cannot be empty
    // ---------------------------------------------------------------
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return name.matches("[a-zA-Z ]+");
    }

    // ---------------------------------------------------------------
    // VALIDATE EMAIL
    // - Must follow standard email format: example@domain.com
    // - Cannot be empty
    // ---------------------------------------------------------------
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    // ---------------------------------------------------------------
    // VALIDATE PASSWORD
    // - Minimum 8 characters
    // - At least 1 uppercase letter
    // - At least 1 lowercase letter
    // - At least 1 digit
    // ---------------------------------------------------------------
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        return hasUpper && hasLower && hasDigit;
    }

    // ---------------------------------------------------------------
    // PRINT ERROR MESSAGES (helper for user feedback)
    // ---------------------------------------------------------------
    public static void printNameError() {
        System.out.println("  X Name must contain alphabets only (no numbers or symbols).");
    }

    public static void printEmailError() {
        System.out.println("  X Invalid email format. Example: hello@gmail.com");
    }

    public static void printPasswordError() {
        System.out.println("  X Password must be at least 8 characters,");
        System.out.println("    include uppercase, lowercase, and a number.");
        System.out.println("    Example: Qwerty123");
    }

    /** True if {@code s} is non-null, non-blank, and not the literal word "null". */
    public static boolean isMeaningfulText(String s) {
        if (s == null) return false;
        String t = s.trim();
        return !t.isEmpty() && !"null".equalsIgnoreCase(t);
    }
}