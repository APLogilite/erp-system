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
import java.util.Arrays;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

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
    registerPurchaseOrder();
    registerReservation();
    registerChartOfAccounts();
    registerJournalEntry();
    registerManufacturing();
    registerCRM();
    registerProjects();
    registerService();
    registerHR();
    registerAssets();
    registerAnalytics();
    registerPlatform();
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
        new FieldMetadataDto("documentNo", "Document No", "string", true, true),
        new FieldMetadataDto("documentDate", "Document Date", "date", true, false),
        new FieldMetadataDto("customerId", "Customer", "string", true, false),
        new FieldMetadataDto("warehouseId", "Warehouse", "string", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("description", "Description", "text", false, false),
        new FieldMetadataDto("totalAmount", "Total Amount", "decimal", false, true),
        new FieldMetadataDto("currency", "Currency", "string", false, false)
    ));
    metadataRegistry.registerModel(model);

    metadataRegistry.registerView(new ViewMetadataDto("sales_order_list", "sales_order", ViewMetadataDto.ViewType.GRID, "Sales Orders"));
    metadataRegistry.registerView(new ViewMetadataDto("sales_order_form", "sales_order", ViewMetadataDto.ViewType.FORM, "Sales Order Details"));

    WorkflowMetadataDto workflow = new WorkflowMetadataDto("sales_order_workflow", "sales_order");
    workflow.setStates(Arrays.asList(
        new WorkflowStateDto("DRAFT", "Draft", true),
        new WorkflowStateDto("COMPLETED", "Completed", false),
        new WorkflowStateDto("APPROVED", "Approved", false),
        new WorkflowStateDto("CLOSED", "Closed", false)
    ));
    workflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("complete", "Complete", "DRAFT", "COMPLETED"),
        new WorkflowTransitionDto("approve", "Approve", "COMPLETED", "APPROVED"),
        new WorkflowTransitionDto("close", "Close", "APPROVED", "CLOSED"),
        new WorkflowTransitionDto("reopen", "Reopen", "CLOSED", "DRAFT"),
        new WorkflowTransitionDto("void", "Void", "DRAFT", "CLOSED")
    ));
    metadataRegistry.registerWorkflow(workflow);

    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_create", "Create Sales Order", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_add_line", "Add Line", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_recalculate", "Recalculate", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_complete", "Complete", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_approve", "Approve", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_close", "Close", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sales_order_print", "Print", ActionMetadataDto.ActionType.EXPORT));

    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_sales_user", "sales_order", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_sales_manager", "sales_order", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerInventoryTransaction() {
    ModelMetadataDto model = new ModelMetadataDto("inventory_transaction", "Inventory Transaction", "Stock movement tracking");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("documentNumber", "Document Number", "string", true, true),
        new FieldMetadataDto("warehouseId", "Warehouse", "string", true, false),
        new FieldMetadataDto("transactionType", "Type", "string", true, false),
        new FieldMetadataDto("transactionDate", "Transaction Date", "date", true, false),
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
    metadataRegistry.registerAction(new ActionMetadataDto("act_inv_trans_complete", "Complete", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_inv_trans_post", "Post", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_inv_trans_close", "Close", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_inv_trans_void", "Void", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_inv_trans_print", "Print", ActionMetadataDto.ActionType.EXPORT));

    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_inv_user", "inventory_transaction", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_inv_manager", "inventory_transaction", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerPurchaseOrder() {
    ModelMetadataDto model = new ModelMetadataDto("purchase_order", "Purchase Order", "Vendor procurement orders");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("documentNo", "Document No", "string", true, true),
        new FieldMetadataDto("documentDate", "Document Date", "date", true, false),
        new FieldMetadataDto("vendorId", "Vendor", "string", true, false),
        new FieldMetadataDto("warehouseId", "Warehouse", "string", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("description", "Description", "text", false, false),
        new FieldMetadataDto("currency", "Currency", "string", false, false),
        new FieldMetadataDto("totalAmount", "Total Amount", "decimal", false, true),
        new FieldMetadataDto("expectedDate", "Expected Date", "date", false, false)
    ));
    metadataRegistry.registerModel(model);

    metadataRegistry.registerView(new ViewMetadataDto("purchase_order_list", "purchase_order", ViewMetadataDto.ViewType.GRID, "Purchase Orders"));
    metadataRegistry.registerView(new ViewMetadataDto("purchase_order_form", "purchase_order", ViewMetadataDto.ViewType.FORM, "Purchase Order Details"));

    WorkflowMetadataDto workflow = new WorkflowMetadataDto("purchase_order_workflow", "purchase_order");
    workflow.setStates(Arrays.asList(
        new WorkflowStateDto("DRAFT", "Draft", true),
        new WorkflowStateDto("COMPLETED", "Completed", false),
        new WorkflowStateDto("APPROVED", "Approved", false),
        new WorkflowStateDto("RECEIVED", "Received", false),
        new WorkflowStateDto("CLOSED", "Closed", false)
    ));
    workflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("complete", "Complete", "DRAFT", "COMPLETED"),
        new WorkflowTransitionDto("approve", "Approve", "COMPLETED", "APPROVED"),
        new WorkflowTransitionDto("receive", "Receive", "APPROVED", "RECEIVED"),
        new WorkflowTransitionDto("close", "Close", "RECEIVED", "CLOSED"),
        new WorkflowTransitionDto("void", "Void", "DRAFT", "CLOSED"),
        new WorkflowTransitionDto("reopen", "Reopen", "CLOSED", "DRAFT")
    ));
    metadataRegistry.registerWorkflow(workflow);

    metadataRegistry.registerAction(new ActionMetadataDto("act_po_create", "Create Purchase Order", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_po_add_line", "Add Line", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_po_recalculate", "Recalculate", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_po_complete", "Complete", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_po_approve", "Approve", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_po_receive", "Receive", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_po_close", "Close", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_po_print", "Print", ActionMetadataDto.ActionType.EXPORT));

    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_purchase_user", "purchase_order", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_purchase_manager", "purchase_order", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerReservation() {
    ModelMetadataDto model = new ModelMetadataDto("reservation", "Reservation", "Inventory reservation tracking");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("productId", "Product", "string", true, false),
        new FieldMetadataDto("warehouseId", "Warehouse", "string", true, false),
        new FieldMetadataDto("locationId", "Location", "string", false, false),
        new FieldMetadataDto("quantity", "Quantity", "decimal", true, false),
        new FieldMetadataDto("reservedQuantity", "Reserved Qty", "decimal", false, true),
        new FieldMetadataDto("sourceDocument", "Source Document", "string", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true)
    ));
    metadataRegistry.registerModel(model);

    metadataRegistry.registerView(new ViewMetadataDto("reservation_list", "reservation", ViewMetadataDto.ViewType.GRID, "Reservations"));
    metadataRegistry.registerView(new ViewMetadataDto("reservation_form", "reservation", ViewMetadataDto.ViewType.FORM, "Reservation Details"));

    metadataRegistry.registerAction(new ActionMetadataDto("act_reserve", "Reserve", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_release", "Release", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_consume", "Consume", ActionMetadataDto.ActionType.CUSTOM));

    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_inv_user_res", "reservation", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_inv_manager_res", "reservation", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerChartOfAccounts() {
    ModelMetadataDto model = new ModelMetadataDto("chart_of_accounts", "Chart of Accounts", "Financial account definitions");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("accountCode", "Account Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("description", "Description", "text", false, false),
        new FieldMetadataDto("accountType", "Account Type", "string", true, false),
        new FieldMetadataDto("parentId", "Parent Account", "string", false, false),
        new FieldMetadataDto("currency", "Currency", "string", false, false),
        new FieldMetadataDto("isControlAccount", "Control Account", "boolean", false, false)
    ));
    metadataRegistry.registerModel(model);
    metadataRegistry.registerView(new ViewMetadataDto("chart_of_accounts_list", "chart_of_accounts", ViewMetadataDto.ViewType.GRID, "Chart of Accounts"));
    metadataRegistry.registerView(new ViewMetadataDto("chart_of_accounts_form", "chart_of_accounts", ViewMetadataDto.ViewType.FORM, "Account Details"));
    metadataRegistry.registerAction(new ActionMetadataDto("act_coa_create", "Create Account", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_coa_user", "chart_of_accounts", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_coa_manager", "chart_of_accounts", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerJournalEntry() {
    ModelMetadataDto model = new ModelMetadataDto("journal_entry", "Journal Entry", "Financial transaction document");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("documentNo", "Document No", "string", true, true),
        new FieldMetadataDto("documentDate", "Document Date", "date", true, false),
        new FieldMetadataDto("description", "Description", "text", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("totalDebit", "Total Debit", "decimal", false, true),
        new FieldMetadataDto("totalCredit", "Total Credit", "decimal", false, true)
    ));
    metadataRegistry.registerModel(model);
    metadataRegistry.registerView(new ViewMetadataDto("journal_entry_list", "journal_entry", ViewMetadataDto.ViewType.GRID, "Journal Entries"));
    metadataRegistry.registerView(new ViewMetadataDto("journal_entry_form", "journal_entry", ViewMetadataDto.ViewType.FORM, "Journal Entry Details"));

    WorkflowMetadataDto workflow = new WorkflowMetadataDto("journal_entry_workflow", "journal_entry");
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
        new WorkflowTransitionDto("reopen", "Reopen", "CLOSED", "DRAFT"),
        new WorkflowTransitionDto("void", "Void", "DRAFT", "CLOSED")
    ));
    metadataRegistry.registerWorkflow(workflow);

    metadataRegistry.registerAction(new ActionMetadataDto("act_je_create", "Create Journal Entry", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_je_complete", "Complete", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_je_post", "Post", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_je_close", "Close", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_je_reverse", "Reverse", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_je_print", "Print", ActionMetadataDto.ActionType.EXPORT));

    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_je_user", "journal_entry", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_je_manager", "journal_entry", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerManufacturing() {
    ModelMetadataDto bomModel = new ModelMetadataDto("bill_of_material", "Bill of Material", "Product manufacturing definition");
    bomModel.setWorkflowEnabled(true);
    bomModel.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("productId", "Product", "string", true, false),
        new FieldMetadataDto("revision", "Revision", "string", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("effectiveFrom", "Effective From", "date", false, false),
        new FieldMetadataDto("effectiveTo", "Effective To", "date", false, false)
    ));
    metadataRegistry.registerModel(bomModel);
    metadataRegistry.registerView(new ViewMetadataDto("bom_list", "bill_of_material", ViewMetadataDto.ViewType.GRID, "Bill of Materials"));
    metadataRegistry.registerView(new ViewMetadataDto("bom_form", "bill_of_material", ViewMetadataDto.ViewType.FORM, "BOM Details"));

    WorkflowMetadataDto bomWorkflow = new WorkflowMetadataDto("bom_workflow", "bill_of_material");
    bomWorkflow.setStates(Arrays.asList(
        new WorkflowStateDto("DRAFT", "Draft", true),
        new WorkflowStateDto("ACTIVE", "Active", false),
        new WorkflowStateDto("ARCHIVED", "Archived", false)
    ));
    bomWorkflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("approve", "Approve", "DRAFT", "ACTIVE"),
        new WorkflowTransitionDto("archive", "Archive", "ACTIVE", "ARCHIVED")
    ));
    metadataRegistry.registerWorkflow(bomWorkflow);
    metadataRegistry.registerAction(new ActionMetadataDto("act_bom_create", "Create BOM", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_bom_approve", "Approve", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_bom_user", "bill_of_material", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_bom_manager", "bill_of_material", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto routingModel = new ModelMetadataDto("routing", "Routing", "Manufacturing process definition");
    routingModel.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("description", "Description", "text", false, false)
    ));
    metadataRegistry.registerModel(routingModel);
    metadataRegistry.registerView(new ViewMetadataDto("routing_list", "routing", ViewMetadataDto.ViewType.GRID, "Routings"));
    metadataRegistry.registerView(new ViewMetadataDto("routing_form", "routing", ViewMetadataDto.ViewType.FORM, "Routing Details"));
    metadataRegistry.registerAction(new ActionMetadataDto("act_routing_create", "Create Routing", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_routing_user", "routing", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_routing_manager", "routing", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto wcModel = new ModelMetadataDto("work_center", "Work Center", "Production resource definition");
    wcModel.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("capacity", "Capacity", "decimal", false, false),
        new FieldMetadataDto("costPerHour", "Cost/Hour", "decimal", false, false),
        new FieldMetadataDto("efficiency", "Efficiency", "decimal", false, false)
    ));
    metadataRegistry.registerModel(wcModel);
    metadataRegistry.registerView(new ViewMetadataDto("work_center_list", "work_center", ViewMetadataDto.ViewType.GRID, "Work Centers"));
    metadataRegistry.registerView(new ViewMetadataDto("work_center_form", "work_center", ViewMetadataDto.ViewType.FORM, "Work Center Details"));
    metadataRegistry.registerAction(new ActionMetadataDto("act_wc_create", "Create Work Center", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_wc_user", "work_center", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_wc_manager", "work_center", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto moModel = new ModelMetadataDto("manufacturing_order", "Manufacturing Order", "Production execution order");
    moModel.setWorkflowEnabled(true);
    moModel.setFields(Arrays.asList(
        new FieldMetadataDto("documentNo", "Document No", "string", true, true),
        new FieldMetadataDto("productId", "Product", "string", true, false),
        new FieldMetadataDto("bomId", "BOM", "string", false, false),
        new FieldMetadataDto("plannedQuantity", "Planned Qty", "decimal", true, false),
        new FieldMetadataDto("completedQuantity", "Completed Qty", "decimal", false, true),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("priority", "Priority", "string", false, false),
        new FieldMetadataDto("plannedStart", "Start Date", "date", false, false),
        new FieldMetadataDto("plannedEnd", "End Date", "date", false, false)
    ));
    metadataRegistry.registerModel(moModel);
    metadataRegistry.registerView(new ViewMetadataDto("mo_list", "manufacturing_order", ViewMetadataDto.ViewType.GRID, "Manufacturing Orders"));
    metadataRegistry.registerView(new ViewMetadataDto("mo_form", "manufacturing_order", ViewMetadataDto.ViewType.FORM, "Manufacturing Order Details"));

    WorkflowMetadataDto moWorkflow = new WorkflowMetadataDto("mo_workflow", "manufacturing_order");
    moWorkflow.setStates(Arrays.asList(
        new WorkflowStateDto("DRAFT", "Draft", true),
        new WorkflowStateDto("PLANNED", "Planned", false),
        new WorkflowStateDto("RELEASED", "Released", false),
        new WorkflowStateDto("IN_PRODUCTION", "In Production", false),
        new WorkflowStateDto("COMPLETED", "Completed", false),
        new WorkflowStateDto("CLOSED", "Closed", false)
    ));
    moWorkflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("plan", "Plan", "DRAFT", "PLANNED"),
        new WorkflowTransitionDto("release", "Release", "PLANNED", "RELEASED"),
        new WorkflowTransitionDto("start", "Start", "RELEASED", "IN_PRODUCTION"),
        new WorkflowTransitionDto("complete", "Complete", "IN_PRODUCTION", "COMPLETED"),
        new WorkflowTransitionDto("close", "Close", "COMPLETED", "CLOSED"),
        new WorkflowTransitionDto("void", "Void", "DRAFT", "CLOSED"),
        new WorkflowTransitionDto("reopen", "Reopen", "CLOSED", "DRAFT")
    ));
    metadataRegistry.registerWorkflow(moWorkflow);

    metadataRegistry.registerAction(new ActionMetadataDto("act_mo_create", "Create MO", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_mo_plan", "Plan", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_mo_release", "Release", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_mo_start", "Start", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_mo_complete", "Complete", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_mo_close", "Close", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_mo_print", "Print", ActionMetadataDto.ActionType.EXPORT));

    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_mo_user", "manufacturing_order", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_mo_manager", "manufacturing_order", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerCRM() {
    ModelMetadataDto leadModel = new ModelMetadataDto("lead", "Lead", "Customer acquisition record");
    leadModel.setWorkflowEnabled(true);
    leadModel.setFields(Arrays.asList(
        new FieldMetadataDto("leadNumber", "Lead Number", "string", true, true),
        new FieldMetadataDto("company", "Company", "string", true, false),
        new FieldMetadataDto("contactName", "Contact Name", "string", true, false),
        new FieldMetadataDto("email", "Email", "string", false, false),
        new FieldMetadataDto("phone", "Phone", "string", false, false),
        new FieldMetadataDto("source", "Source", "string", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("expectedValue", "Expected Value", "decimal", false, false)
    ));
    metadataRegistry.registerModel(leadModel);
    metadataRegistry.registerView(new ViewMetadataDto("lead_list", "lead", ViewMetadataDto.ViewType.GRID, "Leads"));
    metadataRegistry.registerView(new ViewMetadataDto("lead_form", "lead", ViewMetadataDto.ViewType.FORM, "Lead Details"));
    WorkflowMetadataDto leadWorkflow = new WorkflowMetadataDto("lead_workflow", "lead");
    leadWorkflow.setStates(Arrays.asList(
        new WorkflowStateDto("NEW", "New", true),
        new WorkflowStateDto("QUALIFIED", "Qualified", false),
        new WorkflowStateDto("CONVERTED", "Converted", false),
        new WorkflowStateDto("CLOSED", "Closed", false)
    ));
    leadWorkflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("qualify", "Qualify", "NEW", "QUALIFIED"),
        new WorkflowTransitionDto("convert", "Convert", "QUALIFIED", "CONVERTED"),
        new WorkflowTransitionDto("close", "Close", "NEW", "CLOSED"),
        new WorkflowTransitionDto("close", "Close", "CONVERTED", "CLOSED")
    ));
    metadataRegistry.registerWorkflow(leadWorkflow);
    metadataRegistry.registerAction(new ActionMetadataDto("act_lead_create", "Create Lead", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_lead_qualify", "Qualify", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_lead_convert", "Convert", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_lead_user", "lead", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_lead_manager", "lead", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto oppModel = new ModelMetadataDto("opportunity", "Opportunity", "Sales opportunity");
    oppModel.setWorkflowEnabled(true);
    oppModel.setFields(Arrays.asList(
        new FieldMetadataDto("opportunityNumber", "Opportunity Number", "string", true, true),
        new FieldMetadataDto("businessPartnerId", "Business Partner", "string", true, false),
        new FieldMetadataDto("stage", "Stage", "string", false, true),
        new FieldMetadataDto("probability", "Probability", "decimal", false, false),
        new FieldMetadataDto("expectedRevenue", "Expected Revenue", "decimal", false, false),
        new FieldMetadataDto("expectedCloseDate", "Expected Close", "date", false, false)
    ));
    metadataRegistry.registerModel(oppModel);
    metadataRegistry.registerView(new ViewMetadataDto("opportunity_list", "opportunity", ViewMetadataDto.ViewType.GRID, "Opportunities"));
    metadataRegistry.registerView(new ViewMetadataDto("opportunity_form", "opportunity", ViewMetadataDto.ViewType.FORM, "Opportunity Details"));
    WorkflowMetadataDto oppWorkflow = new WorkflowMetadataDto("opportunity_workflow", "opportunity");
    oppWorkflow.setStates(Arrays.asList(
        new WorkflowStateDto("OPEN", "Open", true),
        new WorkflowStateDto("PROPOSAL", "Proposal", false),
        new WorkflowStateDto("NEGOTIATION", "Negotiation", false),
        new WorkflowStateDto("WON", "Won", false),
        new WorkflowStateDto("LOST", "Lost", false)
    ));
    oppWorkflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("proposal", "Proposal", "OPEN", "PROPOSAL"),
        new WorkflowTransitionDto("negotiate", "Negotiate", "PROPOSAL", "NEGOTIATION"),
        new WorkflowTransitionDto("win", "Win", "NEGOTIATION", "WON"),
        new WorkflowTransitionDto("lose", "Lose", "OPEN", "LOST"),
        new WorkflowTransitionDto("lose", "Lose", "PROPOSAL", "LOST"),
        new WorkflowTransitionDto("lose", "Lose", "NEGOTIATION", "LOST")
    ));
    metadataRegistry.registerWorkflow(oppWorkflow);
    metadataRegistry.registerAction(new ActionMetadataDto("act_opp_create", "Create Opportunity", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_opp_advance", "Advance Stage", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_opp_win", "Win", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_opp_lose", "Lose", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_opp_user", "opportunity", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_opp_manager", "opportunity", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerProjects() {
    ModelMetadataDto projectModel = new ModelMetadataDto("project", "Project", "Project management");
    projectModel.setWorkflowEnabled(true);
    projectModel.setFields(Arrays.asList(
        new FieldMetadataDto("projectCode", "Project Code", "string", true, true),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("customerId", "Customer", "string", false, false),
        new FieldMetadataDto("managerId", "Manager", "string", false, false),
        new FieldMetadataDto("startDate", "Start Date", "date", false, false),
        new FieldMetadataDto("endDate", "End Date", "date", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("budget", "Budget", "decimal", false, false)
    ));
    metadataRegistry.registerModel(projectModel);
    metadataRegistry.registerView(new ViewMetadataDto("project_list", "project", ViewMetadataDto.ViewType.GRID, "Projects"));
    metadataRegistry.registerView(new ViewMetadataDto("project_form", "project", ViewMetadataDto.ViewType.FORM, "Project Details"));
    WorkflowMetadataDto projectWorkflow = new WorkflowMetadataDto("project_workflow", "project");
    projectWorkflow.setStates(Arrays.asList(
        new WorkflowStateDto("OPEN", "Open", true),
        new WorkflowStateDto("IN_PROGRESS", "In Progress", false),
        new WorkflowStateDto("COMPLETED", "Completed", false),
        new WorkflowStateDto("CLOSED", "Closed", false)
    ));
    projectWorkflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("start", "Start", "OPEN", "IN_PROGRESS"),
        new WorkflowTransitionDto("complete", "Complete", "IN_PROGRESS", "COMPLETED"),
        new WorkflowTransitionDto("close", "Close", "COMPLETED", "CLOSED")
    ));
    metadataRegistry.registerWorkflow(projectWorkflow);
    metadataRegistry.registerAction(new ActionMetadataDto("act_project_create", "Create Project", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_project_complete", "Complete", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_project_user", "project", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_project_manager", "project", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto taskModel = new ModelMetadataDto("project_task", "Task", "Project task");
    taskModel.setWorkflowEnabled(true);
    taskModel.setFields(Arrays.asList(
        new FieldMetadataDto("taskNumber", "Task Number", "string", true, true),
        new FieldMetadataDto("title", "Title", "string", true, false),
        new FieldMetadataDto("priority", "Priority", "string", false, false),
        new FieldMetadataDto("assignedTo", "Assigned To", "string", false, false),
        new FieldMetadataDto("projectId", "Project", "string", true, false),
        new FieldMetadataDto("plannedHours", "Planned Hours", "decimal", false, false),
        new FieldMetadataDto("actualHours", "Actual Hours", "decimal", false, true),
        new FieldMetadataDto("status", "Status", "string", false, true)
    ));
    metadataRegistry.registerModel(taskModel);
    metadataRegistry.registerView(new ViewMetadataDto("task_list", "project_task", ViewMetadataDto.ViewType.GRID, "Tasks"));
    metadataRegistry.registerView(new ViewMetadataDto("task_form", "project_task", ViewMetadataDto.ViewType.FORM, "Task Details"));
    WorkflowMetadataDto taskWorkflow = new WorkflowMetadataDto("task_workflow", "project_task");
    taskWorkflow.setStates(Arrays.asList(
        new WorkflowStateDto("OPEN", "Open", true),
        new WorkflowStateDto("ASSIGNED", "Assigned", false),
        new WorkflowStateDto("IN_PROGRESS", "In Progress", false),
        new WorkflowStateDto("COMPLETED", "Completed", false),
        new WorkflowStateDto("CLOSED", "Closed", false)
    ));
    taskWorkflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("assign", "Assign", "OPEN", "ASSIGNED"),
        new WorkflowTransitionDto("start", "Start", "ASSIGNED", "IN_PROGRESS"),
        new WorkflowTransitionDto("complete", "Complete", "IN_PROGRESS", "COMPLETED"),
        new WorkflowTransitionDto("close", "Close", "COMPLETED", "CLOSED")
    ));
    metadataRegistry.registerWorkflow(taskWorkflow);
    metadataRegistry.registerAction(new ActionMetadataDto("act_task_create", "Create Task", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_task_assign", "Assign", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_task_complete", "Complete", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_task_user", "project_task", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_task_manager", "project_task", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerService() {
    ModelMetadataDto model = new ModelMetadataDto("service_request", "Service Request", "Customer support ticket");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("ticketNumber", "Ticket Number", "string", true, true),
        new FieldMetadataDto("customerId", "Customer", "string", true, false),
        new FieldMetadataDto("priority", "Priority", "string", false, false),
        new FieldMetadataDto("category", "Category", "string", false, false),
        new FieldMetadataDto("assignedEngineerId", "Engineer", "string", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("description", "Description", "text", false, false)
    ));
    metadataRegistry.registerModel(model);
    metadataRegistry.registerView(new ViewMetadataDto("service_request_list", "service_request", ViewMetadataDto.ViewType.GRID, "Service Requests"));
    metadataRegistry.registerView(new ViewMetadataDto("service_request_form", "service_request", ViewMetadataDto.ViewType.FORM, "Service Request Details"));
    WorkflowMetadataDto workflow = new WorkflowMetadataDto("service_request_workflow", "service_request");
    workflow.setStates(Arrays.asList(
        new WorkflowStateDto("NEW", "New", true),
        new WorkflowStateDto("ASSIGNED", "Assigned", false),
        new WorkflowStateDto("IN_PROGRESS", "In Progress", false),
        new WorkflowStateDto("RESOLVED", "Resolved", false),
        new WorkflowStateDto("CLOSED", "Closed", false)
    ));
    workflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("assign", "Assign", "NEW", "ASSIGNED"),
        new WorkflowTransitionDto("start", "Start", "ASSIGNED", "IN_PROGRESS"),
        new WorkflowTransitionDto("resolve", "Resolve", "IN_PROGRESS", "RESOLVED"),
        new WorkflowTransitionDto("close", "Close", "RESOLVED", "CLOSED")
    ));
    metadataRegistry.registerWorkflow(workflow);
    metadataRegistry.registerAction(new ActionMetadataDto("act_sr_create", "Create Ticket", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sr_assign", "Assign", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_sr_resolve", "Resolve", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_sr_user", "service_request", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_sr_manager", "service_request", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerHR() {
    ModelMetadataDto deptModel = new ModelMetadataDto("department", "Department", "Organizational unit");
    deptModel.setFields(Arrays.asList(
        new FieldMetadataDto("departmentCode", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("parentDepartmentId", "Parent Department", "string", false, false),
        new FieldMetadataDto("managerId", "Manager", "string", false, false)
    ));
    metadataRegistry.registerModel(deptModel);
    metadataRegistry.registerView(new ViewMetadataDto("department_list", "department", ViewMetadataDto.ViewType.GRID, "Departments"));
    metadataRegistry.registerView(new ViewMetadataDto("department_form", "department", ViewMetadataDto.ViewType.FORM, "Department Details"));
    metadataRegistry.registerAction(new ActionMetadataDto("act_dept_create", "Create Department", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_dept_user", "department", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_dept_manager", "department", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto empModel = new ModelMetadataDto("employee", "Employee", "Employee record");
    empModel.setFields(Arrays.asList(
        new FieldMetadataDto("employeeCode", "Employee Code", "string", true, false),
        new FieldMetadataDto("firstName", "First Name", "string", true, false),
        new FieldMetadataDto("lastName", "Last Name", "string", true, false),
        new FieldMetadataDto("email", "Email", "string", false, false),
        new FieldMetadataDto("phone", "Phone", "string", false, false),
        new FieldMetadataDto("departmentId", "Department", "string", false, false),
        new FieldMetadataDto("designation", "Designation", "string", false, false),
        new FieldMetadataDto("managerId", "Manager", "string", false, false),
        new FieldMetadataDto("joiningDate", "Joining Date", "date", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true)
    ));
    metadataRegistry.registerModel(empModel);
    metadataRegistry.registerView(new ViewMetadataDto("employee_list", "employee", ViewMetadataDto.ViewType.GRID, "Employees"));
    metadataRegistry.registerView(new ViewMetadataDto("employee_form", "employee", ViewMetadataDto.ViewType.FORM, "Employee Details"));
    metadataRegistry.registerAction(new ActionMetadataDto("act_emp_create", "Create Employee", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_emp_user", "employee", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_emp_manager", "employee", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerAssets() {
    ModelMetadataDto model = new ModelMetadataDto("asset", "Asset", "Company asset tracking");
    model.setWorkflowEnabled(true);
    model.setFields(Arrays.asList(
        new FieldMetadataDto("assetCode", "Asset Code", "string", true, false),
        new FieldMetadataDto("assetName", "Asset Name", "string", true, false),
        new FieldMetadataDto("assetType", "Type", "string", false, false),
        new FieldMetadataDto("purchaseDate", "Purchase Date", "date", false, false),
        new FieldMetadataDto("purchaseCost", "Purchase Cost", "decimal", false, false),
        new FieldMetadataDto("currentValue", "Current Value", "decimal", false, true),
        new FieldMetadataDto("assignedTo", "Assigned To", "string", false, false),
        new FieldMetadataDto("location", "Location", "string", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true)
    ));
    metadataRegistry.registerModel(model);
    metadataRegistry.registerView(new ViewMetadataDto("asset_list", "asset", ViewMetadataDto.ViewType.GRID, "Assets"));
    metadataRegistry.registerView(new ViewMetadataDto("asset_form", "asset", ViewMetadataDto.ViewType.FORM, "Asset Details"));
    WorkflowMetadataDto workflow = new WorkflowMetadataDto("asset_workflow", "asset");
    workflow.setStates(Arrays.asList(
        new WorkflowStateDto("DRAFT", "Draft", true),
        new WorkflowStateDto("ACTIVE", "Active", false),
        new WorkflowStateDto("MAINTENANCE", "Maintenance", false),
        new WorkflowStateDto("DISPOSED", "Disposed", false)
    ));
    workflow.setTransitions(Arrays.asList(
        new WorkflowTransitionDto("activate", "Activate", "DRAFT", "ACTIVE"),
        new WorkflowTransitionDto("maintain", "Maintenance", "ACTIVE", "MAINTENANCE"),
        new WorkflowTransitionDto("activate", "Activate", "MAINTENANCE", "ACTIVE"),
        new WorkflowTransitionDto("dispose", "Dispose", "ACTIVE", "DISPOSED"),
        new WorkflowTransitionDto("dispose", "Dispose", "MAINTENANCE", "DISPOSED")
    ));
    metadataRegistry.registerWorkflow(workflow);
    metadataRegistry.registerAction(new ActionMetadataDto("act_asset_create", "Create Asset", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerAction(new ActionMetadataDto("act_asset_activate", "Activate", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_asset_maintain", "Maintenance", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerAction(new ActionMetadataDto("act_asset_dispose", "Dispose", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_asset_user", "asset", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_asset_manager", "asset", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerAnalytics() {
    ModelMetadataDto dashboard = new ModelMetadataDto("dashboard", "Dashboard", "Analytics dashboards");
    dashboard.setWorkflowEnabled(false);
    dashboard.setFields(Arrays.asList(
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("description", "Description", "text", false, false),
        new FieldMetadataDto("layout", "Layout", "json", false, false),
        new FieldMetadataDto("isDefault", "Is Default", "boolean", false, false),
        new FieldMetadataDto("roles", "Roles", "json", false, false)
    ));
    metadataRegistry.registerModel(dashboard);
    metadataRegistry.registerView(new ViewMetadataDto("dashboard_list", "dashboard", ViewMetadataDto.ViewType.GRID, "Dashboards"));
    metadataRegistry.registerView(new ViewMetadataDto("dashboard_form", "dashboard", ViewMetadataDto.ViewType.FORM, "Dashboard Details"));
    metadataRegistry.registerAction(new ActionMetadataDto("act_dashboard_create", "Create Dashboard", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_dashboard_user", "dashboard", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_dashboard_admin", "dashboard", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto report = new ModelMetadataDto("report_definition", "Report Definition", "Report definitions");
    report.setWorkflowEnabled(false);
    report.setFields(Arrays.asList(
        new FieldMetadataDto("reportCode", "Report Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("description", "Description", "text", false, false),
        new FieldMetadataDto("reportType", "Report Type", "string", false, false),
        new FieldMetadataDto("modelCode", "Model Code", "string", false, false),
        new FieldMetadataDto("outputFormat", "Output Format", "string", false, false),
        new FieldMetadataDto("isSystem", "Is System", "boolean", false, false)
    ));
    metadataRegistry.registerModel(report);
    metadataRegistry.registerView(new ViewMetadataDto("report_list", "report_definition", ViewMetadataDto.ViewType.GRID, "Reports"));
    metadataRegistry.registerView(new ViewMetadataDto("report_form", "report_definition", ViewMetadataDto.ViewType.FORM, "Report Details"));
    metadataRegistry.registerAction(new ActionMetadataDto("act_report_create", "Create Report", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_report_user", "report_definition", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_report_admin", "report_definition", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto kpi = new ModelMetadataDto("kpi_definition", "KPI Definition", "KPI definitions");
    kpi.setWorkflowEnabled(false);
    kpi.setFields(Arrays.asList(
        new FieldMetadataDto("kpiCode", "KPI Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("category", "Category", "string", false, false),
        new FieldMetadataDto("expression", "Expression", "text", false, false),
        new FieldMetadataDto("unit", "Unit", "string", false, false),
        new FieldMetadataDto("refreshInterval", "Refresh Interval", "integer", false, false)
    ));
    metadataRegistry.registerModel(kpi);
    metadataRegistry.registerView(new ViewMetadataDto("kpi_list", "kpi_definition", ViewMetadataDto.ViewType.GRID, "KPIs"));
    metadataRegistry.registerView(new ViewMetadataDto("kpi_form", "kpi_definition", ViewMetadataDto.ViewType.FORM, "KPI Details"));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_kpi_user", "kpi_definition", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_kpi_admin", "kpi_definition", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto schedule = new ModelMetadataDto("scheduled_report", "Scheduled Report", "Report schedules");
    schedule.setWorkflowEnabled(false);
    schedule.setFields(Arrays.asList(
        new FieldMetadataDto("scheduleCode", "Schedule Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("cronExpression", "Cron Expression", "string", false, false),
        new FieldMetadataDto("outputFormat", "Output Format", "string", false, false),
        new FieldMetadataDto("status", "Status", "string", false, false),
        new FieldMetadataDto("recipientEmails", "Recipient Emails", "json", false, false)
    ));
    metadataRegistry.registerModel(schedule);
    metadataRegistry.registerView(new ViewMetadataDto("schedule_list", "scheduled_report", ViewMetadataDto.ViewType.GRID, "Schedules"));
    metadataRegistry.registerView(new ViewMetadataDto("schedule_form", "scheduled_report", ViewMetadataDto.ViewType.FORM, "Schedule Details"));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_schedule_user", "scheduled_report", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_schedule_admin", "scheduled_report", PermissionMetadataDto.PermissionType.ADMIN));
  }

  private void registerPlatform() {
    ModelMetadataDto notification = new ModelMetadataDto("notification", "Notification", "Platform notifications");
    notification.setWorkflowEnabled(false);
    notification.setFields(Arrays.asList(
        new FieldMetadataDto("title", "Title", "string", true, false),
        new FieldMetadataDto("message", "Message", "text", false, false),
        new FieldMetadataDto("type", "Type", "string", false, true),
        new FieldMetadataDto("priority", "Priority", "string", false, false),
        new FieldMetadataDto("recipient", "Recipient", "string", true, false),
        new FieldMetadataDto("module", "Module", "string", false, false),
        new FieldMetadataDto("recordId", "Record ID", "string", false, false),
        new FieldMetadataDto("status", "Status", "string", false, true),
        new FieldMetadataDto("readAt", "Read At", "datetime", false, false)
    ));
    metadataRegistry.registerModel(notification);
    metadataRegistry.registerView(new ViewMetadataDto("notification_list", "notification", ViewMetadataDto.ViewType.GRID, "Notifications"));
    metadataRegistry.registerView(new ViewMetadataDto("notification_form", "notification", ViewMetadataDto.ViewType.FORM, "Notification Details"));
    metadataRegistry.registerAction(new ActionMetadataDto("act_notification_send", "Send Notification", ActionMetadataDto.ActionType.CUSTOM));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_notification_user", "notification", PermissionMetadataDto.PermissionType.READ));

    ModelMetadataDto emailTemplate = new ModelMetadataDto("email_template", "Email Template", "Email templates");
    emailTemplate.setWorkflowEnabled(false);
    emailTemplate.setFields(Arrays.asList(
        new FieldMetadataDto("code", "Code", "string", true, false),
        new FieldMetadataDto("name", "Name", "string", true, false),
        new FieldMetadataDto("subject", "Subject", "string", true, false),
        new FieldMetadataDto("variables", "Variables", "text", false, false),
        new FieldMetadataDto("locale", "Locale", "string", false, false)
    ));
    metadataRegistry.registerModel(emailTemplate);
    metadataRegistry.registerView(new ViewMetadataDto("email_template_list", "email_template", ViewMetadataDto.ViewType.GRID, "Email Templates"));
    metadataRegistry.registerView(new ViewMetadataDto("email_template_form", "email_template", ViewMetadataDto.ViewType.FORM, "Template Details"));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_template_user", "email_template", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_template_admin", "email_template", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto document = new ModelMetadataDto("document", "Document", "Document management");
    document.setWorkflowEnabled(false);
    document.setFields(Arrays.asList(
        new FieldMetadataDto("fileName", "File Name", "string", true, false),
        new FieldMetadataDto("mimeType", "MIME Type", "string", false, false),
        new FieldMetadataDto("fileSize", "File Size", "integer", false, false),
        new FieldMetadataDto("owner", "Owner", "string", false, false),
        new FieldMetadataDto("module", "Module", "string", false, false),
        new FieldMetadataDto("recordId", "Record ID", "string", false, false),
        new FieldMetadataDto("version", "Version", "integer", false, true),
        new FieldMetadataDto("category", "Category", "string", false, false),
        new FieldMetadataDto("folder", "Folder", "string", false, false)
    ));
    metadataRegistry.registerModel(document);
    metadataRegistry.registerView(new ViewMetadataDto("document_list", "document", ViewMetadataDto.ViewType.GRID, "Documents"));
    metadataRegistry.registerView(new ViewMetadataDto("document_form", "document", ViewMetadataDto.ViewType.FORM, "Document Details"));
    metadataRegistry.registerAction(new ActionMetadataDto("act_document_upload", "Upload Document", ActionMetadataDto.ActionType.CREATE));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_document_user", "document", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_document_manager", "document", PermissionMetadataDto.PermissionType.UPDATE));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_document_admin", "document", PermissionMetadataDto.PermissionType.ADMIN));

    ModelMetadataDto comment = new ModelMetadataDto("comment", "Comment", "Collaboration comments");
    comment.setWorkflowEnabled(false);
    comment.setFields(Arrays.asList(
        new FieldMetadataDto("module", "Module", "string", true, false),
        new FieldMetadataDto("recordId", "Record ID", "string", true, false),
        new FieldMetadataDto("author", "Author", "string", true, false),
        new FieldMetadataDto("body", "Body", "text", true, false),
        new FieldMetadataDto("parentId", "Parent ID", "string", false, false),
        new FieldMetadataDto("mentions", "Mentions", "text", false, false)
    ));
    metadataRegistry.registerModel(comment);
    metadataRegistry.registerView(new ViewMetadataDto("comment_list", "comment", ViewMetadataDto.ViewType.GRID, "Comments"));
    metadataRegistry.registerView(new ViewMetadataDto("comment_form", "comment", ViewMetadataDto.ViewType.FORM, "Comment Details"));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_comment_user", "comment", PermissionMetadataDto.PermissionType.READ));
    metadataRegistry.registerPermission(new PermissionMetadataDto("perm_comment_admin", "comment", PermissionMetadataDto.PermissionType.ADMIN));
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
