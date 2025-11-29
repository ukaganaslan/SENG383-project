package com.kidtask.model;

/**
 * Represents a task assigned to a child.
 * Updated according to requirements: Includes Due Date and Rating.
 */
public class Task {
    private String title;
    private String description;
    private int points;
    private String dueDate; // Format: YYYY-MM-DD
    private int rating;     // 0 if not rated, 1-5 scale
    private TaskStatus status;
    private Child assignee;

    public Task() {
    }

    public Task(String title, String description, int points, String dueDate, TaskStatus status, Child assignee) {
        this.title = title;
        this.description = description;
        this.points = points;
        this.dueDate = dueDate;
        this.status = status;
        this.assignee = assignee;
        this.rating = 0; // Default no rating
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public Child getAssignee() { return assignee; }
    public void setAssignee(Child assignee) { this.assignee = assignee; }
}