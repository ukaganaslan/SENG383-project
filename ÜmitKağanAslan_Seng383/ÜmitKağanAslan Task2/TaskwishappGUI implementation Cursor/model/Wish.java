package com.kidtask.model;

/**
 * Represents a reward/wish a child can redeem.
 */
public class Wish {
    private String name;
    private int cost;
    private String status; // "PENDING", "APPROVED", "REJECTED"
    private Child child;

    public Wish() {}

    public Wish(String name, int cost, Child child) {
        this.name = name;
        this.cost = cost;
        this.child = child;
        this.status = "PENDING";
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Child getChild() { return child; }
    public void setChild(Child child) { this.child = child; }
}