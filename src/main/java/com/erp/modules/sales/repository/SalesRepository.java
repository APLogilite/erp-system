package com.erp.modules.sales.repository;

import com.erp.modules.sales.entity.SalesEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<SalesEntity, UUID> {
}
