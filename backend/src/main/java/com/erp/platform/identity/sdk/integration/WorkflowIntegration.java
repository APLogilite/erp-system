package com.erp.platform.identity.sdk.integration;

import com.erp.platform.identity.sdk.provider.PermissionProvider;
import org.springframework.stereotype.Component;

@Component
public class WorkflowIntegration {

    private final PermissionProvider permissionProvider;

    public WorkflowIntegration(PermissionProvider permissionProvider) {
        this.permissionProvider = permissionProvider;
    }

    public boolean canTransition(String workflowName, String fromState, String toState) {
        return permissionProvider.hasPermission("WORKFLOW", workflowName, toState);
    }

    public boolean canApprove(String workflowName) {
        return permissionProvider.hasPermission("WORKFLOW", workflowName, "APPROVE");
    }

    public boolean canReject(String workflowName) {
        return permissionProvider.hasPermission("WORKFLOW", workflowName, "REJECT");
    }
}
