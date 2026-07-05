package com.erp.modules.businesspartner.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.businesspartner.dto.BusinessPartnerRequestDTO;
import com.erp.modules.businesspartner.dto.BusinessPartnerResponseDTO;
import com.erp.modules.businesspartner.entity.BusinessPartner;
import com.erp.modules.businesspartner.service.BusinessPartnerService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/business-partners")
public class BusinessPartnerController {

    private final BusinessPartnerService businessPartnerService;

    public BusinessPartnerController(BusinessPartnerService businessPartnerService) {
        this.businessPartnerService = businessPartnerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BusinessPartnerResponseDTO>> create(@RequestBody BusinessPartnerRequestDTO dto) {
        BusinessPartner entity = mapToEntity(dto);
        BusinessPartner saved = businessPartnerService.create(entity);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(saved), "Business partner created."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BusinessPartnerResponseDTO>>> getAll() {
        List<BusinessPartnerResponseDTO> list = businessPartnerService.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Business partners retrieved."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessPartnerResponseDTO>> getById(@PathVariable UUID id) {
        BusinessPartner entity = businessPartnerService.findByIdOrThrow(id);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(entity), "Business partner retrieved."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessPartnerResponseDTO>> update(@PathVariable UUID id, @RequestBody BusinessPartnerRequestDTO dto) {
        BusinessPartner existing = businessPartnerService.findByIdOrThrow(id);
        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setPartnerType(dto.getPartnerType());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setMobile(dto.getMobile());
        existing.setWebsite(dto.getWebsite());
        existing.setTaxId(dto.getTaxId());
        existing.setIsCustomer(dto.getIsCustomer());
        existing.setIsVendor(dto.getIsVendor());
        existing.setIsEmployee(dto.getIsEmployee());
        BusinessPartner updated = businessPartnerService.update(existing);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(updated), "Business partner updated."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        businessPartnerService.delete(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Business partner deleted."));
    }

    private BusinessPartner mapToEntity(BusinessPartnerRequestDTO dto) {
        BusinessPartner entity = new BusinessPartner();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setPartnerType(dto.getPartnerType());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setMobile(dto.getMobile());
        entity.setWebsite(dto.getWebsite());
        entity.setTaxId(dto.getTaxId());
        entity.setIsCustomer(dto.getIsCustomer());
        entity.setIsVendor(dto.getIsVendor());
        entity.setIsEmployee(dto.getIsEmployee());
        return entity;
    }

    private BusinessPartnerResponseDTO mapToResponse(BusinessPartner entity) {
        BusinessPartnerResponseDTO dto = new BusinessPartnerResponseDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setPartnerType(entity.getPartnerType());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setMobile(entity.getMobile());
        dto.setWebsite(entity.getWebsite());
        dto.setTaxId(entity.getTaxId());
        dto.setIsCustomer(entity.getIsCustomer());
        dto.setIsVendor(entity.getIsVendor());
        dto.setIsEmployee(entity.getIsEmployee());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
