package com.erp.modules.businesspartner.repository;

import com.erp.modules.businesspartner.entity.BusinessPartner;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, UUID> {
    Optional<BusinessPartner> findByCode(String code);
    List<BusinessPartner> findByIsActiveTrue();
    List<BusinessPartner> findByPartnerType(String partnerType);
}
