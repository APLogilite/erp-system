package com.erp.modules.warehouse.dto;

import jakarta.validation.constraints.NotNull;

public class WarehouseRequestDTO {

    @NotNull
    private String code;

    @NotNull
    private String name;

    private String description;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
