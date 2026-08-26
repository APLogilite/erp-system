package com.erp.modules.purchase.service;

import com.erp.modules.product.entity.Product;
import com.erp.modules.product.repository.ProductRepository;
import com.erp.modules.purchase.dto.PriceResolutionRequest;
import com.erp.modules.purchase.dto.PriceResolutionResponse;
import com.erp.modules.purchase.entity.PurchasePriceList;
import com.erp.modules.purchase.entity.PurchasePriceListLine;
import com.erp.modules.purchase.repository.PurchasePriceListLineRepository;
import com.erp.modules.purchase.repository.PurchasePriceListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PurchasePriceResolutionService {

  private final PurchasePriceListRepository priceListRepository;
  private final PurchasePriceListLineRepository lineRepository;
  private final ProductRepository productRepository;

  public PurchasePriceResolutionService(PurchasePriceListRepository priceListRepository,
                                        PurchasePriceListLineRepository lineRepository,
                                        ProductRepository productRepository) {
    this.priceListRepository = priceListRepository;
    this.lineRepository = lineRepository;
    this.productRepository = productRepository;
  }

  @Transactional(readOnly = true)
  public PriceResolutionResponse resolve(PriceResolutionRequest request) {
    Objects.requireNonNull(request.getVendorId(), "vendorId is required");
    Objects.requireNonNull(request.getProductId(), "productId is required");
    Objects.requireNonNull(request.getQuantity(), "quantity is required");

    LocalDate date = request.getDate() != null ? request.getDate() : LocalDate.now();

    List<PurchasePriceList> activeLists = priceListRepository
        .findByVendorIdAndIsActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqual(
            request.getVendorId(), date, date);

    for (PurchasePriceList list : activeLists) {
      List<PurchasePriceListLine> candidates = lineRepository
          .findByPriceListIdAndProductIdAndIsActiveTrueAndMinQtyLessThanEqual(
              list.getId(), request.getProductId(), request.getQuantity());

      if (request.getUomId() != null) {
        candidates = candidates.stream()
            .filter(line -> request.getUomId().equals(line.getUomId()))
            .toList();
      }

      if (!candidates.isEmpty()) {
        PurchasePriceListLine best = candidates.stream()
            .max(Comparator.comparing(PurchasePriceListLine::getMinQty))
            .orElseThrow();
        return new PriceResolutionResponse(
            request.getVendorId(),
            request.getProductId(),
            list.getId(),
            request.getQuantity(),
            best.getUnitPrice(),
            list.getCurrency(),
            "PRICE_LIST"
        );
      }
    }

    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + request.getProductId()));

    BigDecimal fallbackPrice = product.getUnitPrice() != null ? product.getUnitPrice() : BigDecimal.ZERO;
    return new PriceResolutionResponse(
        request.getVendorId(),
        request.getProductId(),
        null,
        request.getQuantity(),
        fallbackPrice,
        product.getUom() != null ? product.getUom() : "USD",
        "PRODUCT"
    );
  }
}
