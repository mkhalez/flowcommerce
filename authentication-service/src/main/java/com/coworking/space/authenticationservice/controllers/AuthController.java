package com.coworking.space.authenticationservice.controllers;

import com.coworking.space.authenticationservice.dto.request.UserRequest;
import com.coworking.space.authenticationservice.dto.response.AuthResponse;
import com.coworking.space.authenticationservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(UserRequest user) {
        var response = userService.register(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
