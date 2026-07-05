package com.erp.modules.accounting.service;

import com.erp.common.base.BaseService;
import com.erp.modules.accounting.entity.Account;
import com.erp.modules.accounting.repository.AccountRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends BaseService<Account> {

  private final AccountRepository accountRepository;

  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  protected JpaRepository<Account, UUID> getRepository() {
    return accountRepository;
  }

  @Override
  protected void beforeCreate(Account entity) {
    if (entity.getAccountCode() == null || entity.getAccountCode().trim().isEmpty()) {
      throw new IllegalArgumentException("Account code is required");
    }
    if (accountRepository.findByAccountCode(entity.getAccountCode()).isPresent()) {
      throw new IllegalArgumentException("Account code must be unique");
    }
    String type = entity.getAccountType();
    if (type == null || (!type.equals("ASSET") && !type.equals("LIABILITY") && !type.equals("EQUITY")
        && !type.equals("REVENUE") && !type.equals("EXPENSE"))) {
      throw new IllegalArgumentException("Invalid account type: must be ASSET, LIABILITY, EQUITY, REVENUE, or EXPENSE");
    }
  }

  @Override
  protected void beforeUpdate(Account newEntity, Account existingEntity) {
    if (!newEntity.getAccountCode().equals(existingEntity.getAccountCode())
        && accountRepository.findByAccountCode(newEntity.getAccountCode()).isPresent()) {
      throw new IllegalArgumentException("Account code must be unique");
    }
  }

  public List<Account> getRootAccounts() {
    return accountRepository.findByParentIdIsNull();
  }

  public List<Account> getChildren(UUID parentId) {
    return accountRepository.findByParentId(parentId);
  }
}
