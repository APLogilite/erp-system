package com.erp.modules.businesspartner.repository;

import com.erp.modules.businesspartner.entity.Address;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByBusinessPartnerId(UUID businessPartnerId);
    List<Address> findByIsActiveTrue();
}
