package com.erp.platform.identity.sdk.provider;

import com.erp.platform.identity.entity.UserAccount;

import java.util.Optional;

public interface CurrentUserProvider {
    Optional<UserAccount> getCurrentUser();
    String getCurrentUserId();
    String getCurrentUsername();
    String getCurrentUserEmail();
    String getCurrentUserDisplayName();
    boolean isAuthenticated();
}
