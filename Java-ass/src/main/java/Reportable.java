/**
 * Interface that marks a class as capable of producing
 * a one-line text summary of itself.
 *
 * Implemented by: Student, Staff
 * Used by: ManagerModule monthly report
 *
 * Demonstrates Java INTERFACE usage to establish a
 * weak "can-do" relationship between unrelated classes.
 */
public interface Reportable {
    /**
     * Returns a single-line summary suitable for reports and listings.
     * Example:
     *   Student → "2600001 | Lee Hao Ming | Bachelor of Data Science | Active"
     *   Staff   → "ST2001  | Ahmad Fauzi  | IT Staff                 | IT"
     */
    String generateSummary();
}
