package com.erp.modules.metadata.repository;

import com.erp.modules.metadata.entity.SysWindow;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysWindowRepository extends JpaRepository<SysWindow, UUID> {
  Optional<SysWindow> findByName(String name);
}
