package com.erp.modules.product.repository;

import com.erp.modules.product.entity.ProductCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    Optional<ProductCategory> findByCode(String code);
    List<ProductCategory> findByIsActiveTrue();
    List<ProductCategory> findByParentId(UUID parentId);
}
