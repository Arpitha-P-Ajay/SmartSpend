package com.smartspend.model;

import java.time.LocalDate;

/**
 * Represents an expense transaction. Inherits from Transaction
 * and includes a category.
 */
public class Expense extends Transaction {
    private Category category;

    public Expense(LocalDate date, double amount, String description, Category category) {
        super(date, amount, description);
        this.category = category;
    }

    // --- Getter and Setter ---
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}