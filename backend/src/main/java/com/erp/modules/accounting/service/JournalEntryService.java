package com.erp.modules.accounting.service;

import com.erp.common.base.BaseService;
import com.erp.modules.accounting.dto.JournalEntryLineRequest;
import com.erp.modules.accounting.dto.JournalEntryRequest;
import com.erp.modules.accounting.entity.Account;
import com.erp.modules.accounting.entity.JournalEntry;
import com.erp.modules.accounting.entity.JournalEntryLine;
import com.erp.modules.accounting.repository.AccountRepository;
import com.erp.modules.accounting.repository.JournalEntryLineRepository;
import com.erp.modules.accounting.repository.JournalEntryRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JournalEntryService extends BaseService<JournalEntry> {

  private final JournalEntryRepository journalEntryRepository;
  private final JournalEntryLineRepository lineRepository;
  private final AccountRepository accountRepository;

  public JournalEntryService(
      JournalEntryRepository journalEntryRepository,
      JournalEntryLineRepository lineRepository,
      AccountRepository accountRepository) {
    this.journalEntryRepository = journalEntryRepository;
    this.lineRepository = lineRepository;
    this.accountRepository = accountRepository;
  }

  @Override
  protected JpaRepository<JournalEntry, UUID> getRepository() {
    return journalEntryRepository;
  }

  @Override
  protected void beforeCreate(JournalEntry entity) {
    if (entity.getDocumentNo() == null) {
      entity.setDocumentNo("GL-" + System.currentTimeMillis());
    }
    if (entity.getDocumentDate() == null) {
      entity.setDocumentDate(LocalDate.now());
    }
    if (entity.getStatus() == null) {
      entity.setStatus("DRAFT");
    }
  }

  @Override
  protected void beforeUpdate(JournalEntry newEntity, JournalEntry existingEntity) {
    String status = existingEntity.getStatus();
    if ("POSTED".equals(status) || "CLOSED".equals(status)) {
      throw new IllegalArgumentException("Cannot modify a " + status + " journal entry");
    }
    newEntity.setDocumentNo(existingEntity.getDocumentNo());
  }

  @Transactional
  public UUID createWithLines(JournalEntryRequest request) {
    JournalEntry entry = new JournalEntry();
    entry.setDocumentDate(request.getDocumentDate() != null ? request.getDocumentDate() : LocalDate.now());
    entry.setDescription(request.getDescription());
    entry.setStatus("DRAFT");

    beforeCreate(entry);
    JournalEntry saved = journalEntryRepository.save(entry);

    if (request.getLines() != null && !request.getLines().isEmpty()) {
      processLines(saved.getId(), request.getLines());
      saved = findByIdOrThrow(saved.getId());
    }

    return saved.getId();
  }

  @Transactional
  public void completeEntry(UUID entryId) {
    JournalEntry entry = findByIdOrThrow(entryId);
    if (!"DRAFT".equals(entry.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT journal entries can be completed");
    }
    List<JournalEntryLine> lines = lineRepository.findByJournalEntryId(entryId);
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("Journal entry must have at least one line");
    }
    double totalDebit = lines.stream().mapToDouble(JournalEntryLine::getDebit).sum();
    double totalCredit = lines.stream().mapToDouble(JournalEntryLine::getCredit).sum();
    if (Math.abs(totalDebit - totalCredit) > 0.001) {
      throw new IllegalArgumentException("Total debit must equal total credit");
    }
    entry.setTotalDebit(totalDebit);
    entry.setTotalCredit(totalCredit);
    entry.setStatus("COMPLETED");
    journalEntryRepository.save(entry);
  }

  @Transactional
  public void postEntry(UUID entryId) {
    JournalEntry entry = findByIdOrThrow(entryId);
    if (!"COMPLETED".equals(entry.getStatus())) {
      throw new IllegalArgumentException("Only COMPLETED journal entries can be posted");
    }
    entry.setStatus("POSTED");
    journalEntryRepository.save(entry);
  }

  @Transactional
  public void closeEntry(UUID entryId) {
    JournalEntry entry = findByIdOrThrow(entryId);
    if (!"POSTED".equals(entry.getStatus())) {
      throw new IllegalArgumentException("Only POSTED journal entries can be closed");
    }
    entry.setStatus("CLOSED");
    journalEntryRepository.save(entry);
  }

  @Transactional
  public void reopenEntry(UUID entryId) {
    JournalEntry entry = findByIdOrThrow(entryId);
    if (!"CLOSED".equals(entry.getStatus())) {
      throw new IllegalArgumentException("Only CLOSED journal entries can be reopened");
    }
    entry.setStatus("DRAFT");
    journalEntryRepository.save(entry);
  }

  @Transactional
  public void voidEntry(UUID entryId) {
    JournalEntry entry = findByIdOrThrow(entryId);
    if (!"DRAFT".equals(entry.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT journal entries can be voided");
    }
    entry.setStatus("VOID");
    journalEntryRepository.save(entry);
  }

  public List<JournalEntryLine> getLines(UUID entryId) {
    return lineRepository.findByJournalEntryId(entryId);
  }

  private void processLines(UUID entryId, List<JournalEntryLineRequest> lineRequests) {
    int lineNo = lineRepository.findByJournalEntryId(entryId).size() + 1;
    double totalDebit = 0.0;
    double totalCredit = 0.0;

    for (JournalEntryLineRequest req : lineRequests) {
      accountRepository.findById(req.getAccountId())
          .orElseThrow(() -> new IllegalArgumentException("Account not found: " + req.getAccountId()));

      JournalEntryLine line = new JournalEntryLine();
      line.setJournalEntryId(entryId);
      line.setLineNo(lineNo++);
      line.setAccountId(req.getAccountId());
      line.setDescription(req.getDescription());
      line.setDebit(req.getDebit() != null ? req.getDebit() : 0.0);
      line.setCredit(req.getCredit() != null ? req.getCredit() : 0.0);
      line.setBusinessPartnerId(req.getBusinessPartnerId());
      line.setProductId(req.getProductId());
      line.setCostCenter(req.getCostCenter());
      totalDebit += line.getDebit();
      totalCredit += line.getCredit();
      lineRepository.save(line);
    }

    JournalEntry entry = findByIdOrThrow(entryId);
    entry.setTotalDebit(totalDebit);
    entry.setTotalCredit(totalCredit);
    journalEntryRepository.save(entry);
  }
}
