package com.erp.modules.manufacturing.service;

import com.erp.modules.manufacturing.entity.BOMLine;
import com.erp.modules.manufacturing.entity.BillOfMaterial;
import com.erp.modules.manufacturing.entity.ManufacturingOrder;
import com.erp.modules.manufacturing.repository.BOMLineRepository;
import com.erp.modules.manufacturing.repository.BOMRepository;
import com.erp.modules.manufacturing.repository.ManufacturingOrderRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MRPService {

  private final BOMRepository bomRepository;
  private final BOMLineRepository bomLineRepository;
  private final ManufacturingOrderRepository moRepository;

  public MRPService(BOMRepository bomRepository,
                    BOMLineRepository bomLineRepository,
                    ManufacturingOrderRepository moRepository) {
    this.bomRepository = bomRepository;
    this.bomLineRepository = bomLineRepository;
    this.moRepository = moRepository;
  }

  public Map<String, Object> runMRP() {
    List<ManufacturingOrder> plannedOrders = moRepository.findByStatusIn(
        List.of("PLANNED", "RELEASED", "IN_PRODUCTION"));

    Map<UUID, Double> grossDemand = new HashMap<>();
    for (ManufacturingOrder mo : plannedOrders) {
      grossDemand.merge(mo.getProductId(), mo.getPlannedQuantity(), Double::sum);
    }

    List<Map<String, Object>> explodedDemands = new ArrayList<>();
    List<Map<String, Object>> shortages = new ArrayList<>();
    List<Map<String, Object>> purchaseSuggestions = new ArrayList<>();
    List<Map<String, Object>> productionSuggestions = new ArrayList<>();

    for (Map.Entry<UUID, Double> demand : grossDemand.entrySet()) {
      explodeBom(demand.getKey(), demand.getValue(), explodedDemands, shortages,
                 purchaseSuggestions, productionSuggestions, 0);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("grossDemand", grossDemand);
    result.put("explodedDemands", explodedDemands);
    result.put("shortages", shortages);
    result.put("purchaseSuggestions", purchaseSuggestions);
    result.put("productionSuggestions", productionSuggestions);
    result.put("runDate", LocalDate.now().toString());
    return result;
  }

  private void explodeBom(UUID productId, Double quantity,
                          List<Map<String, Object>> explodedDemands,
                          List<Map<String, Object>> shortages,
                          List<Map<String, Object>> purchaseSuggestions,
                          List<Map<String, Object>> productionSuggestions,
                          int level) {
    if (level > 10) return;

    List<BillOfMaterial> activeBoms = bomRepository.findByProductIdAndStatus(productId, "ACTIVE");
    if (activeBoms.isEmpty()) {
      Map<String, Object> shortage = new HashMap<>();
      shortage.put("productId", productId);
      shortage.put("requiredQuantity", quantity);
      shortage.put("level", level);
      shortage.put("type", "PURCHASE");
      shortages.add(shortage);

      Map<String, Object> suggestion = new HashMap<>();
      suggestion.put("productId", productId);
      suggestion.put("suggestedQuantity", quantity);
      suggestion.put("type", "PURCHASE");
      suggestion.put("priority", level == 0 ? "HIGH" : "MEDIUM");
      purchaseSuggestions.add(suggestion);
      return;
    }

    BillOfMaterial bom = activeBoms.get(0);
    List<BOMLine> lines = bomLineRepository.findByBomId(bom.getId());

    for (BOMLine line : lines) {
      double requiredQty = quantity * line.getQuantity();
      double scrapAdjusted = requiredQty * (1 + line.getScrapPercentage() / 100.0);

      Map<String, Object> exploded = new HashMap<>();
      exploded.put("bomId", bom.getId());
      exploded.put("productId", productId);
      exploded.put("componentId", line.getComponentId());
      exploded.put("componentQuantity", line.getQuantity());
      exploded.put("requiredQuantity", scrapAdjusted);
      exploded.put("level", level);
      explodedDemands.add(exploded);

      List<BillOfMaterial> childBoms = bomRepository.findByProductIdAndStatus(line.getComponentId(), "ACTIVE");
      if (!childBoms.isEmpty()) {
        explodeBom(line.getComponentId(), scrapAdjusted, explodedDemands, shortages,
                   purchaseSuggestions, productionSuggestions, level + 1);
      } else {
        Map<String, Object> shortage = new HashMap<>();
        shortage.put("productId", line.getComponentId());
        shortage.put("requiredQuantity", scrapAdjusted);
        shortage.put("level", level + 1);
        shortage.put("type", "PURCHASE");
        shortages.add(shortage);

        Map<String, Object> suggestion = new HashMap<>();
        suggestion.put("productId", line.getComponentId());
        suggestion.put("suggestedQuantity", scrapAdjusted);
        suggestion.put("type", "PURCHASE");
        suggestion.put("priority", level <= 1 ? "HIGH" : "MEDIUM");
        purchaseSuggestions.add(suggestion);
      }
    }
  }
}
