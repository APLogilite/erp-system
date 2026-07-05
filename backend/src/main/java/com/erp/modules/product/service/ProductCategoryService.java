package com.erp.modules.product.service;

import com.erp.common.base.BaseService;
import com.erp.modules.product.entity.ProductCategory;
import com.erp.modules.product.repository.ProductCategoryRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductCategoryService extends BaseService<ProductCategory> {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    protected JpaRepository<ProductCategory, UUID> getRepository() {
        return productCategoryRepository;
    }
}
