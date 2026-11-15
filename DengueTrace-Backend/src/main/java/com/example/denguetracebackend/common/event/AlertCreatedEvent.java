package com.example.denguetracebackend.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AlertCreatedEvent extends ApplicationEvent {

    private final Long alertId;

    public AlertCreatedEvent(Object source, Long alertId) {
        super(source);
        this.alertId = alertId;
    }
}
