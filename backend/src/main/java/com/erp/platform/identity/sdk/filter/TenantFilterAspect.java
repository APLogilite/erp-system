package com.erp.platform.identity.sdk.filter;

import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.sdk.annotation.EnableTenantFilter;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantFilterAspect {

    private final EntityManager entityManager;

    public TenantFilterAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Before("@annotation(enableTenantFilter)")
    public void enableFilters(EnableTenantFilter enableTenantFilter) {
        var ctx = RuntimeContextHolder.get();
        if (ctx == null) return;

        Session session = entityManager.unwrap(Session.class);
        if (ctx.getTenantId() != null) {
            session.enableFilter("tenantFilter").setParameter("tenantId", ctx.getTenantId());
        }
        if (ctx.getOrganizationId() != null) {
            session.enableFilter("organizationFilter").setParameter("organizationId", ctx.getOrganizationId());
        }
        if (ctx.getCompanyId() != null) {
            session.enableFilter("companyFilter").setParameter("companyId", ctx.getCompanyId());
        }
    }

    @After("@annotation(com.erp.platform.identity.sdk.annotation.EnableTenantFilter)")
    public void disableFilters() {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter("tenantFilter");
        session.disableFilter("organizationFilter");
        session.disableFilter("companyFilter");
    }
}
