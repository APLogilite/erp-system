package com.erp.core.security.mapper;

import com.erp.core.metadata.dto.PermissionMetadataDto;
import com.erp.core.security.dto.PermissionCheckResponseDto;

public class PermissionMapper {

  public static PermissionCheckResponseDto toResponse(PermissionMetadataDto permission) {
    PermissionCheckResponseDto response = new PermissionCheckResponseDto();
    response.setAllowed(true);
    response.setResource(permission.getResource());
    response.setAction(permission.getCode());
    response.setMessage("Permission metadata mapped.");
    return response;
  }
}
