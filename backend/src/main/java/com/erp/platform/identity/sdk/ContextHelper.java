package com.erp.platform.identity.sdk;

import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;

import java.util.Optional;

public final class ContextHelper {

    private ContextHelper() {}

    public static Optional<RuntimeContext> getContext() {
        return Optional.ofNullable(RuntimeContextHolder.get());
    }

    public static String tenantId() {
        return getContext().map(RuntimeContext::getTenantId).map(Object::toString).orElse(null);
    }

    public static String organizationId() {
        return getContext().map(RuntimeContext::getOrganizationId).map(Object::toString).orElse(null);
    }

    public static String companyId() {
        return getContext().map(RuntimeContext::getCompanyId).map(Object::toString).orElse(null);
    }

    public static String branchId() {
        return getContext().map(RuntimeContext::getBranchId).map(Object::toString).orElse(null);
    }

    public static String departmentId() {
        return getContext().map(RuntimeContext::getDepartmentId).map(Object::toString).orElse(null);
    }

    public static String userId() {
        return getContext().map(RuntimeContext::getUserId).map(Object::toString).orElse(null);
    }

    public static String sessionId() {
        return getContext().map(RuntimeContext::getSessionId).orElse(null);
    }
}
