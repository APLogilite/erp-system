package com.erp.platform.identity.sdk.provider.impl;

import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.sdk.provider.CurrentContextProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class CurrentContextProviderImpl implements CurrentContextProvider {

    @Override
    public Optional<RuntimeContext> getCurrentContext() {
        return Optional.ofNullable(RuntimeContextHolder.get());
    }

    @Override
    public String getCurrentTenantId() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getTenantId).map(Object::toString).orElse(null);
    }

    @Override
    public String getCurrentOrganizationId() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getOrganizationId).map(Object::toString).orElse(null);
    }

    @Override
    public String getCurrentCompanyId() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getCompanyId).map(Object::toString).orElse(null);
    }

    @Override
    public String getCurrentBranchId() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getBranchId).map(Object::toString).orElse(null);
    }

    @Override
    public String getCurrentDepartmentId() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getDepartmentId).map(Object::toString).orElse(null);
    }

    @Override
    public List<String> getCurrentRoles() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getRoles)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<String> getCurrentPermissions() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getPermissions)
                .orElse(Collections.emptyList());
    }

    @Override
    public String getCurrentLanguage() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getLanguage)
                .orElse(null);
    }

    @Override
    public String getCurrentTimezone() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getTimezone)
                .orElse(null);
    }
}
