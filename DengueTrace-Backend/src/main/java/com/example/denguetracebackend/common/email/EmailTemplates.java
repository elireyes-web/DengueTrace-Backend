package com.example.denguetracebackend.common.email;

public final class EmailTemplates {

    private EmailTemplates() {}

    public static String welcome(String fullName) {
        return """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto">
                  <h2 style="color:#c0392b">DengueTrace</h2>
                  <p>Hola %s,</p>
                  <p>Tu cuenta ha sido creada exitosamente. Ya puedes registrar tu distrito,
                  consultar el mapa de riesgo y enviar autorreportes.</p>
                  <p>Equipo DengueTrace</p>
                </div>
                """.formatted(escapeHtml(fullName));
    }

    public static String alert(String districtName, String message) {
        return """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto">
                  <h2 style="color:#c0392b">Alerta de Dengue - %s</h2>
                  <p>%s</p>
                  <p>Por favor toma las medidas preventivas necesarias en tu zona.</p>
                  <p>Equipo DengueTrace</p>
                </div>
                """.formatted(escapeHtml(districtName), escapeHtml(message));
    }
    private static String escapeHtml(String value) {
        return (value == null ? "" : value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
