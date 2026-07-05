package com.erp.platform.identity.sdk.integration;

import com.erp.platform.identity.sdk.provider.CurrentUserProvider;
import org.springframework.stereotype.Component;

@Component
public class NotificationIntegration {

    private final CurrentUserProvider currentUserProvider;

    public NotificationIntegration(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    public String getNotificationRecipientId() {
        return currentUserProvider.getCurrentUserId();
    }

    public String getNotificationRecipientEmail() {
        return currentUserProvider.getCurrentUserEmail();
    }

    public String getNotificationRecipientName() {
        return currentUserProvider.getCurrentUserDisplayName();
    }
}
