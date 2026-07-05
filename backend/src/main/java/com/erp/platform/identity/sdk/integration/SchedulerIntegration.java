package com.erp.platform.identity.sdk.integration;

import com.erp.platform.identity.sdk.IdentityClient;
import org.springframework.stereotype.Component;

@Component
public class SchedulerIntegration {

    private final IdentityClient identityClient;

    public SchedulerIntegration(IdentityClient identityClient) {
        this.identityClient = identityClient;
    }

    public boolean canExecuteScheduledTask(String taskName, String runByUserId) {
        return identityClient.hasPermission(runByUserId, "SCHEDULER", taskName, "EXECUTE");
    }

    public boolean isAdminUser(String userId) {
        return identityClient.isAdmin(userId);
    }
}
