package com.metapulse.accountsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public User registerUser(String name, String password) {
        // Check if username is already taken
        if (userRepository.findByName(name) != null) {
            throw new RuntimeException("Username already exists");
        }

        // Hash the password before saving
        String hashedPassword = new BCryptPasswordEncoder().encode(password);

        // Create and save the user
        User user = new User();
        user.setName(name);
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public User authenticateUser(String name, String password) {
        // Retrieve the user from the database
        User user = userRepository.findByName(name);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Check if the user exists and the password matches
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }


}
