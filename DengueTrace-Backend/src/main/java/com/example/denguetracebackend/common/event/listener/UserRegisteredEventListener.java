package com.example.denguetracebackend.common.event.listener;

import com.example.denguetracebackend.common.email.EmailService;
import com.example.denguetracebackend.common.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final EmailService emailService;

    @Async("taskExecutor")
    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event.getUser().getEmail(), event.getUser().getFullName());
    }
}
