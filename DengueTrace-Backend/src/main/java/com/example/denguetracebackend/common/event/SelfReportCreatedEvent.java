package com.example.denguetracebackend.common.event;

import com.example.denguetracebackend.selfreport.entity.SelfReport;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SelfReportCreatedEvent extends ApplicationEvent {

    private final SelfReport selfReport;

    public SelfReportCreatedEvent(Object source, SelfReport selfReport) {
        super(source);
        this.selfReport = selfReport;
    }
}
