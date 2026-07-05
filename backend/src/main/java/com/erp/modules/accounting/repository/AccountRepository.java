package com.erp.modules.accounting.repository;

import com.erp.modules.accounting.entity.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
  Optional<Account> findByAccountCode(String accountCode);
  List<Account> findByParentId(UUID parentId);
  List<Account> findByAccountType(String accountType);
  List<Account> findByParentIdIsNull();
}
