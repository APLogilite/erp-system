package com.erp.platform.identity.authorization;

import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizationInterceptor {

  private static final Logger log = LoggerFactory.getLogger(AuthorizationInterceptor.class);

  private final PermissionEvaluator permissionEvaluator;

  public AuthorizationInterceptor(PermissionEvaluator permissionEvaluator) {
    this.permissionEvaluator = permissionEvaluator;
  }

  @Before("@annotation(requirePermission)")
  public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null) {
      throw new AuthorizationException("IDENTITY_AUTH_001", "Authentication required");
    }

    permissionEvaluator.checkPermission(
        ctx.getUserId(),
        requirePermission.resourceType(),
        requirePermission.resource(),
        requirePermission.action()
    );

    log.debug("Permission granted: {} {} {} for user {}",
        requirePermission.resourceType(),
        requirePermission.resource(),
        requirePermission.action(),
        ctx.getUsername());
  }
}
