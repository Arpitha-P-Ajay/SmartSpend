package com.smartspend.model;

/**
 * Represents a category for an expense (e.g., Food, Transport).
 */
public class Category {
    private String name;

    public Category(String name) {
        this.name = name;
    }

    // --- Getter and Setter ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}