package com.erp.modules.purchase.repository;

import com.erp.modules.purchase.entity.PurchasePriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchasePriceListRepository extends JpaRepository<PurchasePriceList, UUID> {

  List<PurchasePriceList> findByVendorIdAndIsActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqual(
      UUID vendorId, LocalDate validOnFrom, LocalDate validOnTo);
}
