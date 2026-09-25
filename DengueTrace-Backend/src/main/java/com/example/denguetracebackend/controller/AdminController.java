package com.example.denguetracebackend.controller;

import com.example.denguetracebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.denguetracebackend.user.Usuario;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    // Ejemplo: solo ADMIN puede listar usuarios (además de la restricción a nivel de ruta en SecurityConfig)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Ejemplo: ADMIN o MODERATOR pueden deshabilitar una cuenta que reporta spam
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/usuarios/{id}/deshabilitar")
    public void deshabilitarUsuario(@PathVariable Long id) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setEnabled(false);
            usuarioRepository.save(u);
        });
    }
}
