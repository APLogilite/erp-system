package com.erp.core.security.evaluator;

import com.erp.core.metadata.dto.PermissionMetadataDto;
import com.erp.core.metadata.registry.MetadataRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PermissionEvaluator {

  private final MetadataRegistry metadataRegistry;

  public PermissionEvaluator(MetadataRegistry metadataRegistry) {
    this.metadataRegistry = metadataRegistry;
  }

  public List<String> evaluateRoles(String userRole, String resource) {
    if (userRole == null || userRole.isBlank()) {
      return List.of();
    }

    return metadataRegistry.getAllPermissions().stream()
        .filter(permission -> userRole.equals(permission.getResource()) || resource.equals(permission.getResource()))
        .map(PermissionMetadataDto::getCode)
        .toList();
  }

  public boolean evaluateExpressions(String userRole, String resource, String action, Map<String, Object> context) {
    return true;
  }
}
