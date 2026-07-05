package com.erp.modules.accounting.service;

import com.erp.modules.accounting.entity.AccountBalance;
import com.erp.modules.accounting.entity.JournalEntry;
import com.erp.modules.accounting.entity.JournalEntryLine;
import com.erp.modules.accounting.repository.AccountBalanceRepository;
import com.erp.modules.accounting.repository.JournalEntryLineRepository;
import com.erp.modules.accounting.repository.JournalEntryRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostingEngine {

  private final JournalEntryRepository journalEntryRepository;
  private final JournalEntryLineRepository lineRepository;
  private final AccountBalanceRepository balanceRepository;

  public PostingEngine(
      JournalEntryRepository journalEntryRepository,
      JournalEntryLineRepository lineRepository,
      AccountBalanceRepository balanceRepository) {
    this.journalEntryRepository = journalEntryRepository;
    this.lineRepository = lineRepository;
    this.balanceRepository = balanceRepository;
  }

  @Transactional
  public void post(UUID journalEntryId) {
    JournalEntry entry = journalEntryRepository.findById(journalEntryId)
        .orElseThrow(() -> new IllegalArgumentException("Journal entry not found"));

    if (!"COMPLETED".equals(entry.getStatus())) {
      throw new IllegalArgumentException("Only COMPLETED entries can be posted");
    }

    List<JournalEntryLine> lines = lineRepository.findByJournalEntryId(journalEntryId);

    if (lines.isEmpty()) {
      throw new IllegalArgumentException("Cannot post a journal entry with no lines");
    }

    double totalDebit = lines.stream().mapToDouble(JournalEntryLine::getDebit).sum();
    double totalCredit = lines.stream().mapToDouble(JournalEntryLine::getCredit).sum();

    if (Math.abs(totalDebit - totalCredit) > 0.001) {
      throw new IllegalArgumentException("Unbalanced journal entry: debits must equal credits");
    }

    String period = YearMonth.from(
        entry.getDocumentDate() != null ? entry.getDocumentDate() : LocalDate.now())
        .toString();

    for (JournalEntryLine line : lines) {
      AccountBalance balance = balanceRepository
          .findByAccountIdAndPeriod(line.getAccountId(), period)
          .orElseGet(() -> {
            AccountBalance newBalance = new AccountBalance();
            newBalance.setAccountId(line.getAccountId());
            newBalance.setPeriod(period);
            newBalance.setOpeningBalance(0.0);
            newBalance.setDebit(0.0);
            newBalance.setCredit(0.0);
            newBalance.setClosingBalance(0.0);
            return newBalance;
          });

      balance.setDebit(balance.getDebit() + (line.getDebit() != null ? line.getDebit() : 0.0));
      balance.setCredit(balance.getCredit() + (line.getCredit() != null ? line.getCredit() : 0.0));
      balance.setClosingBalance(balance.getOpeningBalance() + balance.getDebit() - balance.getCredit());
      balanceRepository.save(balance);
    }

    entry.setTotalDebit(totalDebit);
    entry.setTotalCredit(totalCredit);
    entry.setStatus("POSTED");
    journalEntryRepository.save(entry);
  }

  @Transactional
  public void reverse(UUID journalEntryId) {
    JournalEntry original = journalEntryRepository.findById(journalEntryId)
        .orElseThrow(() -> new IllegalArgumentException("Journal entry not found"));

    if (!"POSTED".equals(original.getStatus())) {
      throw new IllegalArgumentException("Only POSTED entries can be reversed");
    }

    List<JournalEntryLine> originalLines = lineRepository.findByJournalEntryId(journalEntryId);

    JournalEntry reversal = new JournalEntry();
    reversal.setDocumentNo("REV-" + original.getDocumentNo());
    reversal.setDocumentDate(LocalDate.now());
    reversal.setDescription("Reversal of " + original.getDocumentNo());
    reversal.setStatus("POSTED");
    reversal = journalEntryRepository.save(reversal);

    String period = YearMonth.now().toString();

    for (JournalEntryLine ol : originalLines) {
      JournalEntryLine rl = new JournalEntryLine();
      rl.setJournalEntryId(reversal.getId());
      rl.setAccountId(ol.getAccountId());
      rl.setDescription("Reversal: " + ol.getDescription());
      rl.setDebit(ol.getCredit());
      rl.setCredit(ol.getDebit());
      rl.setBusinessPartnerId(ol.getBusinessPartnerId());
      rl.setProductId(ol.getProductId());
      lineRepository.save(rl);

      AccountBalance balance = balanceRepository
          .findByAccountIdAndPeriod(ol.getAccountId(), period)
          .orElseGet(() -> {
            AccountBalance newBalance = new AccountBalance();
            newBalance.setAccountId(ol.getAccountId());
            newBalance.setPeriod(period);
            newBalance.setOpeningBalance(0.0);
            newBalance.setDebit(0.0);
            newBalance.setCredit(0.0);
            newBalance.setClosingBalance(0.0);
            return newBalance;
          });

      balance.setDebit(balance.getDebit() + rl.getDebit());
      balance.setCredit(balance.getCredit() + rl.getCredit());
      balance.setClosingBalance(balance.getOpeningBalance() + balance.getDebit() - balance.getCredit());
      balanceRepository.save(balance);
    }

    original.setStatus("CLOSED");
    journalEntryRepository.save(original);
  }

  @Transactional
  public void validateBalances() {
    List<AccountBalance> balances = balanceRepository.findAll();
    for (AccountBalance balance : balances) {
      double calculated = balance.getOpeningBalance() + balance.getDebit() - balance.getCredit();
      if (Math.abs(calculated - balance.getClosingBalance()) > 0.001) {
        throw new IllegalStateException(
            "Balance mismatch for account " + balance.getAccountId()
            + " in period " + balance.getPeriod());
      }
    }
  }
}
