package com.example.denguetracebackend.user.service;

import com.example.denguetracebackend.common.enums.Role;
import com.example.denguetracebackend.common.event.UserRegisteredEvent;
import com.example.denguetracebackend.common.exception.DuplicateResourceException;
import com.example.denguetracebackend.common.exception.InvalidCredentialsException;
import com.example.denguetracebackend.common.exception.ResourceNotFoundException;
import com.example.denguetracebackend.common.security.JwtUtil;
import com.example.denguetracebackend.district.entity.District;
import com.example.denguetracebackend.district.repository.DistrictRepository;
import com.example.denguetracebackend.user.dto.*;
import com.example.denguetracebackend.user.entity.User;
import com.example.denguetracebackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with email " + request.email() + " already exists");
        }

        District district = null;
        if (request.districtId() != null) {
            district = districtRepository.findById(request.districtId())
                    .orElseThrow(() -> ResourceNotFoundException.of("District", request.districtId()));
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .district(district)
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);
        eventPublisher.publishEvent(new UserRegisteredEvent(this, saved));

        String accessToken = jwtUtil.generateAccessToken(saved.getId(), saved.getEmail(), saved.getAuthorities().stream().toList());
        String refreshToken = jwtUtil.generateRefreshToken(saved.getId(), saved.getEmail());

        return new AuthResponseDTO(accessToken, refreshToken, UserMapper.toResponse(saved));
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (Exception ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getAuthorities().stream().toList());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());

        return new AuthResponseDTO(accessToken, refreshToken, UserMapper.toResponse(user));
    }

    public AuthResponseDTO refresh(String refreshToken) {
        try {
            if (!"refresh".equals(jwtUtil.extractType(refreshToken))) {
                throw new InvalidCredentialsException("Not a refresh token");
            }

            String email = jwtUtil.extractEmail(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

            if (!jwtUtil.isTokenValid(refreshToken, email)) {
                throw new InvalidCredentialsException("Refresh token expired or invalid");
            }

            String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getAuthorities().stream().toList());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getEmail());
            return new AuthResponseDTO(newAccessToken, newRefreshToken, UserMapper.toResponse(user));
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }
    }
}
