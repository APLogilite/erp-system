package com.erp.modules.common.repository;

import com.erp.modules.common.entity.DocumentSequence;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSequenceRepository extends JpaRepository<DocumentSequence, UUID> {

  /**
   * Finds a sequence by name and tenant with a pessimistic write lock.
   * This prevents duplicate numbers under concurrent access.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM DocumentSequence s WHERE s.sequenceName = :name AND s.tenantId = :tenantId")
  Optional<DocumentSequence> findForUpdate(@Param("name") String name, @Param("tenantId") UUID tenantId);

  /** Reads a sequence without locking (for peek). */
  Optional<DocumentSequence> findBySequenceNameAndTenantId(String sequenceName, UUID tenantId);
}
