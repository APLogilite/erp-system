package com.erp.modules.accounting.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "account_balances")
public class AccountBalance extends BaseEntity {

  @Column(name = "account_id", nullable = false)
  private UUID accountId;

  @Column(nullable = false)
  private String period;

  @Column(name = "opening_balance")
  private Double openingBalance = 0.0;

  @Column
  private Double debit = 0.0;

  @Column
  private Double credit = 0.0;

  @Column(name = "closing_balance")
  private Double closingBalance = 0.0;

  public UUID getAccountId() { return accountId; }
  public void setAccountId(UUID accountId) { this.accountId = accountId; }
  public String getPeriod() { return period; }
  public void setPeriod(String period) { this.period = period; }
  public Double getOpeningBalance() { return openingBalance; }
  public void setOpeningBalance(Double openingBalance) { this.openingBalance = openingBalance; }
  public Double getDebit() { return debit; }
  public void setDebit(Double debit) { this.debit = debit; }
  public Double getCredit() { return credit; }
  public void setCredit(Double credit) { this.credit = credit; }
  public Double getClosingBalance() { return closingBalance; }
  public void setClosingBalance(Double closingBalance) { this.closingBalance = closingBalance; }
}
