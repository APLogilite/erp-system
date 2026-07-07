package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.UserAccount;
import com.erp.platform.identity.entity.UserPreference;
import com.erp.platform.identity.entity.UserRole;
import com.erp.platform.identity.service.UserAdminService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity/users")
public class UserAdminController {

  private final UserAdminService userAdminService;
  public UserAdminController(UserAdminService userAdminService) { this.userAdminService = userAdminService; }

  @GetMapping public ResponseEntity<ApiResponse<List<UserAccount>>> getAll() { return ResponseEntity.ok(ApiResponse.success(userAdminService.getAllUsers(), "Users retrieved")); }
  @GetMapping("/{id}") public ResponseEntity<ApiResponse<UserAccount>> getById(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(userAdminService.getUser(id), "User retrieved")); }
  @PostMapping public ResponseEntity<ApiResponse<UserAccount>> create(@RequestBody UserAccount u) { return ResponseEntity.ok(ApiResponse.success(userAdminService.createUser(u), "User created")); }
  @PutMapping("/{id}") public ResponseEntity<ApiResponse<UserAccount>> update(@PathVariable UUID id, @RequestBody UserAccount u) { return ResponseEntity.ok(ApiResponse.success(userAdminService.updateUser(id, u), "User updated")); }
  @PostMapping("/{id}/deactivate") public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) { userAdminService.deactivateUser(id); return ResponseEntity.ok(ApiResponse.successMessage("User deactivated")); }
  @PostMapping("/{id}/activate") public ResponseEntity<ApiResponse<Void>> activate(@PathVariable UUID id) { userAdminService.activateUser(id); return ResponseEntity.ok(ApiResponse.successMessage("User activated")); }
  @PostMapping("/{id}/reset-password") public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable UUID id, @RequestBody Map<String, String> body) { userAdminService.resetPassword(id, body.get("newPassword")); return ResponseEntity.ok(ApiResponse.successMessage("Password reset")); }
  @PostMapping("/{id}/unlock") public ResponseEntity<ApiResponse<Void>> unlock(@PathVariable UUID id) { userAdminService.unlockUser(id); return ResponseEntity.ok(ApiResponse.successMessage("User unlocked")); }
  @PostMapping("/{id}/roles") public ResponseEntity<ApiResponse<Void>> assignRole(@PathVariable UUID id, @RequestBody Map<String, UUID> body) { userAdminService.assignRole(id, body.get("roleId")); return ResponseEntity.ok(ApiResponse.successMessage("Role assigned")); }
  @DeleteMapping("/{id}/roles/{roleId}") public ResponseEntity<ApiResponse<Void>> removeRole(@PathVariable UUID id, @PathVariable UUID roleId) { userAdminService.removeRole(id, roleId); return ResponseEntity.ok(ApiResponse.successMessage("Role removed")); }
  @GetMapping("/{id}/roles") public ResponseEntity<ApiResponse<List<UserRole>>> getRoles(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(userAdminService.getUserRoles(id), "User roles")); }
  @GetMapping("/{id}/preferences") public ResponseEntity<ApiResponse<UserPreference>> getPrefs(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(userAdminService.getUserPreferences(id), "Preferences")); }
  @PutMapping("/{id}/preferences") public ResponseEntity<ApiResponse<Void>> updatePrefs(@PathVariable UUID id, @RequestBody Map<String, String> body) { userAdminService.updatePreferences(id, body.get("language"), body.get("timezone"), body.get("dateFormat"), body.get("timeFormat"), body.get("numberFormat"), body.get("currency"), body.get("theme")); return ResponseEntity.ok(ApiResponse.successMessage("Preferences updated")); }
}
