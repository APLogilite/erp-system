package com.erp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EntityScan(basePackages = "com.erp.modules")
public class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        // Test basic connection
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void testTablesExist() {
        // Check if our tables exist
        Integer productCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'PRODUCTS'", Integer.class);
        assertThat(productCount).isGreaterThan(0);

        Integer warehouseCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'WAREHOUSES'", Integer.class);
        assertThat(warehouseCount).isGreaterThan(0);

        Integer orderCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ORDERS'", Integer.class);
        assertThat(orderCount).isGreaterThan(0);
    }

    @Test
    public void testSampleData() {
        // Check that tables are created and empty (no sample data in tests)
        Integer productCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
        assertThat(productCount).isEqualTo(0);

        Integer warehouseCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM warehouses", Integer.class);
        assertThat(warehouseCount).isEqualTo(0);

        Integer orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
        assertThat(orderCount).isEqualTo(0);
    }
}