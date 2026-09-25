package com.example.denguetracebackend.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.regex.Pattern;


@Component
public class DniHasher {

    private static final Pattern DNI_PATTERN = Pattern.compile("^\\d{8}$"); // DNI peruano: 8 dígitos

    private final Mac hmac;

    public DniHasher(@Value("${dni.hash.secret}") String secret) {
        try {
            hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo inicializar DniHasher", e);
        }
    }

    public boolean esFormatoValido(String dni) {
        return dni != null && DNI_PATTERN.matcher(dni).matches();
    }

    public synchronized String hash(String dni) {
        byte[] result = hmac.doFinal(dni.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(result);
    }
}
