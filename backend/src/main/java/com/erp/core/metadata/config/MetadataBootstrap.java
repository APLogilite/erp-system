package com.erp.core.metadata.config;

import com.erp.core.metadata.dto.ActionMetadataDto;
import com.erp.core.metadata.dto.FieldMetadataDto;
import com.erp.core.metadata.dto.ModelMetadataDto;
import com.erp.core.metadata.dto.PermissionMetadataDto;
import com.erp.core.metadata.dto.ViewMetadataDto;
import com.erp.core.metadata.dto.WorkflowMetadataDto;
import com.erp.core.metadata.dto.WorkflowStateDto;
import com.erp.core.metadata.dto.WorkflowTransitionDto;
import com.erp.core.metadata.registry.MetadataRegistry;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class MetadataBootstrap {

  private final MetadataRegistry metadataRegistry;

  public MetadataBootstrap(MetadataRegistry metadataRegistry) {
    this.metadataRegistry = metadataRegistry;
  }

  @PostConstruct
  public void initializeMetadata() {
    registerProductModule();
    registerBusinessPartnerModule();
    registerWarehouseModule();
    registerSalesOrder();
    registerInventoryTransaction();
  }

  private void registerProductModule() {
    ModelMetadataDto model = new ModelMetadataDto("product", "Product", "Master data for products and SKUs");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("description", "Description", "text", false, false),
        new FieldMetadataDto("sku", "SKU", "string", false, false),
        new FieldMetadataDto("barcode", "Barcode", "string", false, false),
        new FieldMetadataDto("uom", "UOM", "string", false, false),
        new FieldMetadataDto("productType", "Product Type", "string", true, false),
        new FieldMetadataDto("isStocked", "Is Stocked", "boolean", false, false),
        new FieldMetadataDto("isSold", "Is Sold", "boolean", false, false),
        new FieldMetadataDto("isPurchased", "Is Purchased", "boolean", false, false),
        new FieldMetadataDto("categoryId", "Category", "string", false, false)
    ));
    metadataRegistry.registerModel(model);

    metadataRegistry.registerView(new ViewMetadataDto("product_list", "product", ViewMetadataDto.ViewType.GRID, "Products"));
    metadataRegistry.registerView(new ViewMetadataDto("product_form", "product", ViewMetadataDto.ViewType.FORM, "Product Details"));

    metadataRegistry.registerWorkflow(createWorkflow("product_workflow", "product",
        Arrays.asList("Draft", "Active", "Archived"),
        Arrays.asList("activate", "archive"),
        Arrays.asList("Draft", "Active"),
        Arrays.asList("Active", "Archived")));

    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_product_user", "product", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_product_manager", "product", PermissionMetadataDto.PermissionType.ADMIN));

    metadataRegistry.registerAction(new ActionMetadataDto("act_product_create", "Create Product", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_product_export", "Export Products", ActionMetadataDto.ActionType.EXPORT));
  }

  private void registerBusinessPartnerModule() {
    ModelMetadataDto model = new ModelMetadataDto("business_partner", "Business Partner", "Master data for customers, vendors, and employees");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("partnerType", "Partner Type", "string", true, false),
        new FieldMetadataDto("email", "Email", "string", false, false),
        new FieldMetadataDto("phone", "Phone", "string", false, false),
        new FieldMetadataDto("mobile", "Mobile", "string", false, false),
        new FieldMetadataDto("website", "Website", "string", false, false),
        new FieldMetadataDto("taxId", "Tax ID", "string", false, false),
        new FieldMetadataDto("isCustomer", "Is Customer", "boolean", false, false),
        new FieldMetadataDto("isVendor", "Is Vendor", "boolean", false, false),
        new FieldMetadataDto("isEmployee", "Is Employee", "boolean", false, false)
    ));
    metadataRegistry.registerModel(model);

    metadataRegistry.registerView(new ViewMetadataDto("business_partner_list", "business_partner", ViewMetadataDto.ViewType.GRID, "Business Partners"));
    metadataRegistry.registerView(new ViewMetadataDto("business_partner_form", "business_partner", ViewMetadataDto.ViewType.FORM, "Business Partner Details"));

    metadataRegistry.registerWorkflow(createWorkflow("business_partner_workflow", "business_partner",
        Arrays.asList("Draft", "Active", "Inactive"),
        Arrays.asList("activate", "deactivate"),
        Arrays.asList("Draft", "Active"),
        Arrays.asList("Active", "Inactive")));

    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_bp_user", "business_partner", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_bp_manager", "business_partner", PermissionMetadataDto.PermissionType.ADMIN));

    metadataRegistry.registerAction(new ActionMetadataDto("act_bp_create", "Create Partner", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_bp_export", "Export Partners", ActionMetadataDto.ActionType.EXPORT));
  }

  private void registerWarehouseModule() {
    ModelMetadataDto model = new ModelMetadataDto("warehouse", "Warehouse", "Storage facility configuration");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("description", "Description", "text", false, false)
    ));
    metadataRegistry.registerModel(model);

    metadataRegistry.registerView(new ViewMetadataDto("warehouse_list", "warehouse", ViewMetadataDto.ViewType.GRID, "Warehouses"));
    metadataRegistry.registerView(new ViewMetadataDto("warehouse_form", "warehouse", ViewMetadataDto.ViewType.FORM, "Warehouse Details"));

    metadataRegistry.registerWorkflow(createWorkflow("warehouse_workflow", "warehouse",
        Arrays.asList("Draft", "Active", "Closed"),
        Arrays.asList("activate", "close"),
        Arrays.asList("Draft", "Active"),
        Arrays.asList("Active", "Closed")));

    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_wh_user", "warehouse", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_wh_manager", "warehouse", PermissionMetadataDto.PermissionType.ADMIN));

    metadataRegistry.registerAction(new ActionMetadataDto("act_wh_create", "Create Warehouse", ActionMetadataDto.ActionType.CREATE));
  }

  private void registerSalesOrder() {
    ModelMetadataDto model = new ModelMetadataDto("sales_order", "Sales Order", "Customer purchase orders");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("order_number", "Order Number", "string", true, true),
        new FieldMetadataDto("customer_id", "Customer", "string", true, false),
        new FieldMetadataDto("order_date", "Order Date", "date", true, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("total_amount", "Total Amount", "decimal", false, true),
        new FieldMetadataDto("description", "Description", "text", false, false)
    ));
    metadataRegistry.registerModel(model);

    metadataRegistry.registerView(new ViewMetadataDto("sales_order_list", "sales_order", ViewMetadataDto.ViewType.GRID, "Sales Orders"));
    metadataRegistry.registerView(new ViewMetadataDto("sales_order_form", "sales_order", ViewMetadataDto.ViewType.FORM, "Sales Order Details"));

    WorkflowMetadataDto workflow = new WorkflowMetadataDto("sales_order_workflow", "sales_order");
    workflow.setStates(Arrays.asList(
        new WorkflowStateDto("DRAFT", "Draft", true),
        new WorkflowStateDto("CONFIRMED", "Confirmed", false),
        new WorkflowStateDto("COMPLETED", "Completed", false),
        new WorkflowStateDto("APPROVED", "Approved", false),
        new WorkflowStateDto("CLOSED", "Closed", false)
    ));
    workflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("submit", "Submit Order", "DRAFT", "CONFIRMED"),
        new WorkflowTransitionDto("complete", "Complete Order", "CONFIRMED", "COMPLETED"),
        new WorkflowTransitionDto("approve", "Approve Order", "COMPLETED", "APPROVED"),
        new WorkflowTransitionDto("close", "Close Order", "APPROVED", "CLOSED"),
        new WorkflowTransitionDto("reopen", "Reopen Order", "CLOSED", "DRAFT")
    ));
    metadataRegistry.registerWorkflow(workflow);

    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_create", "Create Sales Order", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_add_line", "Add Line", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_recalculate", "Recalculate", ActionMetadataDto.ActionType.CUSTOM));
  }

  private void registerInventoryTransaction() {
    ModelMetadataDto model = new ModelMetadataDto("inventory_transaction", "Inventory Transaction", "Stock movement tracking");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("document_number", "Document Number", "string", true, true),
        new FieldMetadataDto("warehouse_id", "Warehouse", "string", true, false),
        new FieldMetadataDto("transaction_type", "Type", "string", true, false),
        new FieldMetadataDto("transaction_date", "Transaction Date", "date", true, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("description", "Description", "text", false, false)
    ));
    metadataRegistry.registerModel(model);

    metadataRegistry.registerView(new ViewMetadataDto("inventory_transaction_list", "inventory_transaction", ViewMetadataDto.ViewType.GRID, "Inventory Transactions"));
    metadataRegistry.registerView(new ViewMetadataDto("inventory_transaction_form", "inventory_transaction", ViewMetadataDto.ViewType.FORM, "Inventory Transaction Details"));

    WorkflowMetadataDto workflow = new WorkflowMetadataDto("inventory_transaction_workflow", "inventory_transaction");
    workflow.setStates(Arrays.asList(
        new WorkflowStateDto("DRAFT", "Draft", true),
        new WorkflowStateDto("COMPLETED", "Completed", false),
        new WorkflowStateDto("POSTED", "Posted", false),
        new WorkflowStateDto("CLOSED", "Closed", false)
    ));
    workflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("complete", "Complete", "DRAFT", "COMPLETED"),
        new WorkflowTransitionDto("post", "Post", "COMPLETED", "POSTED"),
        new WorkflowTransitionDto("close", "Close", "POSTED", "CLOSED"),
        new WorkflowTransitionDto("void", "Void", "DRAFT", "CLOSED")
    ));
    metadataRegistry.registerWorkflow(workflow);

    metadataRegistry.registerAction(new ActionMetadataDto("act_inv_trans_create", "Create Transaction", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_inv_trans_post", "Post", ActionMetadataDto.ActionType.CUSTOM));
  }

  private WorkflowMetadataDto createWorkflow(String code, String modelCode,
                                              java.util.List<String> stateNames,
                                              java.util.List<String> transitionCodes,
                                              java.util.List<String> fromStates,
                                              java.util.List<String> toStates) {
    WorkflowMetadataDto workflow = new WorkflowMetadataDto(code, modelCode);
    workflow.setStates(Arrays.asList(
        new WorkflowStateDto(stateNames.get(0).toUpperCase(), stateNames.get(0), true),
        new WorkflowStateDto(stateNames.get(1).toUpperCase(), stateNames.get(1), false),
        new WorkflowStateDto(stateNames.get(2).toUpperCase(), stateNames.get(2), false)
    ));
    workflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto(transitionCodes.get(0), transitionCodes.get(0), fromStates.get(0).toUpperCase(), toStates.get(0).toUpperCase()),
        new WorkflowTransitionDto(transitionCodes.get(1), transitionCodes.get(1), fromStates.get(1).toUpperCase(), toStates.get(1).toUpperCase())
    ));
    return workflow;
  }
}
