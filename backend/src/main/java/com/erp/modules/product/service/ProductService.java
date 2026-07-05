package com.erp.modules.product.service;

import com.erp.common.base.BaseService;
import com.erp.modules.product.entity.Product;
import com.erp.modules.product.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

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
        if (productRepository.findByCode(entity.getCode()).isPresent()) {
            throw new IllegalArgumentException("Product code must be unique");
        }
        if (entity.getSku() != null && productRepository.findBySku(entity.getSku()).isPresent()) {
            throw new IllegalArgumentException("SKU must be unique");
        }
    }

    @Override
    protected void beforeUpdate(Product newEntity, Product existingEntity) {
        if (!newEntity.getCode().equals(existingEntity.getCode())
                && productRepository.findByCode(newEntity.getCode()).isPresent()) {
            throw new IllegalArgumentException("Product code must be unique");
        }
        if (newEntity.getSku() != null
                && !newEntity.getSku().equals(existingEntity.getSku())
                && productRepository.findBySku(newEntity.getSku()).isPresent()) {
            throw new IllegalArgumentException("SKU must be unique");
        }
    }
}
