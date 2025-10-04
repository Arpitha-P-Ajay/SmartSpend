package com.smartspend.service;

import com.smartspend.model.User;
import com.smartspend.util.PasswordUtil;

import java.util.List;
import java.util.Optional;

public class AuthService {
    private FileService fileService = new FileService();

    public User login(String username, String password) {
        List<User> users = fileService.loadUsers();
        Optional<User> foundUser = users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();

        if (foundUser.isPresent()) {
            User user = foundUser.get();
            String hashedPassword = PasswordUtil.hashPassword(password);
            if (user.getHashedPassword().equals(hashedPassword)) {
                return user; // Login successful
            }
        }
        return null; // Login failed
    }

    public boolean register(String username, String email, String password) {
        List<User> users = fileService.loadUsers();
        boolean userExists = users.stream().anyMatch(u -> u.getUsername().equals(username));

        if (userExists) {
            return false; // Registration failed: username already taken
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        users.add(new User(username, email, hashedPassword));
        fileService.saveUsers(users);

        return true; // Registration successful
    }
}