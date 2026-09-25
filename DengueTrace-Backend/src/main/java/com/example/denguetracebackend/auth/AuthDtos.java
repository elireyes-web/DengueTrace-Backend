package com.example.denguetracebackend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
            @Email @NotBlank String email,

            @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
            @Pattern(
                    regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
                    message = "La contraseña debe incluir mayúscula, minúscula y número"
            )
            String password,

            @NotBlank @Pattern(regexp = "^\\d{8}$", message = "DNI inválido, debe tener 8 dígitos")
            String dni,

            @NotBlank String ubigeoDistrito,

            String telefono // opcional, solo obligatorio si eligen SMS
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record RefreshRequest(@NotBlank String refreshToken) {}

    public record AuthResponse(
            String accessToken,
            String refreshToken,
            String email,
            String role
    ) {}
}
