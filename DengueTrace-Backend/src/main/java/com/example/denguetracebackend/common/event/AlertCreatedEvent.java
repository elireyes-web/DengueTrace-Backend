package com.example.denguetracebackend.common.event;

import com.example.denguetracebackend.alert.entity.Alert;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AlertCreatedEvent extends ApplicationEvent {

    private final Alert alert;

    public AlertCreatedEvent(Object source, Alert alert) {
        super(source);
        this.alert = alert;
    }
}
