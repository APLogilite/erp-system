package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.*;
import com.erp.core.metadata.entity.MetadataModel;
import com.erp.core.metadata.entity.MetadataVersion;
import com.erp.core.metadata.entity.TableColumnEntity;
import com.erp.core.metadata.repository.FormFieldRepository;
import com.erp.core.metadata.repository.MetadataModelRepository;
import com.erp.core.metadata.repository.MetadataVersionRepository;
import com.erp.core.metadata.repository.TableColumnRepository;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.*;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableDesignerService {

  private static final Logger log = LoggerFactory.getLogger(TableDesignerService.class);
  private static final Pattern CODE_PATTERN = Pattern.compile("^[a-z][a-z0-9_]*$");

  private final MetadataModelRepository modelRepo;
  private final TableColumnRepository columnRepo;
  private final DdlExecutorService ddlExecutor;
  private final MetadataVersionRepository versionRepo;
  private final SchemaHistoryService schemaHistoryService;
  private final FormFieldRepository formFieldRepo;

  public TableDesignerService(
      MetadataModelRepository modelRepo,
      TableColumnRepository columnRepo,
      DdlExecutorService ddlExecutor,
      MetadataVersionRepository versionRepo,
      SchemaHistoryService schemaHistoryService,
      FormFieldRepository formFieldRepo) {
    this.modelRepo = modelRepo;
    this.columnRepo = columnRepo;
    this.ddlExecutor = ddlExecutor;
    this.versionRepo = versionRepo;
    this.schemaHistoryService = schemaHistoryService;
    this.formFieldRepo = formFieldRepo;
  }

  public List<TableResponse> listTables(String search, int page, int size) {
    Page<MetadataModel> models;
    if (search != null && !search.isBlank()) {
      models = modelRepo.findByNameContainingIgnoreCaseOrLabelContainingIgnoreCase(
          search, search, PageRequest.of(page, size));
    } else {
      models = modelRepo.findAll(PageRequest.of(page, size));
    }
    return models.stream().map(this::toTableResponse).toList();
  }

  public TableResponse getTable(UUID id) {
    MetadataModel model = modelRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Table not found: " + id));
    return toTableResponse(model);
  }

  @Transactional(rollbackFor = Exception.class)
  public TableResponse createTable(CreateTableRequest request) {
    validateCode(request.getCode());
    if (modelRepo.findByName(request.getCode()).isPresent()) {
      throw new IllegalArgumentException("Table code already exists: " + request.getCode());
    }

    MetadataModel model = new MetadataModel();
    model.setName(request.getCode());
    model.setLabel(request.getLabel());
    model.setPluralLabel(request.getPluralLabel());
    model.setDescription(request.getDescription());
    model.setTableName(request.getTableName());
    model.setTableType("dynamic");
    model.setDefinition(new HashMap<>());
    MetadataModel saved = modelRepo.save(model);

    // Save columns
    if (request.getColumns() != null) {
      for (int i = 0; i < request.getColumns().size(); i++) {
        CreateColumnRequest col = request.getColumns().get(i);
        TableColumnEntity entity = createColumnEntity(saved.getId(), col, i);
        columnRepo.save(entity);
      }
    }

    // Execute DDL
    try {
      ddlExecutor.createTable(saved.getId());
    } catch (Exception e) {
      log.error("DDL failed for table {}, rolling back", saved.getId());
      throw new RuntimeException("Failed to create physical table: " + e.getMessage(), e);
    }

    // Log schema history
    schemaHistoryService.logChange(saved.getId(), "Created table: " + saved.getName(),
        buildSnapshot(saved), currentUserId());

    return toTableResponse(saved);
  }

  @Transactional
  public TableResponse updateTable(UUID tableId, UpdateTableRequest request) {
    MetadataModel model = modelRepo.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));

    if (request.getLabel() != null) {
      model.setLabel(request.getLabel());
    }
    if (request.getPluralLabel() != null) {
      model.setPluralLabel(request.getPluralLabel());
    }
    if (request.getDescription() != null) {
      model.setDescription(request.getDescription());
    }
    modelRepo.save(model);

    schemaHistoryService.logChange(tableId, "Updated table metadata",
        buildSnapshot(model), currentUserId());

    return toTableResponse(model);
  }

  @Transactional(rollbackFor = Exception.class)
  public TableResponse addColumn(UUID tableId, CreateColumnRequest request) {
    MetadataModel model = modelRepo.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));

    // Determine next position if not specified
    int nextPos = request.getPosition() != null ? request.getPosition()
        : columnRepo.findByTableIdOrderByPosition(tableId).size();
    TableColumnEntity entity = createColumnEntity(tableId, request, nextPos);
    TableColumnEntity saved = columnRepo.save(entity);
    ddlExecutor.addColumn(tableId, saved.getId());

    schemaHistoryService.logChange(tableId, "Added column: " + saved.getCode(),
        buildSnapshot(model), currentUserId());

    return toTableResponse(model);
  }

  @Transactional(rollbackFor = Exception.class)
  public TableResponse updateColumn(UUID tableId, UUID columnId, UpdateColumnRequest request) {
    MetadataModel model = modelRepo.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));

    TableColumnEntity col = columnRepo.findById(columnId)
        .orElseThrow(() -> new IllegalArgumentException("Column not found: " + columnId));

    if (!col.getTableId().equals(tableId)) {
      throw new IllegalArgumentException("Column does not belong to this table");
    }

    if (request.getLabel() != null) col.setLabel(request.getLabel());
    if (request.getType() != null) col.setType(request.getType());
    if (request.getRequired() != null) col.setRequired(request.getRequired());
    if (request.getDefaultValue() != null) col.setDefaultValue(request.getDefaultValue());
    if (request.getMaxLength() != null) col.setMaxLength(request.getMaxLength());
    if (request.getPrecision() != null) col.setPrecision(request.getPrecision());
    if (request.getScale() != null) col.setScale(request.getScale());
    if (request.getRelationTable() != null) col.setRelationTable(request.getRelationTable());
    if (request.getEnumOptions() != null) col.setEnumOptions(request.getEnumOptions());
    if (request.getPosition() != null) col.setPosition(request.getPosition());

    columnRepo.save(col);

    // Execute DDL to modify the physical column
    ddlExecutor.modifyColumn(tableId, columnId);

    schemaHistoryService.logChange(tableId, "Modified column: " + col.getCode(),
        buildSnapshot(model), currentUserId());

    return toTableResponse(model);
  }

  @Transactional
  public void deleteColumn(UUID tableId, UUID columnId) {
    TableColumnEntity col = columnRepo.findById(columnId)
        .orElseThrow(() -> new IllegalArgumentException("Column not found: " + columnId));

    if (!col.getTableId().equals(tableId)) {
      throw new IllegalArgumentException("Column does not belong to this table");
    }

    ddlExecutor.dropColumn(tableId, columnId);
    col.softDelete();
    columnRepo.save(col);

    MetadataModel model = modelRepo.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));

    schemaHistoryService.logChange(tableId, "Dropped column: " + col.getCode(),
        buildSnapshot(model), currentUserId());
  }

  @Transactional
  public void reorderColumns(UUID tableId, ColumnReorderRequest request) {
    List<UUID> columnIds = request.getColumnIds();
    if (columnIds == null || columnIds.isEmpty()) {
      throw new IllegalArgumentException("Column IDs list must not be empty");
    }

    for (int i = 0; i < columnIds.size(); i++) {
      UUID colId = columnIds.get(i);
      TableColumnEntity col = columnRepo.findById(colId)
          .orElseThrow(() -> new IllegalArgumentException("Column not found: " + colId));
      if (!col.getTableId().equals(tableId)) {
        throw new IllegalArgumentException("Column " + colId + " does not belong to this table");
      }
      col.setPosition(i);
      columnRepo.save(col);
    }

    MetadataModel model = modelRepo.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));

    schemaHistoryService.logChange(tableId, "Reordered columns",
        buildSnapshot(model), currentUserId());
  }

  public List<VersionHistoryResponse> getHistory(UUID tableId) {
    List<MetadataVersion> versions = schemaHistoryService.getHistory(tableId);
    return versions.stream().map(v -> {
      VersionHistoryResponse r = new VersionHistoryResponse();
      r.setId(v.getId());
      r.setVersion(v.getVersion());
      r.setTableId(v.getTableId());
      r.setDescription(v.getDescription());
      r.setDefinitionSnapshot(v.getDefinitionSnapshot());
      r.setChangedBy(v.getChangedBy());
      r.setCreatedAt(v.getCreatedAt());
      return r;
    }).toList();
  }

  @Transactional
  public void deleteTable(UUID tableId) {
    MetadataModel model = modelRepo.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));
    model.softDelete();
    modelRepo.save(model);

    schemaHistoryService.logChange(tableId, "Table deactivated",
        buildSnapshot(model), currentUserId());
  }

  // ---------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------

  private UUID currentUserId() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    return ctx != null ? ctx.getUserId() : null;
  }

  private Map<String, Object> buildSnapshot(MetadataModel model) {
    Map<String, Object> snapshot = new HashMap<>();
    snapshot.put("code", model.getName());
    snapshot.put("label", model.getLabel());
    snapshot.put("pluralLabel", model.getPluralLabel());
    snapshot.put("tableType", model.getTableType());
    snapshot.put("tableName", model.getTableName());
    snapshot.put("description", model.getDescription());
    snapshot.put("isActive", model.getIsActive());

    List<TableColumnEntity> cols = columnRepo.findByTableIdOrderByPosition(model.getId());
    List<Map<String, Object>> colMaps = cols.stream().map(c -> {
      Map<String, Object> cm = new HashMap<>();
      cm.put("id", c.getId().toString());
      cm.put("code", c.getCode());
      cm.put("label", c.getLabel());
      cm.put("type", c.getType());
      cm.put("required", c.getRequired());
      cm.put("defaultValue", c.getDefaultValue());
      cm.put("maxLength", c.getMaxLength());
      cm.put("precision", c.getPrecision());
      cm.put("scale", c.getScale());
      cm.put("relationTable", c.getRelationTable());
      cm.put("enumOptions", c.getEnumOptions());
      cm.put("position", c.getPosition());
      cm.put("isActive", c.getIsActive());
      return cm;
    }).toList();
    snapshot.put("columns", colMaps);

    return snapshot;
  }

  private TableColumnEntity createColumnEntity(UUID tableId, CreateColumnRequest req, int position) {
    validateCode(req.getCode());
    validateColumnType(req);
    TableColumnEntity entity = new TableColumnEntity();
    entity.setTableId(tableId);
    entity.setCode(req.getCode());
    entity.setLabel(req.getLabel());
    entity.setType(req.getType());
    entity.setRequired(req.getRequired() != null ? req.getRequired() : false);
    entity.setDefaultValue(req.getDefaultValue());
    entity.setMaxLength(req.getMaxLength());
    entity.setPrecision(req.getPrecision());
    entity.setScale(req.getScale());
    entity.setRelationTable(req.getRelationTable());
    entity.setEnumOptions(req.getEnumOptions());
    entity.setPosition(req.getPosition() != null ? req.getPosition() : position);
    return entity;
  }

  private void validateColumnType(CreateColumnRequest req) {
    if (req.getType() == null) return;
    switch (req.getType()) {
      case "decimal":
        if (req.getPrecision() == null || req.getPrecision() <= 0) {
          throw new IllegalArgumentException("Decimal column requires precision > 0");
        }
        if (req.getScale() == null || req.getScale() < 0) {
          throw new IllegalArgumentException("Decimal column requires scale >= 0");
        }
        break;
      case "many2one":
        if (req.getRelationTable() == null || req.getRelationTable().isBlank()) {
          throw new IllegalArgumentException("Many2one column requires relation_table");
        }
        break;
      default:
        break;
    }
  }

  private void validateCode(String code) {
    if (code == null || !CODE_PATTERN.matcher(code).matches()) {
      throw new IllegalArgumentException("Invalid code: " + code + ". Must match " + CODE_PATTERN.pattern());
    }
  }

  private TableResponse toTableResponse(MetadataModel model) {
    TableResponse r = new TableResponse();
    r.setId(model.getId());
    r.setCode(model.getName());
    r.setLabel(model.getLabel());
    r.setPluralLabel(model.getPluralLabel());
    r.setDescription(model.getDescription());
    r.setTableName(model.getTableName());
    r.setTableType(model.getTableType());
    r.setIsActive(model.getIsActive());

    List<TableColumnEntity> cols = columnRepo.findByTableIdOrderByPosition(model.getId());
    r.setColumns(cols.stream().map(c -> {
      TableColumnDto d = new TableColumnDto();
      d.setId(c.getId());
      d.setTableId(c.getTableId());
      d.setCode(c.getCode());
      d.setLabel(c.getLabel());
      d.setType(c.getType());
      d.setRequired(c.getRequired());
      d.setDefaultValue(c.getDefaultValue());
      d.setMaxLength(c.getMaxLength());
      d.setPrecision(c.getPrecision());
      d.setScale(c.getScale());
      d.setRelationTable(c.getRelationTable());
      d.setEnumOptions(c.getEnumOptions());
      d.setPosition(c.getPosition());
      d.setIsActive(c.getIsActive());
      return d;
    }).toList());
    return r;
  }
}
