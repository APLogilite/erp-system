package com.erp.modules.manufacturing.service;

import com.erp.common.base.BaseService;
import com.erp.modules.manufacturing.dto.BOMLineRequest;
import com.erp.modules.manufacturing.dto.BOMRequest;
import com.erp.modules.manufacturing.entity.BOMLine;
import com.erp.modules.manufacturing.entity.BillOfMaterial;
import com.erp.modules.manufacturing.repository.BOMLineRepository;
import com.erp.modules.manufacturing.repository.BOMRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BOMService extends BaseService<BillOfMaterial> {

  private final BOMRepository bomRepository;
  private final BOMLineRepository lineRepository;

  public BOMService(BOMRepository bomRepository, BOMLineRepository lineRepository) {
    this.bomRepository = bomRepository;
    this.lineRepository = lineRepository;
  }

  @Override
  protected JpaRepository<BillOfMaterial, UUID> getRepository() {
    return bomRepository;
  }

  @Override
  protected void beforeCreate(BillOfMaterial entity) {
    if (entity.getCode() == null || entity.getCode().trim().isEmpty()) {
      throw new IllegalArgumentException("BOM code is required");
    }
    if (bomRepository.findByCode(entity.getCode()).isPresent()) {
      throw new IllegalArgumentException("BOM code must be unique");
    }
    if (entity.getStatus() == null) {
      entity.setStatus("DRAFT");
    }
  }

  @Override
  protected void beforeUpdate(BillOfMaterial newEntity, BillOfMaterial existingEntity) {
    if (!newEntity.getCode().equals(existingEntity.getCode())
        && bomRepository.findByCode(newEntity.getCode()).isPresent()) {
      throw new IllegalArgumentException("BOM code must be unique");
    }
  }

  @Transactional
  public UUID createWithLines(BOMRequest request) {
    BillOfMaterial bom = new BillOfMaterial();
    bom.setCode(request.getCode());
    bom.setName(request.getName());
    bom.setProductId(request.getProductId());
    bom.setRevision(request.getRevision());
    bom.setVersion(request.getVersion());
    bom.setEffectiveFrom(request.getEffectiveFrom());
    bom.setEffectiveTo(request.getEffectiveTo());
    bom.setDescription(request.getDescription());
    bom.setStatus("DRAFT");

    beforeCreate(bom);
    BillOfMaterial saved = bomRepository.save(bom);

    if (request.getLines() != null && !request.getLines().isEmpty()) {
      processLines(saved.getId(), request.getLines());
    }

    return saved.getId();
  }

  public List<BOMLine> getLines(UUID bomId) {
    return lineRepository.findByBomId(bomId);
  }

  public List<BillOfMaterial> getByProduct(UUID productId) {
    return bomRepository.findByProductId(productId);
  }

  public List<BillOfMaterial> getActiveByProduct(UUID productId) {
    return bomRepository.findByProductIdAndStatus(productId, "ACTIVE");
  }

  @Transactional
  public void approveBom(UUID bomId) {
    BillOfMaterial bom = findByIdOrThrow(bomId);
    if (!"DRAFT".equals(bom.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT BOMs can be approved");
    }
    bom.setStatus("ACTIVE");
    bomRepository.save(bom);
  }

  @Transactional
  public void archiveBom(UUID bomId) {
    BillOfMaterial bom = findByIdOrThrow(bomId);
    bom.setStatus("ARCHIVED");
    bomRepository.save(bom);
  }

  private void processLines(UUID bomId, List<BOMLineRequest> lineRequests) {
    int lineNo = lineRepository.findByBomId(bomId).size() + 1;
    for (BOMLineRequest req : lineRequests) {
      BOMLine line = new BOMLine();
      line.setBomId(bomId);
      line.setLineNo(lineNo++);
      line.setComponentId(req.getComponentId());
      line.setQuantity(req.getQuantity() != null ? req.getQuantity() : 1.0);
      line.setUom(req.getUom());
      line.setScrapPercentage(req.getScrapPercentage() != null ? req.getScrapPercentage() : 0.0);
      line.setOperationId(req.getOperationId());
      lineRepository.save(line);
    }
  }
}
