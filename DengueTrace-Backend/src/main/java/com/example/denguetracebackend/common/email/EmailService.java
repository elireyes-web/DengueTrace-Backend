package com.example.denguetracebackend.common.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderAddress;

    @Async("taskExecutor")
    public void sendWelcomeEmail(String to, String fullName) {
        send(to, "Bienvenido a DengueTrace", EmailTemplates.welcome(fullName));
    }

    // This is synchronous because the alert listener itself already runs asynchronously.
    // Returning a result prevents recording SENT when the SMTP server rejects the email.
    public boolean sendAlertEmail(String to, String districtName, String message) {
        if (to == null || to.isBlank()) return false;
        String html = EmailTemplates.alert(districtName, message);
        return send(to, "Alerta de riesgo de Dengue - " + districtName, html);
    }

    private boolean send(String to, String subject, String html) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            if (senderAddress != null && !senderAddress.isBlank()) helper.setFrom(senderAddress);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mimeMessage);
            log.info("Email accepted by SMTP provider for recipient {}", to);
            return true;
        } catch (MessagingException | MailException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
