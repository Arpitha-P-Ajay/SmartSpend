package com.smartspend.service;

import com.smartspend.model.Transaction;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class EmailService {

    private String senderEmail;
    private String senderPassword;

    public EmailService() {
        // Load credentials from the config.properties file
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties props = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            props.load(input);
            this.senderEmail = props.getProperty("EMAIL_USER");
            this.senderPassword = props.getProperty("EMAIL_PASS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendReportEmail(String recipientEmail, List<Transaction> transactions) {
        if (senderEmail == null || senderPassword == null) {
            System.err.println("Email credentials not loaded. Cannot send email.");
            return false;
        }

        String report = generateReport(transactions);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your SmartSpend Monthly Report");
            message.setText(report);

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String generateReport(List<Transaction> transactions) {
        // ... (This method remains the same)
        StringBuilder report = new StringBuilder("Hello,\n\nHere is your financial summary:\n\n");
        double totalIncome = 0;
        double totalExpense = 0;
        for (Transaction t : transactions) {
            if (t instanceof com.smartspend.model.Income) totalIncome += t.getAmount();
            else totalExpense += t.getAmount();
            report.append(String.format("- %s: %s, $%.2f\n", t.getDate(), t.getDescription(), t.getAmount()));
        }
        report.append("\n----------------------------------\n");
        report.append(String.format("Total Income: $%.2f\n", totalIncome));
        report.append(String.format("Total Expense: $%.2f\n", totalExpense));
        report.append(String.format("Final Balance: $%.2f\n", (totalIncome - totalExpense)));
        report.append("\nKeep up the great work!\n\n- The SmartSpend Team");
        return report.toString();
    }
}