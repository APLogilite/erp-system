package com.erp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Database-agnostic integration test.
 *
 * Uses only JDBC standard APIs — no vendor-specific SQL (no INFORMATION_SCHEMA,
 * no SELECT 1, no DUAL). This makes the test portable across H2, PostgreSQL,
 * Oracle, MySQL, SQL Server, and any JDBC-compliant database.
 *
 * Checks:
 * - The DataSource connection is live (via {@link DatabaseMetaData})
 * - All expected JPA entity tables are created
 * - Core business tables are accessible via standard SQL
 */
@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Test
    public void testDatabaseConnection() throws Exception {
        // JDBC standard connection verification — no vendor-specific SQL.
        try (var conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            assertThat(meta.getDatabaseProductName()).isNotNull();
            assertThat(meta.getURL()).isNotNull();
            assertThat(meta.getDriverName()).isNotNull();
            // Verify connection is live and transactional
            assertThat(conn.isValid(5)).isTrue();
            assertThat(conn.isClosed()).isFalse();
        }
    }

    @Test
    public void testJpaEntityTablesExist() throws Exception {
        // Use JDBC DatabaseMetaData.getTables() — works across all databases
        // (H2, PostgreSQL, Oracle, MySQL, SQL Server, etc.) unlike vendor-specific
        // INFORMATION_SCHEMA (no Oracle) or USER_TABLES (not in H2).
        //
        // Table name case handling:
        //   H2, PostgreSQL → lowercase for unquoted identifiers
        //   Oracle → uppercase by default
        // We try all three cases for portability.
        String[] entityTables = {
                "products",
                "product_categories",
                "orders",
                "order_lines",
                "m1_warehouses",
                "warehouse_locations",
                "business_partners",
                "identity_tenants",
                "identity_users",
                "identity_roles"
        };

        DatabaseMetaData metaData;
        try (var conn = dataSource.getConnection()) {
            metaData = conn.getMetaData();
        }

        for (String tableName : entityTables) {
            boolean found = false;
            for (String name : new String[]{tableName, tableName.toUpperCase(), tableName.toLowerCase()}) {
                try (ResultSet rs = metaData.getTables(null, null, name, new String[]{"TABLE"})) {
                    if (rs.next()) {
                        found = true;
                        break;
                    }
                }
            }
            assertThat(found)
                    .as("Table '%s' should exist (created by JPA ddl-auto=create-drop)", tableName)
                    .isTrue();
        }
    }

    @Test
    public void testCoreTablesQueryable() {
        // Standard ANSI SQL — works in H2, PostgreSQL, Oracle, MySQL, SQL Server.
        // Not checking emptiness because IdentitySeedData seeds demo data at startup.
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class))
                .isNotNull();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class))
                .isNotNull();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM m1_warehouses", Integer.class))
                .isNotNull();
    }
}
