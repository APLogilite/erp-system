package com.erp.core.metadata.config;

import com.erp.core.metadata.dto.ActionMetadataDto;
import com.erp.core.metadata.dto.FieldMetadataDto;
import com.erp.core.metadata.dto.ModelMetadataDto;
import com.erp.core.metadata.dto.PermissionMetadataDto;
import com.erp.core.metadata.dto.ViewMetadataDto;
import com.erp.core.metadata.registry.MetadataRegistry;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Bootstrap metadata configuration.
 * Registers sample ERP models for testing frontend runtime.
 */
@Component
public class MetadataBootstrap {

  private final MetadataRegistry metadataRegistry;

  public MetadataBootstrap(MetadataRegistry metadataRegistry) {
    this.metadataRegistry = metadataRegistry;
  }

  @PostConstruct
  public void initializeMetadata() {
    registerBusinessPartner();
    registerProduct();
    registerWarehouse();
    registerSalesOrder();
    registerInventoryTransaction();
  }

  private void registerBusinessPartner() {
    ModelMetadataDto model = new ModelMetadataDto(
        "business_partner",
        "Business Partner",
        "Master data for customers and vendors"
    );

    model.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("type", "Type", "string", true, false),
        new FieldMetadataDto("credit_limit", "Credit Limit", "decimal", false, false),
        new FieldMetadataDto("payment_terms", "Payment Terms", "string", false, false)
    ));

    metadataRegistry.registerModel(model);

    ViewMetadataDto listView = new ViewMetadataDto(
        "business_partner_list",
        "business_partner",
        ViewMetadataDto.ViewType.GRID,
        "Business Partners"
    );
    metadataRegistry.registerView(listView);

    ViewMetadataDto formView = new ViewMetadataDto(
        "business_partner_form",
        "business_partner",
        ViewMetadataDto.ViewType.FORM,
        "Business Partner Details"
    );
    metadataRegistry.registerView(formView);
  }

  private void registerProduct() {
    ModelMetadataDto model = new ModelMetadataDto(
        "product",
        "Product",
        "Master data for products and SKUs"
    );

    model.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("sku", "SKU", "string", true, false),
        new FieldMetadataDto("category", "Category", "string", false, false),
        new FieldMetadataDto("cost_price", "Cost Price", "decimal", true, false),
        new FieldMetadataDto("sale_price", "Sale Price", "decimal", true, false)
    ));

    metadataRegistry.registerModel(model);

    ViewMetadataDto listView = new ViewMetadataDto(
        "product_list",
        "product",
        ViewMetadataDto.ViewType.GRID,
        "Products"
    );
    metadataRegistry.registerView(listView);

    ViewMetadataDto formView = new ViewMetadataDto(
        "product_form",
        "product",
        ViewMetadataDto.ViewType.FORM,
        "Product Details"
    );
    metadataRegistry.registerView(formView);
  }

  private void registerWarehouse() {
    ModelMetadataDto model = new ModelMetadataDto(
        "warehouse",
        "Warehouse",
        "Storage facility configuration"
    );

    model.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("location", "Location", "string", false, false)
    ));

    metadataRegistry.registerModel(model);

    ViewMetadataDto listView = new ViewMetadataDto(
        "warehouse_list",
        "warehouse",
        ViewMetadataDto.ViewType.GRID,
        "Warehouses"
    );
    metadataRegistry.registerView(listView);
  }

  private void registerSalesOrder() {
    ModelMetadataDto model = new ModelMetadataDto(
        "sales_order",
        "Sales Order",
        "Customer purchase orders"
    );

    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("order_number", "Order Number", "string", true, true),
        new FieldMetadataDto("customer_id", "Customer", "string", true, false),
        new FieldMetadataDto("order_date", "Order Date", "date", true, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("total_amount", "Total Amount", "decimal", false, true)
    ));

    metadataRegistry.registerModel(model);

    ViewMetadataDto listView = new ViewMetadataDto(
        "sales_order_list",
        "sales_order",
        ViewMetadataDto.ViewType.GRID,
        "Sales Orders"
    );
    metadataRegistry.registerView(listView);

    ViewMetadataDto formView = new ViewMetadataDto(
        "sales_order_form",
        "sales_order",
        ViewMetadataDto.ViewType.FORM,
        "Sales Order Details"
    );
    metadataRegistry.registerView(formView);
  }

  private void registerInventoryTransaction() {
    ModelMetadataDto model = new ModelMetadataDto(
        "inventory_transaction",
        "Inventory Transaction",
        "Stock movement tracking"
    );

    model.setFields(Arrays.asList(
        new FieldMetadataDto("transaction_number", "Transaction Number", "string", true, true),
        new FieldMetadataDto("product_id", "Product", "string", true, false),
        new FieldMetadataDto("warehouse_id", "Warehouse", "string", true, false),
        new FieldMetadataDto("quantity", "Quantity", "integer", true, false),
        new FieldMetadataDto("transaction_date", "Transaction Date", "date", true, false),
        new FieldMetadataDto("transaction_type", "Type", "string", true, false)
    ));

    metadataRegistry.registerModel(model);

    ViewMetadataDto listView = new ViewMetadataDto(
        "inventory_transaction_list",
        "inventory_transaction",
        ViewMetadataDto.ViewType.GRID,
        "Inventory Transactions"
    );
    metadataRegistry.registerView(listView);
  }
}
