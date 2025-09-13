package com.musique.service;

import com.musique.model.User;
import com.musique.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service for user-related operations and Spring Security UserDetailsService implementation.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find all users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Save user
     */
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Delete user by ID
     */
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Count total users
     */
    public long count() {
        return userRepository.count();
    }

    /**
     * Implementation of Spring Security UserDetailsService
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // Map DB role values like 'admin','client','vendeur' to Spring Security authorities 'ROLE_ADMIN', ...
        String normalizedRole = (user.getRole() == null ? "CLIENT" : user.getRole().trim().toUpperCase());
        // Safety: map potential synonyms
        switch (normalizedRole) {
            case "ADMIN":
            case "CLIENT":
            case "VENDEUR":
                break;
            case "USER": // legacy
                normalizedRole = "CLIENT";
                break;
            default:
                normalizedRole = "CLIENT";
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + normalizedRole))
        );
    }
}
