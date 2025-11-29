package com.kidtask.model;

/**
 * Represents a teacher user who can view all children's progress and manage tasks.
 */
public class Teacher extends User {

    public Teacher() {
        super();
    }

    public Teacher(String id, String name, String email) {
        super(id, name, email);
    }
}