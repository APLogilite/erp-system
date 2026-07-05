package com.erp.platform.identity.sdk.integration;

import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.event.IdentityEventPublisher;
import com.erp.platform.identity.event.IdentityEventType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditIntegration {

    private final IdentityEventPublisher eventPublisher;

    public AuditIntegration(IdentityEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void recordAction(String eventTypeName, String details) {
        var ctx = RuntimeContextHolder.get();
        IdentityEventType eventType = IdentityEventType.valueOf(eventTypeName);
        eventPublisher.publish(eventType,
                ctx != null ? ctx.getUserId() : null,
                ctx != null ? ctx.getUsername() : null,
                null, null, null,
                null, details);
    }

    public void recordChange(String eventTypeName, String oldValue, String newValue) {
        var ctx = RuntimeContextHolder.get();
        IdentityEventType eventType = IdentityEventType.valueOf(eventTypeName);
        eventPublisher.publish(eventType,
                ctx != null ? ctx.getUserId() : null,
                ctx != null ? ctx.getUsername() : null,
                null, null, null,
                oldValue, newValue);
    }
}
