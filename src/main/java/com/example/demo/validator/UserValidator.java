package com.example.demo.validator;

import com.example.demo.domain.User;
import com.example.demo.strategy.UserValidationStrategy;
import java.util.ArrayList;
import java.util.List;

public class UserValidator {
    private List<UserValidationStrategy> strategies = new ArrayList<>();
    
    public void addStrategy(UserValidationStrategy strategy) {
        strategies.add(strategy);
    }
    
    public ValidationResult validateUser(User user) {
        ValidationResult result = new ValidationResult();
        
        for (UserValidationStrategy strategy : strategies) {
            if (!strategy.validate(user)) {
                result.addError(strategy.getErrorMessage());
            }
        }
        
        return result;
    }
    
    public static class ValidationResult {
        private List<String> errors = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return errors;
        }
    }
}