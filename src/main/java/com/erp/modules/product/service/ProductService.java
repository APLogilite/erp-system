package com.erp.modules.product.service;

import com.erp.common.base.BaseService;
import com.erp.modules.product.entity.ProductEntity;
import com.erp.modules.product.repository.ProductRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Product service placeholder.
 * Business logic is intentionally not implemented.
 */
@Service
public class ProductService extends BaseService<ProductEntity> {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  protected JpaRepository<ProductEntity, UUID> getRepository() {
    return productRepository;
  }
}
