package com.smartspend.ui;

import com.smartspend.model.*;
import com.smartspend.service.EmailService;
import com.smartspend.service.FileService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrackerFrame extends JFrame {
    private FileService fileService = new FileService();
    private EmailService emailService = new EmailService();
    private List<Transaction> transactions = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JLabel balanceLabel;
    private User currentUser; // The logged-in user

    public TrackerFrame(User user) {
        this.currentUser = user;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        setTitle("SmartSpend - Welcome " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        balanceLabel = new JLabel("Current Balance: $0.00", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(balanceLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addIncomeButton = new JButton("Add Income");
        JButton addExpenseButton = new JButton("Add Expense");
        JButton emailReportButton = new JButton("Email Report");
        buttonPanel.add(addIncomeButton);
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(emailReportButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = {"Date", "Type", "Description", "Category", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);

        loadData();

        // Button Actions
        addIncomeButton.addActionListener(e -> showAddTransactionDialog(true));
        addExpenseButton.addActionListener(e -> showAddTransactionDialog(false));
        emailReportButton.addActionListener(e -> sendReport());

        setLocationRelativeTo(null);
    }
    
    private void sendReport() {
        boolean success = emailService.sendReportEmail(currentUser.getEmail(), transactions);
        if (success) {
            JOptionPane.showMessageDialog(this, "Report sent successfully to " + currentUser.getEmail(), "Email Sent", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send the report. Check console for errors.", "Email Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        transactions = fileService.loadTransactions(currentUser);
        refreshTableAndBalance();
    }

    private void saveData() {
        fileService.saveTransactions(currentUser, transactions);
    }

    private void refreshTableAndBalance() {
        tableModel.setRowCount(0);
        double balance = 0.0;

        for (Transaction t : transactions) {
            String type = (t instanceof Income) ? "Income" : "Expense";
            String category = (t instanceof Expense) ? ((Expense) t).getCategory().getName() : "N/A";
            double amount = t.getAmount();

            if (t instanceof Income) {
                balance += amount;
            } else {
                balance -= amount;
            }

            tableModel.addRow(new Object[]{t.getDate(), type, t.getDescription(), category, String.format("%.2f", amount)});
        }

        balanceLabel.setText(String.format("Current Balance: $%.2f", balance));
    }

    private void showAddTransactionDialog(boolean isIncome) {
        JTextField descriptionField = new JTextField(20);
        JTextField amountField = new JTextField(10);
        JTextField categoryField = new JTextField(15);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        
        if (!isIncome) {
            panel.add(new JLabel("Category:"));
            panel.add(categoryField);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, "Add " + (isIncome ? "Income" : "Expense"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText();
                double amount = Double.parseDouble(amountField.getText());

                if (description.isEmpty() || amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (isIncome) {
                    transactions.add(new Income(LocalDate.now(), amount, description));
                } else {
                    String category = categoryField.getText();
                    if(category.isEmpty()){
                         JOptionPane.showMessageDialog(this, "Category is required.", "Error", JOptionPane.ERROR_MESSAGE);
                         return;
                    }
                    transactions.add(new Expense(LocalDate.now(), amount, description, new Category(category)));
                }

                saveData();
                refreshTableAndBalance();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}