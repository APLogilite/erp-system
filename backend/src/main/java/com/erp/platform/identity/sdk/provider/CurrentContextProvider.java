package com.erp.platform.identity.sdk.provider;

import com.erp.platform.identity.dto.RuntimeContext;

import java.util.List;
import java.util.Optional;

public interface CurrentContextProvider {
    Optional<RuntimeContext> getCurrentContext();
    String getCurrentTenantId();
    String getCurrentOrganizationId();
    String getCurrentCompanyId();
    String getCurrentBranchId();
    String getCurrentDepartmentId();
    List<String> getCurrentRoles();
    List<String> getCurrentPermissions();
    String getCurrentLanguage();
    String getCurrentTimezone();
}
