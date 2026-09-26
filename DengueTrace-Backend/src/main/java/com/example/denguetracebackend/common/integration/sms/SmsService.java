package com.example.denguetracebackend.common.integration.sms;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sends SMS through Twilio. Credentials come only from environment variables
 * (TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, TWILIO_FROM_NUMBER) - see PATCH_NOTES.md.
 */
@Service
@Slf4j
public class SmsService {

    @Value("${app.external.twilio.account-sid:}")
    private String accountSid;

    @Value("${app.external.twilio.auth-token:}")
    private String authToken;

    @Value("${app.external.twilio.from-number:}")
    private String fromNumber;

    private boolean configured = false;

    @PostConstruct
    public void init() {
        if (accountSid.isBlank() || authToken.isBlank() || fromNumber.isBlank()) {
            log.warn("Twilio credentials not fully configured; SMS notifications are disabled.");
            return;
        }
        Twilio.init(accountSid, authToken);
        configured = true;
        log.info("Twilio client initialized.");
    }

    public boolean sendSms(String toPhoneNumber, String body) {
        if (!configured) {
            log.warn("Twilio not configured; SMS not sent.");
            return false;
        }
        if (toPhoneNumber == null || toPhoneNumber.isBlank()) {
            log.warn("Cannot send SMS: user has no phone number registered.");
            return false;
        }
        try {
            Message.creator(new PhoneNumber(toPhoneNumber), new PhoneNumber(fromNumber), body).create();
            return true;
        } catch (ApiException e) {
            log.error("Twilio SMS failed for {}: {}", toPhoneNumber, e.getMessage());
            return false;
        }
    }
}
