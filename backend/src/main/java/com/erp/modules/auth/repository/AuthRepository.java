package com.erp.modules.auth.repository;

import com.erp.modules.auth.entity.AuthEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<AuthEntity, UUID> {
}
