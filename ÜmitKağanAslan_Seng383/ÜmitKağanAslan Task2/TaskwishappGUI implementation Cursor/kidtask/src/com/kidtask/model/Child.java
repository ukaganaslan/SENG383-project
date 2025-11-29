package com.kidtask.model;

/**
 * Represents a child user participating in tasks.
 */
public class Child extends User {
    private int totalPoints;

    public Child() {
        super();
    }

    public Child(String id, String name, String email, int totalPoints) {
        super(id, name, email);
        this.totalPoints = totalPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
}