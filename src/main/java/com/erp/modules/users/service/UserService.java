package com.erp.modules.users.service;

import com.erp.common.base.BaseService;
import com.erp.modules.users.entity.UserEntity;
import com.erp.modules.users.repository.UserRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Users service placeholder.
 * Business logic is intentionally not implemented.
 */
@Service
public class UserService extends BaseService<UserEntity> {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  protected JpaRepository<UserEntity, UUID> getRepository() {
    return userRepository;
  }
}
