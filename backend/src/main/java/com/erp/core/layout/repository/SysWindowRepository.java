package com.erp.core.layout.repository;

import com.erp.core.layout.entity.SysWindow;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysWindowRepository extends JpaRepository<SysWindow, UUID> {
  Optional<SysWindow> findByName(String name);
}
