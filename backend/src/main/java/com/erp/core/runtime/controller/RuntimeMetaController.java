package com.erp.core.runtime.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime metadata endpoints.
 *
 * GET /api/v1/runtime/meta/generation — Returns a "data generation" marker that
 * changes whenever the database is reseeded or a new Flyway migration is applied.
 * The frontend polls this to auto-invalidate cached window definitions, which
 * otherwise reference ghost UUIDs from a previous DB generation (ENH-004).
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/runtime/meta")
public class RuntimeMetaController {

  private static final Logger log = LoggerFactory.getLogger(RuntimeMetaController.class);

  private final JdbcTemplate jdbcTemplate;

  /** Startup timestamp — fallback generation when flyway_schema_history is absent. */
  private final long startupEpochMillis = System.currentTimeMillis();

  public RuntimeMetaController(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Returns the current data generation marker.
   *
   * Generation = "flyway-{maxInstalledRank}-{maxInstalledOnEpochMillis}" derived from
   * flyway_schema_history. A reseed recreates that table (new installed_on values), and
   * any new migration increases the rank — both change the marker. Plain restarts do not.
   * If flyway_schema_history does not exist (Flyway disabled), falls back to the
   * application startup timestamp ("start-{epochMillis}").
   */
  @GetMapping("/generation")
  public ResponseEntity<ApiResponse<Map<String, String>>> getGeneration() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    }

    String generation = resolveGeneration();
    return ResponseEntity.ok(ApiResponse.success(Map.of("generation", generation), "Generation retrieved."));
  }

  private String resolveGeneration() {
    try {
      Map<String, Object> row = jdbcTemplate.queryForMap(
          "SELECT max(installed_rank) AS rank, "
              + "COALESCE(extract(epoch from max(installed_on))::bigint, 0) AS installed_on "
              + "FROM flyway_schema_history");
      Object rank = row.get("rank");
      Object installedOn = row.get("installed_on");
      if (rank != null) {
        return "flyway-" + rank + "-" + installedOn;
      }
      log.debug("flyway_schema_history is empty — using startup timestamp as generation");
    } catch (Exception e) {
      log.debug("flyway_schema_history not available ({}), using startup timestamp as generation", e.getMessage());
    }
    return "start-" + startupEpochMillis;
  }
}
