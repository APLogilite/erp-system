package com.erp.modules.common.service;

import com.erp.modules.common.entity.DocumentSequence;
import com.erp.modules.common.repository.DocumentSequenceRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentSequenceService {

  private static final Logger log = LoggerFactory.getLogger(DocumentSequenceService.class);

  private final DocumentSequenceRepository repository;

  public DocumentSequenceService(DocumentSequenceRepository repository) {
    this.repository = repository;
  }

  /**
   * Returns the next formatted document number and increments the counter.
   * Thread-safe via PESSIMISTIC_WRITE lock on the sequence row.
   *
   * @param sequenceName e.g. "quotation", "sales_order"
   * @param tenantId     the current tenant UUID
   * @return formatted number, e.g. "QT-0001"
   * @throws IllegalArgumentException if the sequence is not found
   */
  @Transactional
  public String getNextNumber(String sequenceName, UUID tenantId) {
    DocumentSequence seq = repository.findForUpdate(sequenceName, tenantId)
        .orElseThrow(() -> new IllegalArgumentException("Sequence not found: " + sequenceName));

    int current = seq.getNextValue();
    seq.setNextValue(current + 1);
    repository.save(seq);

    return formatDocumentNumber(seq.getPrefix(), current, seq.getPadding());
  }

  /**
   * Returns the next number that WOULD be assigned, without incrementing.
   */
  @Transactional(readOnly = true)
  public String peekNextNumber(String sequenceName, UUID tenantId) {
    DocumentSequence seq = repository.findBySequenceNameAndTenantId(sequenceName, tenantId)
        .orElseThrow(() -> new IllegalArgumentException("Sequence not found: " + sequenceName));

    return formatDocumentNumber(seq.getPrefix(), seq.getNextValue(), seq.getPadding());
  }

  private String formatDocumentNumber(String prefix, int number, int padding) {
    return prefix + String.format("%0" + padding + "d", number);
  }
}
