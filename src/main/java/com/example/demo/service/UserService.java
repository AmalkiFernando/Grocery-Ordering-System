package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.strategy.*;
import com.example.demo.validator.UserValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        
        // Initialize validator with all strategies
        this.userValidator = new UserValidator();
        this.userValidator.addStrategy(new UsernameValidationStrategy());
        this.userValidator.addStrategy(new EmailValidationStrategy());
        this.userValidator.addStrategy(new PasswordValidationStrategy());
    }

    public User createUser(User user) {
        // Validate user using strategies
        UserValidator.ValidationResult validationResult = userValidator.validateUser(user);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Invalid user data: " + 
                String.join(", ", validationResult.getErrors()));
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Save user
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        // Validate user using strategies
        UserValidator.ValidationResult validationResult = userValidator.validateUser(user);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Invalid user data: " + 
                String.join(", ", validationResult.getErrors()));
        }

        return userRepository.save(user);
    }
}