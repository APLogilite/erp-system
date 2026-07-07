package com.erp.platform.identity.security;

import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.service.RuntimeContextService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ContextFilter extends OncePerRequestFilter {

  private final RuntimeContextService runtimeContextService;

  public ContextFilter(RuntimeContextService runtimeContextService) {
    this.runtimeContextService = runtimeContextService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof JwtPrincipal) {
        JwtPrincipal principal = (JwtPrincipal) auth.getPrincipal();
        RuntimeContext context = runtimeContextService.resolve(principal.getUserId());
        RuntimeContextHolder.set(context);
      }
      filterChain.doFilter(request, response);
    } finally {
      RuntimeContextHolder.clear();
    }
  }
}
