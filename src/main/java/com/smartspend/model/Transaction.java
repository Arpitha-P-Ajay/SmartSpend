package com.smartspend.model;

import java.time.LocalDate;

/**
 * An abstract base class for all financial transactions.
 * It holds common properties like date, amount, and description.
 */
public abstract class Transaction {
    private LocalDate date;
    private double amount;
    private String description;

    public Transaction(LocalDate date, double amount, String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    // --- Getters and Setters ---
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}