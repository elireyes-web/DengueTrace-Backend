package com.example.denguetracebackend.user.controller;

import com.example.denguetracebackend.user.dto.UserResponseDTO;
import com.example.denguetracebackend.user.dto.UserUpdateRequestDTO;
import com.example.denguetracebackend.user.entity.User;
import com.example.denguetracebackend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(userService.getByEmail(principal.getEmail()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(@AuthenticationPrincipal User principal,
                                                               @Valid @RequestBody UserUpdateRequestDTO request) {
        return ResponseEntity.ok(userService.update(principal.getId(), request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }
}
