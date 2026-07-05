package com.erp.modules.businesspartner.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.businesspartner.dto.AddressDTO;
import com.erp.modules.businesspartner.entity.Address;
import com.erp.modules.businesspartner.repository.AddressRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/addresses")
public class AddressController {

    private final AddressRepository addressRepository;

    public AddressController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressDTO>> create(@RequestBody AddressDTO dto) {
        Address entity = mapToEntity(dto);
        Address saved = addressRepository.save(entity);
        return ResponseEntity.ok(ApiResponse.success(mapToDTO(saved), "Address created."));
    }

    @GetMapping("/by-partner/{partnerId}")
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getByPartner(@PathVariable UUID partnerId) {
        List<AddressDTO> list = addressRepository.findByBusinessPartnerId(partnerId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Addresses retrieved."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        addressRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Address deleted."));
    }

    private Address mapToEntity(AddressDTO dto) {
        Address entity = new Address();
        entity.setBusinessPartnerId(dto.getBusinessPartnerId());
        entity.setAddressType(dto.getAddressType());
        entity.setLine1(dto.getLine1());
        entity.setLine2(dto.getLine2());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(dto.getCountry());
        return entity;
    }

    private AddressDTO mapToDTO(Address entity) {
        AddressDTO dto = new AddressDTO();
        dto.setId(entity.getId());
        dto.setBusinessPartnerId(entity.getBusinessPartnerId());
        dto.setAddressType(entity.getAddressType());
        dto.setLine1(entity.getLine1());
        dto.setLine2(entity.getLine2());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPostalCode(entity.getPostalCode());
        dto.setCountry(entity.getCountry());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
