package com.erp.modules.product.repository;

import com.erp.modules.product.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByCode(String code);
    Optional<Product> findBySku(String sku);
    List<Product> findByIsActiveTrue();
    List<Product> findByCategoryId(UUID categoryId);
    List<Product> findByProductType(String productType);
}
