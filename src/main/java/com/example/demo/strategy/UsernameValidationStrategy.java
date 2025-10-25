package com.example.demo.strategy;

import com.example.demo.domain.User;

public class UsernameValidationStrategy implements UserValidationStrategy {
    private String errorMessage;

    @Override
    public boolean validate(User user) {
        String username = user.getUsername();
        if (username == null || username.trim().isEmpty()) {
            errorMessage = "Username cannot be empty";
            return false;
        }
        if (username.length() < 3 || username.length() > 50) {
            errorMessage = "Username must be between 3 and 50 characters";
            return false;
        }
        if (!username.matches("^[a-zA-Z0-9_-]+$")) {
            errorMessage = "Username can only contain letters, numbers, underscores and hyphens";
            return false;
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}