package com.erp.modules.accounting.repository;

import com.erp.modules.accounting.entity.JournalEntryLine;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLine, UUID> {
  List<JournalEntryLine> findByJournalEntryId(UUID journalEntryId);
  void deleteByJournalEntryId(UUID journalEntryId);
}
