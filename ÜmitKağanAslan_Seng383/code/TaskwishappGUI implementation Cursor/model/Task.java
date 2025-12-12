package com.kidtask.model;
public class Task {
    private String title, description, dueDate;
    private int points, rating;
    private TaskStatus status;
    private Child assignee;

    public Task(String t, String d, int p, String dd, TaskStatus s, Child c) {
        this.title=t; this.description=d; this.points=p; this.dueDate=dd; this.status=s; this.assignee=c;
    }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPoints() { return points; }
    public String getDueDate() { return dueDate; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus s) { this.status = s; }
    public Child getAssignee() { return assignee; }
    public int getRating() { return rating; }
    public void setRating(int r) { this.rating = r; }
    @Override public String toString() { return title; }
}