package com.coworking.space.authenticationservice.controllers;

import com.coworking.space.authenticationservice.dto.request.LoginRequest;
import com.coworking.space.authenticationservice.dto.request.RefreshRequest;
import com.coworking.space.authenticationservice.dto.request.SingUpRequest;
import com.coworking.space.authenticationservice.dto.response.AuthResponse;
import com.coworking.space.authenticationservice.dto.response.RegistrationStatusResponse;
import com.coworking.space.authenticationservice.event.registration.UserRegistrationWriter;
import com.coworking.space.authenticationservice.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService userService;
    private final UserRegistrationWriter userRegistrationOutboxWriter;

    @PostMapping("/signup")
    public ResponseEntity<RegistrationStatusResponse> register(@RequestBody @Valid SingUpRequest userRequest) {
        var response = userRegistrationOutboxWriter.register(userRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest userRequest) {
        var response = userService.authenticate(userRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> accessToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        var response = userService.accessToken(refreshRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/status/{registrationId}")
    public ResponseEntity<RegistrationStatusResponse> checkRegistrationResponse(@PathVariable UUID registrationId) {
        var response  = userService.checkRegistrationStatus(registrationId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
