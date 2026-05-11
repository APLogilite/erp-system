package com.erp.modules.product.service;

import com.erp.common.base.BaseService;
import com.erp.modules.product.entity.Product;
import com.erp.modules.product.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Product service.
 */
@Service
public class ProductService extends BaseService<Product> {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  protected JpaRepository<Product, UUID> getRepository() {
    return productRepository;
  }

  @Override
  protected void beforeCreate(Product entity) {
    validateSkuUniqueness(entity.getSku());
  }

  @Override
  protected void beforeUpdate(Product newEntity, Product existingEntity) {
    if (!newEntity.getSku().equals(existingEntity.getSku())) {
      Optional<Product> existingWithSku = productRepository.findBySku(newEntity.getSku());
      if (existingWithSku.isPresent() && !existingWithSku.get().getId().equals(newEntity.getId())) {
        throw new IllegalArgumentException("SKU must be unique");
      }
    }
  }

  private void validateSkuUniqueness(String sku) {
    if (productRepository.findBySku(sku).isPresent()) {
      throw new IllegalArgumentException("SKU must be unique");
    }
  }
}
