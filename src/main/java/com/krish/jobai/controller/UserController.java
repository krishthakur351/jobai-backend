package com.krish.jobai.controller;

import com.krish.jobai.dto.LoginRequest;
import com.krish.jobai.entity.User;
import com.krish.jobai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // REGISTER API
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // LOGIN API
    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest loginRequest) {

        return userService.loginUser(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
    }

    @GetMapping("/profile")
    public String profile() {
        return "Welcome User! You are authenticated.";
    }
}