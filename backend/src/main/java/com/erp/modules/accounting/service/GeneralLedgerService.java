package com.erp.modules.accounting.service;

import com.erp.modules.accounting.entity.Account;
import com.erp.modules.accounting.entity.AccountBalance;
import com.erp.modules.accounting.entity.JournalEntry;
import com.erp.modules.accounting.entity.JournalEntryLine;
import com.erp.modules.accounting.repository.AccountBalanceRepository;
import com.erp.modules.accounting.repository.AccountRepository;
import com.erp.modules.accounting.repository.JournalEntryLineRepository;
import com.erp.modules.accounting.repository.JournalEntryRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GeneralLedgerService {

  private final AccountRepository accountRepository;
  private final AccountBalanceRepository balanceRepository;
  private final JournalEntryRepository journalEntryRepository;
  private final JournalEntryLineRepository lineRepository;

  public GeneralLedgerService(
      AccountRepository accountRepository,
      AccountBalanceRepository balanceRepository,
      JournalEntryRepository journalEntryRepository,
      JournalEntryLineRepository lineRepository) {
    this.accountRepository = accountRepository;
    this.balanceRepository = balanceRepository;
    this.journalEntryRepository = journalEntryRepository;
    this.lineRepository = lineRepository;
  }

  public Double getAccountBalance(UUID accountId) {
    List<AccountBalance> balances = balanceRepository.findByAccountId(accountId);
    return balances.stream().mapToDouble(AccountBalance::getClosingBalance).sum();
  }

  public List<AccountBalance> getAccountHistory(UUID accountId) {
    return balanceRepository.findByAccountId(accountId);
  }

  public List<JournalEntryLine> getAccountTransactions(UUID accountId) {
    return lineRepository.findAll().stream()
        .filter(l -> l.getAccountId().equals(accountId))
        .toList();
  }

  public Map<String, Object> getTrialBalance() {
    List<Account> accounts = accountRepository.findAll();
    Map<String, Object> trialBalance = new LinkedHashMap<>();
    double totalDebit = 0.0;
    double totalCredit = 0.0;

    for (Account account : accounts) {
      double balance = getAccountBalance(account.getId());
      Map<String, Object> entry = new LinkedHashMap<>();
      entry.put("accountCode", account.getAccountCode());
      entry.put("accountName", account.getName());
      entry.put("accountType", account.getAccountType());

      if (balance >= 0) {
        entry.put("debit", balance);
        entry.put("credit", 0.0);
        totalDebit += balance;
      } else {
        entry.put("debit", 0.0);
        entry.put("credit", -balance);
        totalCredit += -balance;
      }

      trialBalance.put(account.getAccountCode(), entry);
    }

    trialBalance.put("TOTAL", Map.of("debit", totalDebit, "credit", totalCredit));
    return trialBalance;
  }
}
