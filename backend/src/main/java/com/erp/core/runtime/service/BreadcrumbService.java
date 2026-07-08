package com.erp.core.runtime.service;

import com.erp.core.metadata.entity.FormSubFormEntity;
import com.erp.core.metadata.entity.MetadataView;
import com.erp.core.metadata.repository.FormSubFormRepository;
import com.erp.core.metadata.repository.MetadataViewRepository;
import com.erp.core.runtime.dto.BreadcrumbEntry;
import com.erp.core.runtime.dto.ParentContext;
import java.util.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class BreadcrumbService {

  private final MetadataViewRepository viewRepository;
  private final FormSubFormRepository subFormRepository;
  private final NamedParameterJdbcTemplate jdbcTemplate;

  public BreadcrumbService(MetadataViewRepository viewRepository,
                           FormSubFormRepository subFormRepository,
                           NamedParameterJdbcTemplate jdbcTemplate) {
    this.viewRepository = viewRepository;
    this.subFormRepository = subFormRepository;
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<BreadcrumbEntry> buildBreadcrumb(String formCode, UUID recordId, UUID tenantId) {
    List<BreadcrumbEntry> chain = new ArrayList<>();
    buildChain(formCode, recordId, tenantId, chain);
    Collections.reverse(chain);
    return chain;
  }

  private void buildChain(String formCode, UUID recordId, UUID tenantId, List<BreadcrumbEntry> chain) {
    MetadataView view = viewRepository.findByName(formCode).orElse(null);
    if (view == null) return;

    String label = getRecordLabel(view.getModelName(), recordId, tenantId);
    chain.add(new BreadcrumbEntry(formCode, recordId, label));

    // Find parent sub-form reference
    List<FormSubFormEntity> parents = subFormRepository.findAll().stream()
        .filter(sf -> formCode.equals(sf.getChildFormCode()))
        .toList();

    if (!parents.isEmpty()) {
      FormSubFormEntity parentRef = parents.get(0);
      MetadataView parentView = viewRepository.findById(parentRef.getParentFormId()).orElse(null);
      if (parentView != null) {
        UUID parentRecordId = findParentId(parentView.getModelName(),
            parentRef.getRelationCode(), recordId, tenantId);
        if (parentRecordId != null) {
          buildChain(parentView.getName(), parentRecordId, tenantId, chain);
        }
      }
    }
  }

  public ParentContext getParentContext(String formCode, UUID recordId, UUID tenantId) {
    List<FormSubFormEntity> parents = subFormRepository.findAll().stream()
        .filter(sf -> formCode.equals(sf.getChildFormCode()))
        .toList();

    if (parents.isEmpty()) return null;

    FormSubFormEntity parentRef = parents.get(0);
    MetadataView parentView = viewRepository.findById(parentRef.getParentFormId()).orElse(null);
    if (parentView == null) return null;

    UUID parentRecordId = findParentId(parentView.getModelName(),
        parentRef.getRelationCode(), recordId, tenantId);

    ParentContext ctx = new ParentContext();
    ctx.setFormCode(parentView.getName());
    ctx.setRecordId(parentRecordId);
    ctx.setLabel(parentRecordId != null ? getRecordLabel(parentView.getModelName(), parentRecordId, tenantId) : null);
    ctx.setRelationColumn(parentRef.getRelationCode());
    return ctx;
  }

  public String getRecordLabel(String tableName, UUID recordId, UUID tenantId) {
    if (tableName == null || recordId == null) return "#" + recordId;
    // Try to get a name/code field if it exists, fallback to shortened UUID
    return "#" + recordId.toString().substring(0, 8);
  }

  private UUID findParentId(String parentTableName, String relationColumn, UUID childRecordId, UUID tenantId) {
    if (parentTableName == null || relationColumn == null) return null;
    try {
      MapSqlParameterSource params = new MapSqlParameterSource();
      params.addValue("id", childRecordId);
      String sql = "SELECT \"" + relationColumn + "\" FROM \"" + parentTableName +
          "\" WHERE id = :id AND tenant_id = :tenantId";
      params.addValue("tenantId", tenantId);
      List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
      if (!rows.isEmpty() && rows.get(0).get(relationColumn) != null) {
        return (UUID) rows.get(0).get(relationColumn);
      }
    } catch (Exception e) {
      // Table or column may not exist yet
    }
    return null;
  }
}
