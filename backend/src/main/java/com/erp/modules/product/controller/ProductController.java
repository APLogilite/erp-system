package com.erp.modules.product.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.product.dto.ProductRequestDTO;
import com.erp.modules.product.dto.ProductResponseDTO;
import com.erp.modules.product.entity.Product;
import com.erp.modules.product.service.ProductService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> create(@RequestBody ProductRequestDTO dto) {
        Product entity = mapToEntity(dto);
        Product saved = productService.create(entity);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(saved), "Product created."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAll() {
        List<ProductResponseDTO> list = productService.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Products retrieved."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getById(@PathVariable UUID id) {
        Product entity = productService.findByIdOrThrow(id);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(entity), "Product retrieved."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> update(@PathVariable UUID id, @RequestBody ProductRequestDTO dto) {
        Product existing = productService.findByIdOrThrow(id);
        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setSku(dto.getSku());
        existing.setBarcode(dto.getBarcode());
        existing.setUom(dto.getUom());
        existing.setProductType(dto.getProductType());
        existing.setIsStocked(dto.getIsStocked());
        existing.setIsSold(dto.getIsSold());
        existing.setIsPurchased(dto.getIsPurchased());
        existing.setCategoryId(dto.getCategoryId());
        Product updated = productService.update(existing);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(updated), "Product updated."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Product deleted."));
    }

    private Product mapToEntity(ProductRequestDTO dto) {
        Product entity = new Product();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setSku(dto.getSku());
        entity.setBarcode(dto.getBarcode());
        entity.setUom(dto.getUom());
        entity.setProductType(dto.getProductType());
        entity.setIsStocked(dto.getIsStocked());
        entity.setIsSold(dto.getIsSold());
        entity.setIsPurchased(dto.getIsPurchased());
        entity.setCategoryId(dto.getCategoryId());
        return entity;
    }

    private ProductResponseDTO mapToResponse(Product entity) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setSku(entity.getSku());
        dto.setBarcode(entity.getBarcode());
        dto.setUom(entity.getUom());
        dto.setProductType(entity.getProductType());
        dto.setIsStocked(entity.getIsStocked());
        dto.setIsSold(entity.getIsSold());
        dto.setIsPurchased(entity.getIsPurchased());
        dto.setIsActive(entity.getIsActive());
        dto.setCategoryId(entity.getCategoryId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
