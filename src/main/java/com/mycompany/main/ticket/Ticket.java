/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main.ticket;

import java.time.LocalDate;

public class Ticket {

    private String ticketId;
    private String studentId;
    private String title;
    private String category;
    private String priority;
    private String description;            // student's problem description
    private String status;                 // Pending | In Progress | Resolved | Closed | Reassignment Requested
    private String handledBy;              // staff ID
    private String response;               // staff's reply
    private String reassignmentReason;     // reason sent to manager
    private Integer rating;                // 1–5 (student feedback)
    private String feedback;               // student comment
    private LocalDate createdDate;         // auto-set on construction
    private LocalDate resolvedDate;        // set when staff marks Resolved

    //constructor
    public Ticket() {}
    public Ticket(String ticketId, String studentId, String title, String category, String priority) {
        this.ticketId = ticketId;
        this.studentId = studentId;
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.status = "Pending";
        this.createdDate = LocalDate.now();
    }

    //getter
    public String getTicketId() {
        return ticketId;
    }
    public String getStudentId() {
        return studentId;
    }
    public String getTitle() {
        return title;
    }
    public String getCategory() {
        return category;
    }
    public String getPriority() {
        return priority;
    }
    public String getDescription() {
        return description;
    }
    public String getStatus() {
        return status;
    }
    public String getHandledBy() {
        return handledBy;
    }
    public String getResponse() {
        return response;
    }
    public String getReassignmentReason() {
        return reassignmentReason;
    }
    public Integer getRating() {
        return rating;
    }
    public String getFeedback() {
        return feedback;
    }
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    public LocalDate getResolvedDate() {
        return resolvedDate;
    }

    //setter
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setHandledBy(String handledBy) {
        this.handledBy = handledBy;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    public void setReassignmentReason(String reason) {
        if (reason == null || reason.trim().isEmpty()
                || "null".equalsIgnoreCase(reason.trim())) {
            this.reassignmentReason = null;
        } else {
            this.reassignmentReason = reason;
        }
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    public void setResolvedDate(LocalDate resolvedDate) {
        this.resolvedDate = resolvedDate;
    }
}