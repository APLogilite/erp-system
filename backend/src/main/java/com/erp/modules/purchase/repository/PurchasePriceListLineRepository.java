package com.erp.modules.purchase.repository;

import com.erp.modules.purchase.entity.PurchasePriceListLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchasePriceListLineRepository extends JpaRepository<PurchasePriceListLine, UUID> {

  List<PurchasePriceListLine> findByPriceListIdAndProductIdAndIsActiveTrueAndMinQtyLessThanEqual(
      UUID priceListId, UUID productId, BigDecimal quantity);
}
