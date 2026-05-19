package com.primeestate.model;

import java.sql.Timestamp;

/**
 * OOP - Inheritance: Abstract base class shared by all domain entities.
 * OOP - Encapsulation: Used encapsulation to keep their fields private and expose them through getters and setters.
 * OOP - Polymorphism: Abstract methods getDisplayName() and validate() are overridden differently in each subclass.
 */
public abstract class BaseEntity {

    private int       id;
    private Timestamp createdAt;

    public int       getId()        { return id; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(int id)                   { this.id        = id; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Polymorphic: each subclass returns a human-readable label for itself
    public abstract String getDisplayName();

    // Polymorphic: each subclass validates its own required fields; null = valid
    public abstract String validate();
}
