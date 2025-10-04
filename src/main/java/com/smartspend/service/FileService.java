package com.smartspend.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.smartspend.model.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    private static final String USERS_FILE = "users.csv";

    // --- User Management ---

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(USERS_FILE))) {
            String[] nextLine;
            reader.readNext(); // Skip header
            while ((nextLine = reader.readNext()) != null) {
                users.add(new User(nextLine[0], nextLine[1], nextLine[2]));
            }
        } catch (Exception e) {
            System.out.println("No users file found. A new one will be created upon registration.");
        }
        return users;
    }

    public void saveUsers(List<User> users) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(USERS_FILE))) {
            writer.writeNext(new String[]{"Username", "Email", "HashedPassword"});
            for (User user : users) {
                writer.writeNext(new String[]{user.getUsername(), user.getEmail(), user.getHashedPassword()});
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    // --- Transaction Management (Now user-specific!) ---

    private String getTransactionFileNameFor(User user) {
        return user.getUsername() + "_transactions.csv";
    }

    public List<Transaction> loadTransactions(User user) {
        List<Transaction> transactions = new ArrayList<>();
        String fileName = getTransactionFileNameFor(user);
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            String[] nextLine;
            reader.readNext(); // Skip header
            while ((nextLine = reader.readNext()) != null) {
                LocalDate date = LocalDate.parse(nextLine[0]);
                String type = nextLine[1];
                double amount = Double.parseDouble(nextLine[2]);
                String categoryName = nextLine[3];
                String description = nextLine[4];

                if ("INCOME".equalsIgnoreCase(type)) {
                    transactions.add(new Income(date, amount, description));
                } else {
                    transactions.add(new Expense(date, amount, description, new Category(categoryName)));
                }
            }
        } catch (Exception e) {
            System.out.println("No transactions found for user: " + user.getUsername());
        }
        return transactions;
    }

    public void saveTransactions(User user, List<Transaction> transactions) {
        String fileName = getTransactionFileNameFor(user);
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(new String[]{"Date", "Type", "Amount", "Category", "Description"});
            for (Transaction t : transactions) {
                if (t instanceof Income) {
                    writer.writeNext(new String[]{t.getDate().toString(), "INCOME", String.valueOf(t.getAmount()), "", t.getDescription()});
                } else if (t instanceof Expense) {
                    Expense e = (Expense) t;
                    writer.writeNext(new String[]{e.getDate().toString(), "EXPENSE", String.valueOf(e.getAmount()), e.getCategory().getName(), e.getDescription()});
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions for user " + user.getUsername() + ": " + e.getMessage());
        }
    }
}