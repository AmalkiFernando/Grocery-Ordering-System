package com.grocery.ordering.Service;

import com.grocery.ordering.Auth.User;
import com.grocery.ordering.Auth.Role;
import com.grocery.ordering.Repository.UserRepository;
import com.grocery.ordering.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    public User registerUser(String name, String email, String password, String userType) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User(name, email, password);
        
        // Assign role based on user type
        Role.RoleName roleName = "admin".equalsIgnoreCase(userType) ? 
            Role.RoleName.ADMIN : Role.RoleName.CUSTOMER;

        Role role = roleRepository.findByName(roleName)
            .orElseGet(() -> roleRepository.save(new Role(roleName)));
        user.setRoles(Set.of(role));
        
        return userRepository.save(user);
    }
    
    public User loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            User found = user.get();
            // Ensure user has at least CUSTOMER role if none assigned
            if (found.getRoles() == null || found.getRoles().isEmpty()) {
                Role fallback = roleRepository.findByName(Role.RoleName.CUSTOMER)
                    .orElseGet(() -> roleRepository.save(new Role(Role.RoleName.CUSTOMER)));
                found.setRoles(Set.of(fallback));
                userRepository.save(found);
            }
            return found;
        }
        throw new RuntimeException("Invalid email or password");
    }
    
    public boolean isAdmin(User user) {
        return user.getRoles().stream()
            .anyMatch(role -> role.getName() == Role.RoleName.ADMIN);
    }
    
    public boolean isCustomer(User user) {
        return user.getRoles().stream()
            .anyMatch(role -> role.getName() == Role.RoleName.CUSTOMER);
    }
}
