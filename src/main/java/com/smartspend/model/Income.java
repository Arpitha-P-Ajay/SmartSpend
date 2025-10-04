package com.smartspend.model;

import java.time.LocalDate;

/**
 * Represents an income transaction. Inherits from Transaction.
 */
public class Income extends Transaction {
    public Income(LocalDate date, double amount, String description) {
        super(date, amount, description);
    }
}