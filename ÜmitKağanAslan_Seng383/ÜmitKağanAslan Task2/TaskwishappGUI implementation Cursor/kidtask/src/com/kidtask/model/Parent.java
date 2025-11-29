package com.kidtask.model;

/**
 * Represents a parent user who manages children's tasks.
 */
public class Parent extends User {

    public Parent() {
        super();
    }

    public Parent(String id, String name, String email) {
        super(id, name, email);
    }
}