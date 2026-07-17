package com.erp.core.layout.repository;

import com.erp.core.layout.entity.SysTable;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysTableRepository extends JpaRepository<SysTable, UUID> {
  Optional<SysTable> findByName(String name);
}
