package com.coworking.space.userservice.controllers;

import com.coworking.space.userservice.dto.requests.UserCreateRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import com.coworking.space.userservice.dto.requests.UserUpdateRequest;
import com.coworking.space.userservice.dto.responses.UserResponse;
import com.coworking.space.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private static final String USER_ID_CLAIM_NAME = "userId";

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String authUserId = String.valueOf(jwt.<Integer>getClaim(USER_ID_CLAIM_NAME));
        var response = userService.createUser(request, authUserId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN') or @userUtil.getAuthIdByEmail(#email).equals(authentication.principal.claims['userId'].toString)")
    public ResponseEntity<UserResponse> findByEmail(@RequestParam @NotBlank @Email String email) {
        var response = userService.findByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userUtil.getAuthIdByUserId(#id).equals(authentication.principal.claims['userId'].toString)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
        var response = userService.getUserById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam int limit,
            @RequestParam int page) {
        var response = userService.findAll(limit, page, name, surname);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userUtil.getAuthIdByUserId(#id).equals(authentication.principal.claims['userId'].toString)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable int id,
            @Valid @RequestBody UserUpdateRequest request) {
        var response = userService.updateUser(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable int id) {
        userService.changeStatus(id, true);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable int id) {
        userService.changeStatus(id, false);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
