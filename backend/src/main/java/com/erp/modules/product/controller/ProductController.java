package com.erp.modules.product.controller;

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
  public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody ProductRequestDTO dto) {
    Product product = mapToEntity(dto);
    Product saved = productService.create(product);
    ProductResponseDTO response = mapToResponse(saved);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<ProductResponseDTO>> getProducts() {
    List<Product> products = productService.findAll().stream()
        .filter(Product::getIsActive)
        .collect(Collectors.toList());
    List<ProductResponseDTO> responses = products.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable UUID id) {
    Product product = productService.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    if (!product.getIsActive()) {
      return ResponseEntity.notFound().build();
    }
    ProductResponseDTO response = mapToResponse(product);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID id, @RequestBody ProductRequestDTO dto) {
    Product existing = productService.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    existing.setName(dto.getName());
    existing.setSku(dto.getSku());
    existing.setDescription(dto.getDescription());
    existing.setCategory(dto.getCategory());
    existing.setUom(dto.getUom());
    existing.setType(dto.getType());
    existing.setCostPrice(dto.getCostPrice());
    existing.setSalePrice(dto.getSalePrice());
    Product updated = productService.update(existing);
    ProductResponseDTO response = mapToResponse(updated);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
  }

  private Product mapToEntity(ProductRequestDTO dto) {
    Product product = new Product();
    product.setName(dto.getName());
    product.setSku(dto.getSku());
    product.setDescription(dto.getDescription());
    product.setCategory(dto.getCategory());
    product.setUom(dto.getUom());
    product.setType(dto.getType());
    product.setCostPrice(dto.getCostPrice());
    product.setSalePrice(dto.getSalePrice());
    return product;
  }

  private ProductResponseDTO mapToResponse(Product product) {
    ProductResponseDTO dto = new ProductResponseDTO();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setSku(product.getSku());
    dto.setDescription(product.getDescription());
    dto.setCategory(product.getCategory());
    dto.setUom(product.getUom());
    dto.setType(product.getType());
    dto.setCostPrice(product.getCostPrice());
    dto.setSalePrice(product.getSalePrice());
    dto.setCreatedAt(product.getCreatedAt());
    dto.setUpdatedAt(product.getUpdatedAt());
    dto.setIsActive(product.getIsActive());
    return dto;
  }
}
