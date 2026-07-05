package com.erp.modules.businesspartner.service;

import com.erp.common.base.BaseService;
import com.erp.modules.businesspartner.entity.BusinessPartner;
import com.erp.modules.businesspartner.repository.BusinessPartnerRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessPartnerService extends BaseService<BusinessPartner> {

    private final BusinessPartnerRepository businessPartnerRepository;

    public BusinessPartnerService(BusinessPartnerRepository businessPartnerRepository) {
        this.businessPartnerRepository = businessPartnerRepository;
    }

    @Override
    protected JpaRepository<BusinessPartner, UUID> getRepository() {
        return businessPartnerRepository;
    }

    @Override
    protected void beforeCreate(BusinessPartner entity) {
        if (businessPartnerRepository.findByCode(entity.getCode()).isPresent()) {
            throw new IllegalArgumentException("Business partner code must be unique");
        }
    }

    @Override
    protected void beforeUpdate(BusinessPartner newEntity, BusinessPartner existingEntity) {
        if (!newEntity.getCode().equals(existingEntity.getCode())
                && businessPartnerRepository.findByCode(newEntity.getCode()).isPresent()) {
            throw new IllegalArgumentException("Business partner code must be unique");
        }
    }
}
