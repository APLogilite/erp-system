package com.erp.modules.product.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.product.dto.ProductCategoryDTO;
import com.erp.modules.product.entity.ProductCategory;
import com.erp.modules.product.service.ProductCategoryService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/product-categories")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductCategoryDTO>> create(@RequestBody ProductCategoryDTO dto) {
        ProductCategory entity = mapToEntity(dto);
        ProductCategory saved = productCategoryService.create(entity);
        return ResponseEntity.ok(ApiResponse.success(mapToDTO(saved), "Category created."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductCategoryDTO>>> getAll() {
        List<ProductCategoryDTO> list = productCategoryService.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Categories retrieved."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryDTO>> getById(@PathVariable UUID id) {
        ProductCategory entity = productCategoryService.findByIdOrThrow(id);
        return ResponseEntity.ok(ApiResponse.success(mapToDTO(entity), "Category retrieved."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductCategoryDTO>> update(@PathVariable UUID id, @RequestBody ProductCategoryDTO dto) {
        ProductCategory existing = productCategoryService.findByIdOrThrow(id);
        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setParentId(dto.getParentId());
        ProductCategory updated = productCategoryService.update(existing);
        return ResponseEntity.ok(ApiResponse.success(mapToDTO(updated), "Category updated."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        productCategoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Category deleted."));
    }

    private ProductCategory mapToEntity(ProductCategoryDTO dto) {
        ProductCategory entity = new ProductCategory();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setParentId(dto.getParentId());
        return entity;
    }

    private ProductCategoryDTO mapToDTO(ProductCategory entity) {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setParentId(entity.getParentId());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
