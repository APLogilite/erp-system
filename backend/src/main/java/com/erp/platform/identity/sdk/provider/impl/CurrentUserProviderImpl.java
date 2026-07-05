package com.erp.platform.identity.sdk.provider.impl;

import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.entity.UserAccount;
import com.erp.platform.identity.repository.UserAccountRepository;
import com.erp.platform.identity.sdk.provider.CurrentUserProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CurrentUserProviderImpl implements CurrentUserProvider {

    private final UserAccountRepository userAccountRepository;

    public CurrentUserProviderImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public Optional<UserAccount> getCurrentUser() {
        String id = getCurrentUserId();
        if (id == null) return Optional.empty();
        return userAccountRepository.findById(UUID.fromString(id));
    }

    @Override
    public String getCurrentUserId() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getUserId).map(Object::toString).orElse(null);
    }

    @Override
    public String getCurrentUsername() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getUsername)
                .orElse(null);
    }

    @Override
    public String getCurrentUserEmail() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getEmail)
                .orElse(null);
    }

    @Override
    public String getCurrentUserDisplayName() {
        RuntimeContext ctx = RuntimeContextHolder.get();
        if (ctx == null) return null;
        return ctx.getDisplayName() != null ? ctx.getDisplayName() : ctx.getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        return RuntimeContextHolder.get() != null;
    }
}
