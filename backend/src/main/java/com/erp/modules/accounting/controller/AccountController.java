package com.erp.modules.accounting.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.accounting.dto.AccountRequest;
import com.erp.modules.accounting.dto.AccountResponse;
import com.erp.modules.accounting.entity.Account;
import com.erp.modules.accounting.service.AccountService;
import java.util.List;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/accounts")
public class AccountController {

  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<AccountResponse>> create(@RequestBody AccountRequest request) {
    Account entity = new Account();
    entity.setAccountCode(request.getAccountCode());
    entity.setName(request.getName());
    entity.setDescription(request.getDescription());
    entity.setAccountType(request.getAccountType());
    entity.setParentId(request.getParentId());
    entity.setCurrency(request.getCurrency());
    entity.setIsControlAccount(request.getIsControlAccount());
    Account saved = accountService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Account created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<AccountResponse>>> getAll() {
    List<AccountResponse> list = accountService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Accounts retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AccountResponse>> getById(@PathVariable UUID id) {
    Account entity = accountService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Account retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AccountResponse>> update(@PathVariable UUID id, @RequestBody AccountRequest request) {
    Account existing = accountService.findByIdOrThrow(id);
    existing.setAccountCode(request.getAccountCode());
    existing.setName(request.getName());
    existing.setDescription(request.getDescription());
    existing.setAccountType(request.getAccountType());
    existing.setParentId(request.getParentId());
    existing.setCurrency(request.getCurrency());
    existing.setIsControlAccount(request.getIsControlAccount());
    Account updated = accountService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Account updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    accountService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Account deleted"));
  }

  @GetMapping("/roots")
  public ResponseEntity<ApiResponse<List<AccountResponse>>> getRoots() {
    List<AccountResponse> list = accountService.getRootAccounts().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Root accounts retrieved"));
  }

  @GetMapping("/{parentId}/children")
  public ResponseEntity<ApiResponse<List<AccountResponse>>> getChildren(@PathVariable UUID parentId) {
    List<AccountResponse> list = accountService.getChildren(parentId).stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Child accounts retrieved"));
  }

  private AccountResponse toResponse(Account entity) {
    AccountResponse r = new AccountResponse();
    r.setId(entity.getId());
    r.setAccountCode(entity.getAccountCode());
    r.setName(entity.getName());
    r.setDescription(entity.getDescription());
    r.setAccountType(entity.getAccountType());
    r.setParentId(entity.getParentId());
    r.setCurrency(entity.getCurrency());
    r.setIsControlAccount(entity.getIsControlAccount());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }
}
