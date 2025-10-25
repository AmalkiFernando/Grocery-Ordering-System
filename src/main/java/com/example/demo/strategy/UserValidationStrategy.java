package com.example.demo.strategy;

import com.example.demo.domain.User;

public interface UserValidationStrategy {
    boolean validate(User user);
    String getErrorMessage();
}