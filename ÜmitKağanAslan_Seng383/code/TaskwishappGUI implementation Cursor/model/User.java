package com.kidtask.model;
public abstract class User {
    private String id, name, email;
    public User() {}
    public User(String id, String name, String email) { this.id=id; this.name=name; this.email=email; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}