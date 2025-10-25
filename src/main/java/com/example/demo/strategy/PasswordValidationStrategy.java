package com.example.demo.strategy;

import com.example.demo.domain.User;

public class PasswordValidationStrategy implements UserValidationStrategy {
    private String errorMessage;

    @Override
    public boolean validate(User user) {
        String password = user.getPassword();
        if (password == null || password.trim().isEmpty()) {
            errorMessage = "Password cannot be empty";
            return false;
        }
        if (password.length() < 8) {
            errorMessage = "Password must be at least 8 characters long";
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            errorMessage = "Password must contain at least one uppercase letter";
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            errorMessage = "Password must contain at least one lowercase letter";
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            errorMessage = "Password must contain at least one number";
            return false;
        }
        if (!password.matches(".*[@#$%^&+=].*")) {
            errorMessage = "Password must contain at least one special character (@#$%^&+=)";
            return false;
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}