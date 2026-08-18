package com.erp.modules.common.service;

import com.erp.modules.common.dto.PriceResolveRequest;
import com.erp.modules.common.dto.PriceResolveResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PriceResolutionService {

  private static final Logger log = LoggerFactory.getLogger(PriceResolutionService.class);
  private static final long CACHE_TTL_MS = 5 * 60 * 1000;

  private final NamedParameterJdbcTemplate jdbc;
  private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

  public PriceResolutionService(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public PriceResolveResponse resolve(PriceResolveRequest req) {
    String cacheKey = req.getProductId() + "|" + req.getPartnerId() + "|" + req.getQuantity();
    CacheEntry cached = cache.get(cacheKey);
    if (cached != null && System.currentTimeMillis() - cached.timestamp < CACHE_TTL_MS) {
      return cached.response;
    }

    PriceResolveResponse response = doResolve(req);
    cache.put(cacheKey, new CacheEntry(response, System.currentTimeMillis()));
    if (cache.size() > 500) {
      long now = System.currentTimeMillis();
      cache.entrySet().removeIf(e -> now - e.getValue().timestamp > CACHE_TTL_MS);
    }
    return response;
  }

  private PriceResolveResponse doResolve(PriceResolveRequest req) {
    LocalDate today = LocalDate.now();

    Map<String, Object> priceList = findPriceList(req.getPartnerId(), today);
    String source = "customer_price_list";

    if (priceList == null) {
      priceList = findDefaultPriceList(today);
      source = priceList != null ? "default_price_list" : null;
    }

    UUID priceListId = null;
    BigDecimal unitPrice = null;

    if (priceList != null) {
      priceListId = (UUID) priceList.get("id");
      unitPrice = findBestLinePrice(priceListId, req.getProductId(), req.getQuantity());
    }

    if (unitPrice == null) {
      source = "product_base";
      unitPrice = findProductBasePrice(req.getProductId());
    }

    if (unitPrice == null) {
      throw new IllegalArgumentException("Product not found: " + req.getProductId());
    }

    PriceResolveResponse resp = new PriceResolveResponse();
    resp.setUnitPrice(unitPrice);
    resp.setPriceListId(priceListId);
    resp.setSource(source);
    return resp;
  }

  private Map<String, Object> findPriceList(UUID partnerId, LocalDate today) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT id FROM md_price_list WHERE partner_id = :pid AND is_active = true "
          + "AND (valid_from IS NULL OR valid_from <= :today) AND (valid_to IS NULL OR valid_to >= :today) "
          + "LIMIT 1",
        new MapSqlParameterSource()
          .addValue("pid", partnerId)
          .addValue("today", today));
    return rows.isEmpty() ? null : Map.of("id", rows.get(0).get("id"));
  }

  private Map<String, Object> findDefaultPriceList(LocalDate today) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT id FROM md_price_list WHERE is_default = true AND is_active = true "
          + "AND (valid_from IS NULL OR valid_from <= :today) AND (valid_to IS NULL OR valid_to >= :today) "
          + "LIMIT 1",
        new MapSqlParameterSource().addValue("today", today));
    return rows.isEmpty() ? null : Map.of("id", rows.get(0).get("id"));
  }

  private BigDecimal findBestLinePrice(UUID priceListId, UUID productId, BigDecimal qty) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT unit_price, min_quantity FROM md_price_list_line "
          + "WHERE price_list_id = :plid AND product_id = :pid AND is_active = true "
          + "AND (min_quantity IS NULL OR min_quantity <= :qty) "
          + "ORDER BY min_quantity DESC NULLS LAST LIMIT 1",
        new MapSqlParameterSource()
          .addValue("plid", priceListId)
          .addValue("pid", productId)
          .addValue("qty", qty != null ? qty : BigDecimal.ONE));
    return rows.isEmpty() ? null : (BigDecimal) rows.get(0).get("unit_price");
  }

  private BigDecimal findProductBasePrice(UUID productId) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT unit_price FROM md_product WHERE id = :pid AND is_active = true LIMIT 1",
        new MapSqlParameterSource().addValue("pid", productId));
    return rows.isEmpty() ? null : (BigDecimal) rows.get(0).get("unit_price");
  }

  private static class CacheEntry {
    final PriceResolveResponse response;
    final long timestamp;
    CacheEntry(PriceResolveResponse r, long t) { this.response = r; this.timestamp = t; }
  }
}
