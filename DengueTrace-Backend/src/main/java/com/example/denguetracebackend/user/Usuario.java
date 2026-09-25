package com.example.denguetracebackend.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "dni_hash")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;


    @Column(name = "dni_hash", nullable = false, unique = true, length = 64)
    private String dniHash;

    @Column(nullable = false)
    private String ubigeoDistrito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CanalNotificacion canalNotificacion = CanalNotificacion.PUSH;

    private String telefono;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    void prePersist() {
        this.creadoEn = LocalDateTime.now();
    }

    public enum CanalNotificacion { PUSH, SMS, AMBOS }
}
