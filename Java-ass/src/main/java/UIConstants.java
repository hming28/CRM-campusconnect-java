/*
 * UIConstants — shared display constants for all UI module classes.
 * Using an interface allows any class to implement it and gain access
 * to the constants without inheritance, demonstrating weak relationships
 * via Java interfaces as encouraged by the OOP design brief.
 *
 * W  = wide  border  (==) used for section headers / outer frames
 * D  = dash  divider (--) used for sub-section separators
 * WM = wide  border for ManagerModule (wider tables)
 * DM = dash  divider for ManagerModule
 */

public interface UIConstants {

    // ---------------------------------------------------------------
    // Standard width  — used by Login / Staff / Applicant modules
    // Calculated to contain the widest table row they produce:
    //   | ID       | Title                  | Handled By             | Priority | Category       | Status                   |
    //   2+10+3+24+3+24+3+10+3+16+3+26+2 = ~104  -> round to 108
    // ---------------------------------------------------------------
    String W = "============================================================================================================";
    String D = "------------------------------------------------------------------------------------------------------------";

}
