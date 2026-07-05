package com.erp.modules.accounting.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.accounting.dto.JournalEntryLineRequest;
import com.erp.modules.accounting.dto.JournalEntryLineResponse;
import com.erp.modules.accounting.dto.JournalEntryRequest;
import com.erp.modules.accounting.dto.JournalEntryResponse;
import com.erp.modules.accounting.entity.JournalEntry;
import com.erp.modules.accounting.entity.JournalEntryLine;
import com.erp.modules.accounting.service.GeneralLedgerService;
import com.erp.modules.accounting.service.JournalEntryService;
import com.erp.modules.accounting.service.PostingEngine;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/journal-entries")
public class JournalEntryController {

  private final JournalEntryService journalEntryService;
  private final PostingEngine postingEngine;
  private final GeneralLedgerService generalLedgerService;

  public JournalEntryController(
      JournalEntryService journalEntryService,
      PostingEngine postingEngine,
      GeneralLedgerService generalLedgerService) {
    this.journalEntryService = journalEntryService;
    this.postingEngine = postingEngine;
    this.generalLedgerService = generalLedgerService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody JournalEntryRequest request) {
    UUID id = journalEntryService.createWithLines(request);
    return ResponseEntity.ok(ApiResponse.success(id, "Journal entry created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<JournalEntryResponse>>> getAll() {
    List<JournalEntryResponse> list = journalEntryService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Journal entries retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<JournalEntryResponse>> getById(@PathVariable UUID id) {
    JournalEntry entry = journalEntryService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entry), "Journal entry retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<JournalEntryResponse>> update(
      @PathVariable UUID id, @RequestBody JournalEntryRequest request) {
    JournalEntry existing = journalEntryService.findByIdOrThrow(id);
    existing.setDocumentDate(request.getDocumentDate());
    existing.setDescription(request.getDescription());
    JournalEntry updated = journalEntryService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Journal entry updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    JournalEntry entry = journalEntryService.findByIdOrThrow(id);
    if (!"DRAFT".equals(entry.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT entries can be deleted");
    }
    journalEntryService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Journal entry deleted"));
  }

  @GetMapping("/{id}/lines")
  public ResponseEntity<ApiResponse<List<JournalEntryLineResponse>>> getLines(@PathVariable UUID id) {
    List<JournalEntryLine> lines = journalEntryService.getLines(id);
    List<JournalEntryLineResponse> list = lines.stream().map(this::toLineResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Lines retrieved"));
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<Void>> complete(@PathVariable UUID id) {
    journalEntryService.completeEntry(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Journal entry completed"));
  }

  @PostMapping("/{id}/post")
  public ResponseEntity<ApiResponse<Void>> post(@PathVariable UUID id) {
    postingEngine.post(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Journal entry posted"));
  }

  @PostMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
    journalEntryService.closeEntry(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Journal entry closed"));
  }

  @PostMapping("/{id}/reopen")
  public ResponseEntity<ApiResponse<Void>> reopen(@PathVariable UUID id) {
    journalEntryService.reopenEntry(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Journal entry reopened"));
  }

  @PostMapping("/{id}/void")
  public ResponseEntity<ApiResponse<Void>> voidEntry(@PathVariable UUID id) {
    journalEntryService.voidEntry(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Journal entry voided"));
  }

  @PostMapping("/{id}/reverse")
  public ResponseEntity<ApiResponse<Void>> reverse(@PathVariable UUID id) {
    postingEngine.reverse(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Journal entry reversed"));
  }

  @PostMapping("/validate")
  public ResponseEntity<ApiResponse<Void>> validateBalances() {
    postingEngine.validateBalances();
    return ResponseEntity.ok(ApiResponse.successMessage("All balances validated"));
  }

  @GetMapping("/trial-balance")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getTrialBalance() {
    Map<String, Object> trialBalance = generalLedgerService.getTrialBalance();
    return ResponseEntity.ok(ApiResponse.success(trialBalance, "Trial balance retrieved"));
  }

  @GetMapping("/accounts/{accountId}/balance")
  public ResponseEntity<ApiResponse<Double>> getAccountBalance(@PathVariable UUID accountId) {
    Double balance = generalLedgerService.getAccountBalance(accountId);
    return ResponseEntity.ok(ApiResponse.success(balance, "Account balance retrieved"));
  }

  private JournalEntryResponse toResponse(JournalEntry entry) {
    JournalEntryResponse r = new JournalEntryResponse();
    r.setId(entry.getId());
    r.setDocumentNo(entry.getDocumentNo());
    r.setDocumentDate(entry.getDocumentDate());
    r.setDescription(entry.getDescription());
    r.setStatus(entry.getStatus());
    r.setTotalDebit(entry.getTotalDebit());
    r.setTotalCredit(entry.getTotalCredit());
    r.setCreatedAt(entry.getCreatedAt());
    r.setUpdatedAt(entry.getUpdatedAt());
    r.setIsActive(entry.getIsActive());
    try {
      List<JournalEntryLineResponse> lines = journalEntryService.getLines(entry.getId()).stream()
          .map(this::toLineResponse).collect(Collectors.toList());
      r.setLines(lines);
    } catch (Exception e) {
      r.setLines(List.of());
    }
    return r;
  }

  private JournalEntryLineResponse toLineResponse(JournalEntryLine line) {
    JournalEntryLineResponse r = new JournalEntryLineResponse();
    r.setId(line.getId());
    r.setJournalEntryId(line.getJournalEntryId());
    r.setLineNo(line.getLineNo());
    r.setAccountId(line.getAccountId());
    r.setDescription(line.getDescription());
    r.setDebit(line.getDebit());
    r.setCredit(line.getCredit());
    r.setBusinessPartnerId(line.getBusinessPartnerId());
    r.setProductId(line.getProductId());
    r.setCostCenter(line.getCostCenter());
    return r;
  }
}
