package com.coworking.space.authenticationservice.controllers;

import com.coworking.space.authenticationservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {
    private final UserService userService;
    private static final boolean DISABLE = false;
    private static final boolean ENABLE = true;

    @PatchMapping("/users/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable int id) {
        userService.changeUserStatus(id, DISABLE);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable int id) {
        userService.changeUserStatus(id, ENABLE);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}

