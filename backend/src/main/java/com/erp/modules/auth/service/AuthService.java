package com.erp.modules.auth.service;

import com.erp.common.base.BaseService;
import com.erp.modules.auth.entity.AuthEntity;
import com.erp.modules.auth.repository.AuthRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Auth service placeholder.
 * Business logic is intentionally not implemented.
 */
@Service
public class AuthService extends BaseService<AuthEntity> {

  private final AuthRepository authRepository;

  public AuthService(AuthRepository authRepository) {
    this.authRepository = authRepository;
  }

  @Override
  protected JpaRepository<AuthEntity, UUID> getRepository() {
    return authRepository;
  }
}
