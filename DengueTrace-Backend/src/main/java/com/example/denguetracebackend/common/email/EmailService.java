package com.example.denguetracebackend.common.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async("taskExecutor")
    public void sendWelcomeEmail(String to, String fullName) {
        String html = EmailTemplates.welcome(fullName);
        send(to, "Bienvenido a DengueTrace", html);
    }

    @Async("taskExecutor")
    public void sendAlertEmail(String to, String districtName, String message) {
        String html = EmailTemplates.alert(districtName, message);
        send(to, "Alerta de riesgo de Dengue - " + districtName, html);
    }

    private void send(String to, String subject, String html) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mimeMessage);
            log.info("Email sent to {} - {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
