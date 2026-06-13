package com.krish.jobai.service;

import com.krish.jobai.service.JwtUtil;
import com.krish.jobai.entity.User;
import com.krish.jobai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    // REGISTER USER
    public User registerUser(User user) {

        // Set default role
        user.setRole("USER");
        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );


        return userRepository.save(user);
    }

    // LOGIN USER
    public String loginUser(String email, String password) {

        // Find user by email
        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        // Check if user exists
        if(optionalUser.isEmpty()) {
            return "User not found";
        }

        // Get actual user object
        User user = optionalUser.get();

        // Compare entered password with encrypted password
        boolean isPasswordCorrect =
                passwordEncoder.matches(password, user.getPassword());

        if(isPasswordCorrect) {

            return jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole()
            );

        } else {
            return "Invalid Password";
        }
    }
}