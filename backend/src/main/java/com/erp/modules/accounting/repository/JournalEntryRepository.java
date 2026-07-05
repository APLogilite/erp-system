package com.erp.modules.accounting.repository;

import com.erp.modules.accounting.entity.JournalEntry;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
  Optional<JournalEntry> findByDocumentNo(String documentNo);
}
