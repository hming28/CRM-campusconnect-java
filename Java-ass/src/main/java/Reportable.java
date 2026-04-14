/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.main;

public interface Reportable {
    /**
     * Returns a single-line summary suitable for reports and listings.
     * Example:
     *   Student → "2600001 | Lee Hao Ming | Bachelor of Data Science | Active"
     *   Staff   → "ST2001  | Ahmad Fauzi  | IT Staff                 | IT"
     */
    String generateSummary();
}