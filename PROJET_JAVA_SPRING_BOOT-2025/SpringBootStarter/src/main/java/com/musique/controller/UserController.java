package com.musique.controller;

import com.musique.model.User;
import com.musique.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

/**
 * Controller for user registration and profile management.
 */
@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Show registration form
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Process registration
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        // Check if email is already used
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("emailError", "This email is already registered");
            return "register";
        }

        // Encode password and set default role matching DB enum
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("client");

        userService.save(user);

        // Always redirect to login after successful registration
        return "redirect:/login?registered";
    }

    /**
     * Show user profile
     */
    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Get the authenticated user
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        User user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Update user profile
     */
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("user") User userForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "profile";
        }

        // Get the authenticated user
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        User user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Update only allowed fields
        user.setName(userForm.getName());
        
        // If password is provided, update it
        if (userForm.getPassword() != null && !userForm.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        }
        
        userService.save(user);
        
        model.addAttribute("successMessage", "Profile updated successfully");
        return "profile";
    }
}
