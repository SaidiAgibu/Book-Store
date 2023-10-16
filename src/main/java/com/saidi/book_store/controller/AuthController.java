package com.saidi.book_store.controller;

import com.saidi.book_store.dto.LoginDto;
import com.saidi.book_store.models.User;
import com.saidi.book_store.requests.AuthResponse;
import com.saidi.book_store.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Data
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        return  userService.registerUser(user);
    }

    @GetMapping
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        return userService.verifyAccount(token);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginDto loginDto) {
        return userService.loginUser(loginDto);
    }
}
