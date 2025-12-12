package com.kidtask.model;
public class Wish {
    private String name, status;
    private int cost;
    private Child child;

    public Wish(String name, int cost, Child child) {
        this.name = name; this.cost = cost; this.child = child; this.status = "PENDING";
    }
    public String getName() { return name; }
    public int getCost() { return cost; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public Child getChild() { return child; }
    @Override public String toString() { return name; }
}