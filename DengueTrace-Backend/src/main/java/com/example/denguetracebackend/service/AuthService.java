package com.example.denguetracebackend.service;

import com.example.denguetracebackend.security.JwtService;
import com.example.denguetracebackend.entity.DniHasher;
import com.example.denguetracebackend.user.Usuario;
import com.example.denguetracebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static com.example.denguetracebackend.auth.AuthDtos.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final DniHasher dniHasher;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registrar(RegisterRequest req) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        if (!dniHasher.esFormatoValido(req.dni())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DNI inválido");
        }

        String dniHash = dniHasher.hash(req.dni());
        if (usuarioRepository.existsByDniHash(dniHash)) {
            // Este DNI ya tiene una cuenta -> exactamente la fricción anti-spam que buscaba el equipo
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta asociada a este DNI");
        }

        Usuario usuario = Usuario.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .dniHash(dniHash)
                .ubigeoDistrito(req.ubigeoDistrito())
                .telefono(req.telefono())
                .build();

        usuarioRepository.save(usuario);

        return construirRespuesta(usuario);
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        Usuario usuario = usuarioRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        return construirRespuesta(usuario);
    }

    public AuthResponse refrescar(RefreshRequest req) {
        String token = req.refreshToken();

        if (!jwtService.esRefreshToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de refresco inválido");
        }

        String email = jwtService.extraerEmail(token);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (!jwtService.esValido(token, usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de refresco expirado o inválido");
        }

        return construirRespuesta(usuario);
    }

    private AuthResponse construirRespuesta(Usuario usuario) {
        return new AuthResponse(
                jwtService.generarAccessToken(usuario),
                jwtService.generarRefreshToken(usuario),
                usuario.getEmail(),
                usuario.getRole().name()
        );
    }
}
