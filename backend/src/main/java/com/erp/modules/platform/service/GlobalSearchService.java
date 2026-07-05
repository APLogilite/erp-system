package com.erp.modules.platform.service;

import com.erp.modules.platform.dto.SearchResultResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class GlobalSearchService {

  private final JdbcTemplate jdbcTemplate;

  public GlobalSearchService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public List<SearchResultResponse> search(String query, String module, int limit) {
    if (query == null || query.trim().isEmpty()) {
      return List.of();
    }
    String searchTerm = "%" + query.toLowerCase() + "%";
    int maxResults = Math.min(limit > 0 ? limit : 20, 100);

    List<SearchResultResponse> results = new ArrayList<>();

    if (module == null || module.isEmpty() || "products".equals(module)) {
      results.addAll(searchTable("product", "code", "name", "description", searchTerm, maxResults));
    }
    if (module == null || module.isEmpty() || "business_partners".equals(module)) {
      results.addAll(searchTable("business_partner", "code", "name", "email", searchTerm, maxResults));
    }
    if (module == null || module.isEmpty() || "sales_orders".equals(module)) {
      results.addAll(searchTable("sales_order", "order_number", "status", "notes", searchTerm, maxResults));
    }
    if (module == null || module.isEmpty() || "projects".equals(module)) {
      results.addAll(searchTable("project", "code", "name", "description", searchTerm, maxResults));
    }
    if (module == null || module.isEmpty() || "employees".equals(module)) {
      results.addAll(searchTable("employee", "code", "first_name", "last_name", searchTerm, maxResults));
    }
    if (module == null || module.isEmpty() || "assets".equals(module)) {
      results.addAll(searchTable("asset", "code", "name", "description", searchTerm, maxResults));
    }
    if (module == null || module.isEmpty() || "documents".equals(module)) {
      results.addAll(searchTable("documents", "file_name", "category", "module", searchTerm, maxResults));
    }
    if (module == null || module.isEmpty() || "reports".equals(module)) {
      results.addAll(searchTable("report_definitions", "report_code", "name", "description", searchTerm, maxResults));
    }

    return results.stream().sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))
        .limit(maxResults).collect(Collectors.toList());
  }

  private List<SearchResultResponse> searchTable(String table, String col1, String col2,
                                                 String col3, String searchTerm, int limit) {
    List<SearchResultResponse> results = new ArrayList<>();
    try {
      String sql = "SELECT id, COALESCE(" + col1 + ",'') as c1, COALESCE(" + col2 + ",'') as c2, "
          + "COALESCE(" + col3 + ",'') as c3 FROM " + table
          + " WHERE LOWER(" + col1 + ") LIKE ? OR LOWER(" + col2 + ") LIKE ?"
          + (col3 != null ? " OR LOWER(" + col3 + ") LIKE ?" : "")
          + " LIMIT ?";

      String c3 = col3 != null ? col3 : col2;
      jdbcTemplate.query(sql, rs -> {
        SearchResultResponse r = new SearchResultResponse();
        r.setId(java.util.UUID.fromString(rs.getString("id")));
        r.setModule(table);
        r.setRecordId(rs.getString("id"));
        r.setTitle(rs.getString("c1") + " - " + rs.getString("c2"));
        r.setDescription(rs.getString("c3"));
        r.setUrl("/" + table + "/" + rs.getString("id"));
        r.setScore(1.0f);
        results.add(r);
      }, searchTerm, searchTerm, searchTerm, limit);
    } catch (Exception e) {
      // table may not exist or have different schema
    }
    return results;
  }

  public List<String> autocomplete(String prefix, int limit) {
    if (prefix == null || prefix.trim().isEmpty()) return List.of();
    return search(prefix, null, limit).stream()
        .map(SearchResultResponse::getTitle)
        .distinct()
        .limit(limit)
        .collect(Collectors.toList());
  }
}
