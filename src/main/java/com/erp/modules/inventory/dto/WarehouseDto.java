package com.erp.modules.inventory.dto;

/**
 * Warehouse DTO.
 */
public class WarehouseDto {
    private String name;
    private String location;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}