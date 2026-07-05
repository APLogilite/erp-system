package com.erp.modules.businesspartner.repository;

import com.erp.modules.businesspartner.entity.Contact;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    List<Contact> findByBusinessPartnerId(UUID businessPartnerId);
    List<Contact> findByIsPrimaryTrue();
}
