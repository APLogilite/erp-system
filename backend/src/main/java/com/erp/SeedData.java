package com.erp;

import com.erp.modules.businesspartner.entity.BusinessPartner;
import com.erp.modules.businesspartner.repository.BusinessPartnerRepository;
import com.erp.modules.product.entity.Product;
import com.erp.modules.product.entity.ProductCategory;
import com.erp.modules.product.repository.ProductCategoryRepository;
import com.erp.modules.product.repository.ProductRepository;
import com.erp.modules.warehouse.entity.Warehouse;
import com.erp.modules.warehouse.repository.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedData.class);

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final BusinessPartnerRepository businessPartnerRepository;
    private final WarehouseRepository warehouseRepository;

    public SeedData(ProductRepository productRepository,
                    ProductCategoryRepository productCategoryRepository,
                    BusinessPartnerRepository businessPartnerRepository,
                    WarehouseRepository warehouseRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.businessPartnerRepository = businessPartnerRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Seed data already exists, skipping.");
            return;
        }
        log.info("Creating seed data...");

        ProductCategory electronics = new ProductCategory();
        electronics.setCode("ELECTRONICS");
        electronics.setName("Electronics");
        productCategoryRepository.save(electronics);

        ProductCategory services = new ProductCategory();
        services.setCode("SERVICES");
        services.setName("Services");
        productCategoryRepository.save(services);

        Product laptop = new Product();
        laptop.setCode("PROD-001");
        laptop.setName("Laptop");
        laptop.setDescription("High-performance laptop");
        laptop.setSku("LAP-001");
        laptop.setBarcode("BAR-LAP-001");
        laptop.setUom("UNIT");
        laptop.setProductType("ITEM");
        laptop.setIsStocked(true);
        laptop.setIsSold(true);
        laptop.setIsPurchased(true);
        laptop.setCategoryId(electronics.getId());
        productRepository.save(laptop);

        Product keyboard = new Product();
        keyboard.setCode("PROD-002");
        keyboard.setName("Keyboard");
        keyboard.setDescription("Mechanical keyboard");
        keyboard.setSku("KBD-001");
        keyboard.setBarcode("BAR-KBD-001");
        keyboard.setUom("UNIT");
        keyboard.setProductType("ITEM");
        keyboard.setIsStocked(true);
        keyboard.setIsSold(true);
        keyboard.setIsPurchased(true);
        keyboard.setCategoryId(electronics.getId());
        productRepository.save(keyboard);

        Product consulting = new Product();
        consulting.setCode("PROD-003");
        consulting.setName("Consulting Service");
        consulting.setDescription("IT consulting service");
        consulting.setSku("CONS-001");
        consulting.setUom("HOUR");
        consulting.setProductType("SERVICE");
        consulting.setIsStocked(false);
        consulting.setIsSold(true);
        consulting.setIsPurchased(false);
        consulting.setCategoryId(services.getId());
        productRepository.save(consulting);

        BusinessPartner abcCustomer = new BusinessPartner();
        abcCustomer.setCode("BP-001");
        abcCustomer.setName("ABC Customer");
        abcCustomer.setPartnerType("CUSTOMER");
        abcCustomer.setEmail("contact@abc-customer.com");
        abcCustomer.setPhone("+1-555-0100");
        abcCustomer.setIsCustomer(true);
        abcCustomer.setIsVendor(false);
        abcCustomer.setIsEmployee(false);
        businessPartnerRepository.save(abcCustomer);

        BusinessPartner xyzSupplier = new BusinessPartner();
        xyzSupplier.setCode("BP-002");
        xyzSupplier.setName("XYZ Supplier");
        xyzSupplier.setPartnerType("VENDOR");
        xyzSupplier.setEmail("sales@xyz-supplier.com");
        xyzSupplier.setPhone("+1-555-0200");
        xyzSupplier.setIsCustomer(false);
        xyzSupplier.setIsVendor(true);
        xyzSupplier.setIsEmployee(false);
        businessPartnerRepository.save(xyzSupplier);

        Warehouse mainWarehouse = new Warehouse();
        mainWarehouse.setCode("WH-001");
        mainWarehouse.setName("Main Warehouse");
        mainWarehouse.setDescription("Primary storage facility");
        warehouseRepository.save(mainWarehouse);

        Warehouse finishedGoods = new Warehouse();
        finishedGoods.setCode("WH-002");
        finishedGoods.setName("Finished Goods");
        finishedGoods.setDescription("Finished goods storage");
        warehouseRepository.save(finishedGoods);

        log.info("Seed data created successfully.");
    }
}
