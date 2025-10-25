package com.example.demo.strategy;

import com.example.demo.domain.User;
import java.util.regex.Pattern;

public class EmailValidationStrategy implements UserValidationStrategy {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private String errorMessage;

    @Override
    public boolean validate(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errorMessage = "Email cannot be empty";
            return false;
        }
        if (!Pattern.compile(EMAIL_PATTERN).matcher(user.getEmail()).matches()) {
            errorMessage = "Invalid email format";
            return false;
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}