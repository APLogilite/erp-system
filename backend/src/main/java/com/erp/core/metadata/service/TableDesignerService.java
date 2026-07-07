package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.CreateColumnRequest;
import com.erp.core.metadata.dto.CreateTableRequest;
import com.erp.core.metadata.dto.TableColumnDto;
import com.erp.core.metadata.dto.TableResponse;
import com.erp.core.metadata.entity.MetadataModel;
import com.erp.core.metadata.entity.TableColumnEntity;
import com.erp.core.metadata.repository.MetadataModelRepository;
import com.erp.core.metadata.repository.MetadataVersionRepository;
import com.erp.core.metadata.repository.TableColumnRepository;
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

  public TableDesignerService(
      MetadataModelRepository modelRepo,
      TableColumnRepository columnRepo,
      DdlExecutorService ddlExecutor,
      MetadataVersionRepository versionRepo) {
    this.modelRepo = modelRepo;
    this.columnRepo = columnRepo;
    this.ddlExecutor = ddlExecutor;
    this.versionRepo = versionRepo;
  }

  public List<TableResponse> listTables(String search, int page, int size) {
    Page<MetadataModel> models = modelRepo.findAll(
        PageRequest.of(page, size));
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

    return toTableResponse(saved);
  }

  @Transactional(rollbackFor = Exception.class)
  public TableResponse addColumn(UUID tableId, CreateColumnRequest request) {
    MetadataModel model = modelRepo.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));

    TableColumnEntity entity = createColumnEntity(tableId, request, 0);
    TableColumnEntity saved = columnRepo.save(entity);
    ddlExecutor.addColumn(tableId, saved.getId());

    return toTableResponse(model);
  }

  @Transactional
  public void deleteColumn(UUID tableId, UUID columnId) {
    TableColumnEntity col = columnRepo.findById(columnId)
        .orElseThrow(() -> new IllegalArgumentException("Column not found: " + columnId));
    ddlExecutor.dropColumn(tableId, columnId);
    col.softDelete();
    columnRepo.save(col);
  }

  @Transactional
  public void deleteTable(UUID tableId) {
    MetadataModel model = modelRepo.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table not found: " + tableId));
    model.softDelete();
    modelRepo.save(model);
  }

  private TableColumnEntity createColumnEntity(UUID tableId, CreateColumnRequest req, int position) {
    validateCode(req.getCode());
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
