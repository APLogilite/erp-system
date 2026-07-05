package com.erp.modules.accounting.repository;

import com.erp.modules.accounting.entity.AccountBalance;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, UUID> {
  Optional<AccountBalance> findByAccountIdAndPeriod(UUID accountId, String period);
  List<AccountBalance> findByAccountId(UUID accountId);
  List<AccountBalance> findByPeriod(String period);
}
