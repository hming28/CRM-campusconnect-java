/*
 * UIConstants — shared display constants for all UI module classes.
 * Using an interface allows any class to implement it and gain access
 * to the constants without inheritance, demonstrating weak relationships
 * via Java interfaces as encouraged by the OOP design brief.
 *
 * W  = wide  border  (==) used for section headers / outer frames
 * D  = dash  divider (--) used for sub-section separators
 */
package com.mycompany.main;

public interface UIConstants {

    // ---------------------------------------------------------------
    // Standard width  — used by Login / Staff / Applicant modules
    // Calculated to contain the widest table row they produce:
    //   | ID       | Title                  | Handled By             | Priority | Category       | Status                   |
    // ---------------------------------------------------------------
    String W = "============================================================================================================";
    String D = "------------------------------------------------------------------------------------------------------------";

}
