package com.erp.platform.identity.sdk.filter;

import jakarta.persistence.*;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@FilterDef(name = "tenantFilter",
        parameters = @ParamDef(name = "tenantId", type = String.class))
@FilterDef(name = "organizationFilter",
        parameters = @ParamDef(name = "organizationId", type = String.class))
@FilterDef(name = "companyFilter",
        parameters = @ParamDef(name = "companyId", type = String.class))
public class TenantFilter {
}
