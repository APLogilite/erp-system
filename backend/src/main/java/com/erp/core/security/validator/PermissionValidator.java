package com.erp.core.security.validator;

import com.erp.core.security.dto.PermissionMetadataDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PermissionValidator {

  public void validate(PermissionMetadataDto permission) {
    List<String> errors = new ArrayList<>();

    if (permission == null) {
      throw new IllegalArgumentException("Permission metadata cannot be null");
    }
    if (permission.getCode() == null || permission.getCode().isBlank()) {
      errors.add("Permission code cannot be empty");
    }
    if (permission.getResource() == null || permission.getResource().isBlank()) {
      errors.add("Permission resource cannot be empty");
    }
    if (permission.getPermissionLevel() == null) {
      errors.add("Permission level cannot be null");
    }

    if (!errors.isEmpty()) {
      throw new IllegalArgumentException(String.join("; ", errors));
    }
  }
}
