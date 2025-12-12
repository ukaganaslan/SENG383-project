package com.kidtask.model;
public class Child extends User {
    private int totalPoints;
    public Child(String id, String name, String email, int totalPoints) {
        super(id, name, email); this.totalPoints = totalPoints;
    }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int p) { this.totalPoints = p; }
    @Override public String toString() { return getName(); } // ComboBox için
}