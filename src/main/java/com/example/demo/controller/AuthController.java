package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;

@Controller
public class AuthController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

	@GetMapping("/login")
	public String login() {
		return "login";
	}

    // Simple DTO for signup
    public static class SignupForm {
        private String username;
        private String password;
        private String confirmPassword;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }

    private final UserDetailsManager userManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDetailsManager userManager, PasswordEncoder passwordEncoder) {
        this.userManager = userManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("form", new SignupForm());
        model.addAttribute("error", null);
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute("form") SignupForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Please correct the errors");
            return "signup";
        }
        if (form.getUsername() == null || form.getUsername().trim().isEmpty() ||
            form.getPassword() == null || form.getPassword().length() < 4 ||
            form.getConfirmPassword() == null || form.getConfirmPassword().length() < 4) {
            model.addAttribute("error", "All fields are required and must be at least 4 characters");
            return "signup";
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "signup";
        }
        if (userManager.userExists(form.getUsername())) {
            model.addAttribute("error", "Username already exists");
            return "signup";
        }
        userManager.createUser(
                User.withUsername(form.getUsername())
                        .password(passwordEncoder.encode(form.getPassword()))
                        .roles("CUSTOMER")
                        .build()
        );
        return "redirect:/login";
    }
}


