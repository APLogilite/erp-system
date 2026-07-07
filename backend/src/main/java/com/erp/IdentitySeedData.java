package com.erp;

import com.erp.platform.identity.entity.*;
import com.erp.platform.identity.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class IdentitySeedData implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IdentitySeedData.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private final TenantRepository tenantRepository;
    private final OrganizationRepository organizationRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserOrganizationRepository userOrganizationRepository;
    private final UserCompanyRepository userCompanyRepository;
    private final UserBranchRepository userBranchRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    public IdentitySeedData(TenantRepository tenantRepository,
                            OrganizationRepository organizationRepository,
                            CompanyRepository companyRepository,
                            BranchRepository branchRepository,
                            DepartmentRepository departmentRepository,
                            UserAccountRepository userAccountRepository,
                            RoleRepository roleRepository,
                            PermissionRepository permissionRepository,
                            UserRoleRepository userRoleRepository,
                            RolePermissionRepository rolePermissionRepository,
                            UserOrganizationRepository userOrganizationRepository,
                            UserCompanyRepository userCompanyRepository,
                            UserBranchRepository userBranchRepository,
                            UserPreferenceRepository userPreferenceRepository) {
        this.tenantRepository = tenantRepository;
        this.organizationRepository = organizationRepository;
        this.companyRepository = companyRepository;
        this.branchRepository = branchRepository;
        this.departmentRepository = departmentRepository;
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userOrganizationRepository = userOrganizationRepository;
        this.userCompanyRepository = userCompanyRepository;
        this.userBranchRepository = userBranchRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @Override
    public void run(String... args) {
        if (userAccountRepository.count() > 0) {
            log.info("Identity seed data already exists, skipping.");
            return;
        }
        log.info("Creating identity seed data...");

        // ── Tenants ──
        Tenant acme = new Tenant();
        acme.setCode("ACME");
        acme.setName("Acme Corporation");
        acme.setDomain("acme.example.com");
        acme.setDefaultLanguage("en");
        acme.setDefaultTimezone("America/New_York");
        acme.setDefaultCurrency("USD");
        tenantRepository.save(acme);

        Tenant globex = new Tenant();
        globex.setCode("GLOBEX");
        globex.setName("Globex Industries");
        globex.setDomain("globex.example.com");
        globex.setDefaultLanguage("en");
        globex.setDefaultTimezone("Europe/London");
        globex.setDefaultCurrency("EUR");
        tenantRepository.save(globex);

        // ── Organizations ──
        Organization acmeGlobal = new Organization();
        acmeGlobal.setCode("ACME-GLOBAL");
        acmeGlobal.setName("Acme Global");
        acmeGlobal.setDescription("Acme Corporation worldwide operations");
        acmeGlobal.setTenant(acme);
        acmeGlobal.setLevel(0);
        acmeGlobal.setPath("/ACME-GLOBAL");
        organizationRepository.save(acmeGlobal);

        Organization acmeApac = new Organization();
        acmeApac.setCode("ACME-APAC");
        acmeApac.setName("Acme APAC");
        acmeApac.setDescription("Acme Corporation Asia-Pacific operations");
        acmeApac.setTenant(acme);
        acmeApac.setLevel(0);
        acmeApac.setPath("/ACME-APAC");
        organizationRepository.save(acmeApac);

        Organization globexCorp = new Organization();
        globexCorp.setCode("GLOBEX-CORP");
        globexCorp.setName("Globex Corp");
        globexCorp.setDescription("Globex Industries corporate entity");
        globexCorp.setTenant(globex);
        globexCorp.setLevel(0);
        globexCorp.setPath("/GLOBEX-CORP");
        organizationRepository.save(globexCorp);

        // ── Companies ──
        Company acmeInc = new Company();
        acmeInc.setCode("ACME-INC");
        acmeInc.setName("Acme Inc.");
        acmeInc.setTaxId("TAX-US-001");
        acmeInc.setRegistrationNumber("REG-US-001");
        acmeInc.setAddress("123 Main Street, New York, NY 10001");
        acmeInc.setPhone("+1-555-0100");
        acmeInc.setEmail("info@acme-inc.com");
        acmeInc.setCurrency("USD");
        acmeInc.setOrganization(acmeGlobal);
        acmeInc.setTenant(acme);
        companyRepository.save(acmeInc);

        Company acmeEu = new Company();
        acmeEu.setCode("ACME-EU");
        acmeEu.setName("Acme Europe GmbH");
        acmeEu.setTaxId("TAX-EU-001");
        acmeEu.setRegistrationNumber("REG-EU-001");
        acmeEu.setAddress("50 Berliner Strasse, Berlin, Germany");
        acmeEu.setPhone("+49-30-555-0100");
        acmeEu.setEmail("info@acme-eu.de");
        acmeEu.setCurrency("EUR");
        acmeEu.setOrganization(acmeGlobal);
        acmeEu.setTenant(acme);
        companyRepository.save(acmeEu);

        Company apacInc = new Company();
        apacInc.setCode("APAC-INC");
        apacInc.setName("Acme APAC Inc.");
        apacInc.setTaxId("TAX-IN-001");
        apacInc.setRegistrationNumber("REG-IN-001");
        apacInc.setAddress("42 Tech Park, Mumbai, India");
        apacInc.setPhone("+91-22-555-0100");
        apacInc.setEmail("info@apac-inc.in");
        apacInc.setCurrency("INR");
        apacInc.setOrganization(acmeApac);
        apacInc.setTenant(acme);
        companyRepository.save(apacInc);

        Company globexLtd = new Company();
        globexLtd.setCode("GLOBEX-LTD");
        globexLtd.setName("Globex Ltd.");
        globexLtd.setTaxId("TAX-UK-001");
        globexLtd.setRegistrationNumber("REG-UK-001");
        globexLtd.setAddress("1 London Bridge, London, UK");
        globexLtd.setPhone("+44-20-555-0100");
        globexLtd.setEmail("info@globex-ltd.co.uk");
        globexLtd.setCurrency("GBP");
        globexLtd.setOrganization(globexCorp);
        globexLtd.setTenant(globex);
        companyRepository.save(globexLtd);

        // ── Branches ──
        Branch ho = new Branch();
        ho.setCode("HO");
        ho.setName("Head Office");
        ho.setAddress("123 Main Street, New York, NY 10001");
        ho.setPhone("+1-555-0101");
        ho.setEmail("ho@acme-inc.com");
        ho.setIsHeadOffice(true);
        ho.setCompany(acmeInc);
        ho.setTenant(acme);
        branchRepository.save(ho);

        Branch nb = new Branch();
        nb.setCode("NB");
        nb.setName("North Branch");
        nb.setAddress("456 Industrial Ave, Boston, MA 02101");
        nb.setPhone("+1-555-0102");
        nb.setEmail("north@acme-inc.com");
        nb.setIsHeadOffice(false);
        nb.setCompany(acmeInc);
        nb.setTenant(acme);
        branchRepository.save(nb);

        Branch euHq = new Branch();
        euHq.setCode("EU-HQ");
        euHq.setName("Europe HQ");
        euHq.setAddress("50 Berliner Strasse, Berlin, Germany");
        euHq.setPhone("+49-30-555-0101");
        euHq.setEmail("hq@acme-eu.de");
        euHq.setIsHeadOffice(true);
        euHq.setCompany(acmeEu);
        euHq.setTenant(acme);
        branchRepository.save(euHq);

        Branch apacHq = new Branch();
        apacHq.setCode("APAC-HQ");
        apacHq.setName("APAC Headquarters");
        apacHq.setAddress("42 Tech Park, Mumbai, India");
        apacHq.setPhone("+91-22-555-0101");
        apacHq.setEmail("hq@apac-inc.in");
        apacHq.setIsHeadOffice(true);
        apacHq.setCompany(apacInc);
        apacHq.setTenant(acme);
        branchRepository.save(apacHq);

        Branch gxHq = new Branch();
        gxHq.setCode("GX-HQ");
        gxHq.setName("Globex HQ");
        gxHq.setAddress("1 London Bridge, London, UK");
        gxHq.setPhone("+44-20-555-0101");
        gxHq.setEmail("hq@globex-ltd.co.uk");
        gxHq.setIsHeadOffice(true);
        gxHq.setCompany(globexLtd);
        gxHq.setTenant(globex);
        branchRepository.save(gxHq);

        // ── Departments ──
        Department eng = new Department();
        eng.setCode("ENG");
        eng.setName("Engineering");
        eng.setDescription("Software engineering department");
        eng.setCostCenter("CC-ENG");
        eng.setBranch(ho);
        eng.setTenant(acme);
        eng.setLevel(0);
        departmentRepository.save(eng);

        Department sales = new Department();
        sales.setCode("SALES");
        sales.setName("Sales");
        sales.setDescription("Sales and business development");
        sales.setCostCenter("CC-SALES");
        sales.setBranch(ho);
        sales.setTenant(acme);
        sales.setLevel(0);
        departmentRepository.save(sales);

        Department finance = new Department();
        finance.setCode("FIN");
        finance.setName("Finance");
        finance.setDescription("Finance and accounting");
        finance.setCostCenter("CC-FIN");
        finance.setBranch(ho);
        finance.setTenant(acme);
        finance.setLevel(0);
        departmentRepository.save(finance);

        Department legal = new Department();
        legal.setCode("LEGAL");
        legal.setName("Legal");
        legal.setDescription("Legal and compliance");
        legal.setCostCenter("CC-LEGAL");
        legal.setBranch(ho);
        legal.setTenant(acme);
        legal.setLevel(0);
        departmentRepository.save(legal);

        Department it = new Department();
        it.setCode("IT");
        it.setName("Information Technology");
        it.setDescription("IT infrastructure and support");
        it.setCostCenter("CC-IT");
        it.setBranch(ho);
        it.setTenant(acme);
        it.setLevel(0);
        departmentRepository.save(it);

        Department hr = new Department();
        hr.setCode("HR");
        hr.setName("Human Resources");
        hr.setDescription("Human resources and recruitment");
        hr.setCostCenter("CC-HR");
        hr.setBranch(ho);
        hr.setTenant(acme);
        hr.setLevel(0);
        departmentRepository.save(hr);

        Department support = new Department();
        support.setCode("SUPPORT");
        support.setName("Customer Support");
        support.setDescription("Customer support and success");
        support.setCostCenter("CC-SUPPORT");
        support.setBranch(nb);
        support.setTenant(acme);
        support.setLevel(0);
        departmentRepository.save(support);

        Department logistics = new Department();
        logistics.setCode("LOGISTICS");
        logistics.setName("Logistics");
        logistics.setDescription("Logistics and supply chain");
        logistics.setCostCenter("CC-LOGISTICS");
        logistics.setBranch(nb);
        logistics.setTenant(acme);
        logistics.setLevel(0);
        departmentRepository.save(logistics);

        Department service = new Department();
        service.setCode("SERVICE");
        service.setName("Field Service");
        service.setDescription("Field service and maintenance");
        service.setCostCenter("CC-SERVICE");
        service.setBranch(nb);
        service.setTenant(acme);
        service.setLevel(0);
        departmentRepository.save(service);

        Department euDev = new Department();
        euDev.setCode("EU-DEV");
        euDev.setName("Development");
        euDev.setDescription("European development team");
        euDev.setCostCenter("CC-EU-DEV");
        euDev.setBranch(euHq);
        euDev.setTenant(acme);
        euDev.setLevel(0);
        departmentRepository.save(euDev);

        Department euSales = new Department();
        euSales.setCode("EU-SALES");
        euSales.setName("European Sales");
        euSales.setDescription("European sales team");
        euSales.setCostCenter("CC-EU-SALES");
        euSales.setBranch(euHq);
        euSales.setTenant(acme);
        euSales.setLevel(0);
        departmentRepository.save(euSales);

        Department euMarketing = new Department();
        euMarketing.setCode("EU-MARKETING");
        euMarketing.setName("European Marketing");
        euMarketing.setDescription("European marketing team");
        euMarketing.setCostCenter("CC-EU-MKT");
        euMarketing.setBranch(euHq);
        euMarketing.setTenant(acme);
        euMarketing.setLevel(0);
        departmentRepository.save(euMarketing);

        Department apacEng = new Department();
        apacEng.setCode("APAC-ENG");
        apacEng.setName("APAC Engineering");
        apacEng.setDescription("APAC engineering team");
        apacEng.setCostCenter("CC-APAC-ENG");
        apacEng.setBranch(apacHq);
        apacEng.setTenant(acme);
        apacEng.setLevel(0);
        departmentRepository.save(apacEng);

        Department apacHr = new Department();
        apacHr.setCode("APAC-HR");
        apacHr.setName("APAC Human Resources");
        apacHr.setDescription("APAC HR team");
        apacHr.setCostCenter("CC-APAC-HR");
        apacHr.setBranch(apacHq);
        apacHr.setTenant(acme);
        apacHr.setLevel(0);
        departmentRepository.save(apacHr);

        Department gxOps = new Department();
        gxOps.setCode("GX-OPS");
        gxOps.setName("Operations");
        gxOps.setDescription("Globex operations department");
        gxOps.setCostCenter("CC-GX-OPS");
        gxOps.setBranch(gxHq);
        gxOps.setTenant(globex);
        gxOps.setLevel(0);
        departmentRepository.save(gxOps);

        Department gxFinance = new Department();
        gxFinance.setCode("GX-FINANCE");
        gxFinance.setName("Finance");
        gxFinance.setDescription("Globex finance department");
        gxFinance.setCostCenter("CC-GX-FIN");
        gxFinance.setBranch(gxHq);
        gxFinance.setTenant(globex);
        gxFinance.setLevel(0);
        departmentRepository.save(gxFinance);

        Department gxHr = new Department();
        gxHr.setCode("GX-HR");
        gxHr.setName("Human Resources");
        gxHr.setDescription("Globex HR department");
        gxHr.setCostCenter("CC-GX-HR");
        gxHr.setBranch(gxHq);
        gxHr.setTenant(globex);
        gxHr.setLevel(0);
        departmentRepository.save(gxHr);

        Department gxSales = new Department();
        gxSales.setCode("GX-SALES");
        gxSales.setName("Sales");
        gxSales.setDescription("Globex sales department");
        gxSales.setCostCenter("CC-GX-SALES");
        gxSales.setBranch(gxHq);
        gxSales.setTenant(globex);
        gxSales.setLevel(0);
        departmentRepository.save(gxSales);

        // ── Permissions ──
        Permission[][] permGroups = {
            { makePerm("user:read", "View Users", "user", "*", "read", "identity"),
              makePerm("user:create", "Create Users", "user", "*", "create", "identity"),
              makePerm("user:update", "Update Users", "user", "*", "update", "identity"),
              makePerm("user:delete", "Delete Users", "user", "*", "delete", "identity") },
            { makePerm("role:read", "View Roles", "role", "*", "read", "identity"),
              makePerm("role:create", "Create Roles", "role", "*", "create", "identity"),
              makePerm("role:update", "Update Roles", "role", "*", "update", "identity"),
              makePerm("role:delete", "Delete Roles", "role", "*", "delete", "identity") },
            { makePerm("perm:read", "View Permissions", "permission", "*", "read", "identity"),
              makePerm("perm:assign", "Assign Permissions", "permission", "*", "assign", "identity") },
            { makePerm("tenant:read", "View Tenants", "tenant", "*", "read", "identity"),
              makePerm("tenant:create", "Create Tenants", "tenant", "*", "create", "identity"),
              makePerm("tenant:update", "Update Tenants", "tenant", "*", "update", "identity") },
            { makePerm("org:read", "View Organizations", "organization", "*", "read", "identity"),
              makePerm("org:create", "Create Organizations", "organization", "*", "create", "identity"),
              makePerm("org:update", "Update Organizations", "organization", "*", "update", "identity") },
            { makePerm("company:read", "View Companies", "company", "*", "read", "identity"),
              makePerm("company:create", "Create Companies", "company", "*", "create", "identity"),
              makePerm("company:update", "Update Companies", "company", "*", "update", "identity") },
            { makePerm("branch:read", "View Branches", "branch", "*", "read", "identity"),
              makePerm("branch:create", "Create Branches", "branch", "*", "create", "identity"),
              makePerm("branch:update", "Update Branches", "branch", "*", "update", "identity") },
            { makePerm("dept:read", "View Departments", "department", "*", "read", "identity"),
              makePerm("dept:create", "Create Departments", "department", "*", "create", "identity"),
              makePerm("dept:update", "Update Departments", "department", "*", "update", "identity") },
            { makePerm("session:read", "View Sessions", "session", "*", "read", "identity"),
              makePerm("session:revoke", "Revoke Sessions", "session", "*", "revoke", "identity") },
            { makePerm("audit:read", "View Audit Logs", "audit", "*", "read", "identity") },
            { makePerm("report:read", "View Reports", "report", "*", "read", "analytics"),
              makePerm("report:export", "Export Reports", "report", "*", "export", "analytics") },
            { makePerm("dashboard:read", "View Dashboards", "dashboard", "*", "read", "analytics"),
              makePerm("dashboard:manage", "Manage Dashboards", "dashboard", "*", "manage", "analytics") },
            { makePerm("settings:admin", "System Settings", "settings", "*", "admin", "system") },
        };
        for (Permission[] group : permGroups) {
            for (Permission p : group) {
                permissionRepository.save(p);
            }
        }

        // ── Roles ──
        Role sysAdmin = new Role();
        sysAdmin.setCode("sys_admin");
        sysAdmin.setName("System Administrator");
        sysAdmin.setDescription("Full system access - bypasses all permission checks");
        sysAdmin.setIsSystem(true);
        roleRepository.save(sysAdmin);

        Role tntAdmin = new Role();
        tntAdmin.setCode("tnt_admin");
        tntAdmin.setName("Tenant Administrator");
        tntAdmin.setDescription("Tenant-level administrative access");
        tntAdmin.setIsSystem(true);
        tntAdmin.setTenant(acme);
        roleRepository.save(tntAdmin);

        Role user = new Role();
        user.setCode("user");
        user.setName("Regular User");
        user.setDescription("Standard user with basic permissions");
        user.setIsSystem(true);
        roleRepository.save(user);

        Role viewer = new Role();
        viewer.setCode("viewer");
        viewer.setName("Read Only");
        viewer.setDescription("Read-only access to assigned resources");
        viewer.setIsSystem(true);
        roleRepository.save(viewer);

        Role manager = new Role();
        manager.setCode("manager");
        manager.setName("Manager");
        manager.setDescription("Manager with elevated access");
        manager.setIsSystem(true);
        roleRepository.save(manager);

        Role salesExec = new Role();
        salesExec.setCode("sales_executive");
        salesExec.setName("Sales Executive");
        salesExec.setDescription("Sales team member");
        salesExec.setIsSystem(true);
        roleRepository.save(salesExec);

        Role warehouseOp = new Role();
        warehouseOp.setCode("warehouse_op");
        warehouseOp.setName("Warehouse Operator");
        warehouseOp.setDescription("Warehouse and inventory operations");
        warehouseOp.setIsSystem(true);
        roleRepository.save(warehouseOp);

        Role hrManager = new Role();
        hrManager.setCode("hr_manager");
        hrManager.setName("HR Manager");
        hrManager.setDescription("Human resources manager");
        hrManager.setIsSystem(true);
        roleRepository.save(hrManager);

        // ── Role-Permission assignments ──
        // tnt_admin gets all permissions
        for (Permission[] group : permGroups) {
            for (Permission p : group) {
                RolePermission rp = new RolePermission();
                rp.setRole(tntAdmin);
                rp.setPermission(p);
                rolePermissionRepository.save(rp);
            }
        }
        // user gets read + basic
        for (String code : new String[]{"user:read", "role:read", "perm:read", "tenant:read", "org:read",
                "company:read", "branch:read", "dept:read", "session:read", "report:read", "dashboard:read", "audit:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> {
                RolePermission rp = new RolePermission();
                rp.setRole(user);
                rp.setPermission(p);
                rolePermissionRepository.save(rp);
            });
        }
        // viewer gets read-only
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "report:read", "dashboard:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> {
                RolePermission rp = new RolePermission();
                rp.setRole(viewer);
                rp.setPermission(p);
                rolePermissionRepository.save(rp);
            });
        }
        // manager gets user permissions + more
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "session:read", "report:read",
                "dashboard:read", "audit:read", "user:create", "user:update"}) {
            permissionRepository.findByCode(code).ifPresent(p -> {
                RolePermission rp = new RolePermission();
                rp.setRole(manager);
                rp.setPermission(p);
                rolePermissionRepository.save(rp);
            });
        }
        // sales_executive gets read + basic
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "report:read", "dashboard:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> {
                RolePermission rp = new RolePermission();
                rp.setRole(salesExec);
                rp.setPermission(p);
                rolePermissionRepository.save(rp);
            });
        }
        // warehouse_op gets read + basic
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "report:read", "dashboard:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> {
                RolePermission rp = new RolePermission();
                rp.setRole(warehouseOp);
                rp.setPermission(p);
                rolePermissionRepository.save(rp);
            });
        }
        // hr_manager gets read + basic (same as viewer for now)
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "report:read", "dashboard:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> {
                RolePermission rp = new RolePermission();
                rp.setRole(hrManager);
                rp.setPermission(p);
                rolePermissionRepository.save(rp);
            });
        }

        // ── Users ──
        UserAccount admin = makeUser("admin", "Admin@123", "admin@acme.com",
                "System", "Administrator", "+1-555-1000", null);

        UserAccount john = makeUser("john.doe", "User@123", "john.doe@acme.com",
                "John", "Doe", "+1-555-1001", "https://api.dicebear.com/7.x/avataaars/svg?seed=john");

        UserAccount jane = makeUser("jane.smith", "User@123", "jane.smith@acme.com",
                "Jane", "Smith", "+1-555-1002", "https://api.dicebear.com/7.x/avataaars/svg?seed=jane");

        UserAccount bob = makeUser("bob.wilson", "User@123", "bob.wilson@globex.com",
                "Bob", "Wilson", "+1-555-1003", "https://api.dicebear.com/7.x/avataaars/svg?seed=bob");

        UserAccount alice = makeUser("alice.johnson", "User@123", "alice.johnson@acme.com",
                "Alice", "Johnson", "+1-555-1004", "https://api.dicebear.com/7.x/avataaars/svg?seed=alice");

        UserAccount charlie = makeUser("charlie.brown", "User@123", "charlie.brown@globex.com",
                "Charlie", "Brown", "+1-555-1005", "https://api.dicebear.com/7.x/avataaars/svg?seed=charlie");

        UserAccount diana = makeUser("diana.prince", "User@123", "diana.prince@globex.com",
                "Diana", "Prince", "+1-555-1006", "https://api.dicebear.com/7.x/avataaars/svg?seed=diana");

        // ── New test users ──
        UserAccount auto = makeUser("auto.user", "User@123", "auto.user@acme.com",
                "Auto", "User", "+1-555-1010", "https://api.dicebear.com/7.x/avataaars/svg?seed=auto");

        UserAccount superU = makeUser("super.user", "User@123", "super.user@erp.com",
                "Super", "User", "+1-555-1011", "https://api.dicebear.com/7.x/avataaars/svg?seed=super");

        UserAccount multiOrg = makeUser("multi-org.user", "User@123", "multi-org.user@acme.com",
                "Multi", "Org", "+1-555-1012", "https://api.dicebear.com/7.x/avataaars/svg?seed=multiorg");

        UserAccount multiCo = makeUser("multi-co.user", "User@123", "multi-co.user@acme.com",
                "Multi", "Company", "+1-555-1013", "https://api.dicebear.com/7.x/avataaars/svg?seed=multico");

        UserAccount multiBranch = makeUser("multi-branch.user", "User@123", "multi-branch.user@acme.com",
                "Multi", "Branch", "+1-555-1014", "https://api.dicebear.com/7.x/avataaars/svg?seed=multibranch");

        UserAccount multiRole = makeUser("multi-role.user", "User@123", "multi-role.user@acme.com",
                "Multi", "Role", "+1-555-1015", "https://api.dicebear.com/7.x/avataaars/svg?seed=multirole");

        // ── User-Role assignments ──
        assignRole(admin, sysAdmin);
        assignRole(admin, tntAdmin);
        assignRole(admin, user);
        assignRole(admin, viewer);
        assignRole(john, user);
        assignRole(jane, tntAdmin);
        assignRole(bob, viewer);
        assignRole(alice, user);
        assignRole(charlie, user);
        assignRole(diana, tntAdmin);
        // new users
        assignRole(auto, user);
        assignRole(superU, sysAdmin);
        assignRole(superU, tntAdmin);
        assignRole(multiOrg, user);
        assignRole(multiCo, user);
        assignRole(multiBranch, salesExec);
        assignRole(multiBranch, warehouseOp);
        assignRole(multiRole, manager);
        assignRole(multiRole, user);
        assignRole(multiRole, viewer);

        // ── User-Organization assignments ──
        assignOrg(admin, acmeGlobal);
        assignOrg(admin, acmeApac);
        assignOrg(admin, globexCorp);
        assignOrg(john, acmeGlobal);
        assignOrg(jane, acmeGlobal);
        assignOrg(bob, globexCorp);
        assignOrg(alice, acmeGlobal);
        assignOrg(charlie, globexCorp);
        assignOrg(diana, globexCorp);
        // new users
        assignOrg(auto, acmeGlobal);
        assignOrg(superU, acmeGlobal);
        assignOrg(superU, globexCorp);
        assignOrg(multiOrg, acmeGlobal);
        assignOrg(multiOrg, acmeApac);
        assignOrg(multiCo, acmeGlobal);
        assignOrg(multiBranch, acmeGlobal);
        assignOrg(multiRole, acmeGlobal);

        // ── User-Company assignments ──
        assignCompany(admin, acmeInc, true);
        assignCompany(admin, acmeEu, false);
        assignCompany(admin, apacInc, false);
        assignCompany(admin, globexLtd, false);
        assignCompany(john, acmeInc, true);
        assignCompany(jane, acmeEu, true);
        assignCompany(bob, globexLtd, true);
        assignCompany(alice, acmeInc, true);
        assignCompany(charlie, globexLtd, true);
        assignCompany(diana, globexLtd, true);
        // new users
        assignCompany(auto, acmeInc, true);
        assignCompany(superU, acmeInc, true);
        assignCompany(superU, globexLtd, false);
        assignCompany(multiOrg, acmeInc, true);
        assignCompany(multiOrg, apacInc, false);
        assignCompany(multiCo, acmeInc, true);
        assignCompany(multiCo, acmeEu, false);
        assignCompany(multiBranch, acmeInc, true);
        assignCompany(multiRole, acmeInc, true);

        // ── User-Branch assignments ──
        assignBranch(admin, ho, true);
        assignBranch(admin, nb, false);
        assignBranch(admin, euHq, false);
        assignBranch(admin, apacHq, false);
        assignBranch(admin, gxHq, false);
        assignBranch(john, ho, true);
        assignBranch(jane, euHq, true);
        assignBranch(bob, gxHq, true);
        assignBranch(alice, ho, true);
        assignBranch(charlie, gxHq, true);
        assignBranch(diana, gxHq, true);
        assignBranch(auto, ho, true);
        assignBranch(superU, ho, true);
        assignBranch(superU, gxHq, false);
        assignBranch(multiOrg, ho, true);
        assignBranch(multiOrg, apacHq, false);
        assignBranch(multiCo, ho, true);
        assignBranch(multiCo, euHq, false);
        assignBranch(multiBranch, ho, true);
        assignBranch(multiBranch, nb, false);
        assignBranch(multiRole, ho, true);

        // ── User Preferences ──
        makePref(admin, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(john, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(jane, "de", "Europe/Berlin", "DD.MM.YYYY", "HH:mm", "#.##0,00", "EUR", "dark", 50);
        makePref(bob, "en", "Europe/London", "DD/MM/YYYY", "HH:mm", "#,##0.00", "GBP", "light", 25);
        makePref(alice, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(charlie, "en", "Europe/London", "DD/MM/YYYY", "HH:mm", "#,##0.00", "GBP", "light", 25);
        makePref(diana, "en", "Europe/London", "DD/MM/YYYY", "HH:mm", "#,##0.00", "GBP", "dark", 50);
        makePref(auto, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(superU, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(multiOrg, "en", "Asia/Kolkata", "DD/MM/YYYY", "HH:mm", "#,##0.00", "INR", "light", 25);
        makePref(multiCo, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(multiBranch, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(multiRole, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);

        log.info("Identity seed data created successfully.");

        log.info("  admin          / Admin@123   - Super-user, all tenants");
        log.info("  john.doe       / User@123    - ACME regular, ACME-INC, HO");
        log.info("  jane.smith     / User@123    - ACME tnt_admin, ACME-EU, EU-HQ (German locale)");
        log.info("  bob.wilson     / User@123    - GLOBEX viewer, GLOBEX-LTD, GX-HQ");
        log.info("  alice.johnson  / User@123    - ACME regular, ACME-INC, HO");
        log.info("  charlie.brown  / User@123    - GLOBEX regular, GLOBEX-LTD, GX-HQ");
        log.info("  diana.prince   / User@123    - GLOBEX tnt_admin, GLOBEX-LTD, GX-HQ");
        log.info("  auto.user      / User@123    - Single profile → auto-route");
        log.info("  super.user     / User@123    - Multi-tenant (ACME + GLOBEX)");
        log.info("  multi-org.user / User@123    - 2 orgs under ACME (ACME-GLOBAL + ACME-APAC)");
        log.info("  multi-co.user  / User@123    - 2 companies under ACME-GLOBAL (ACME-INC + ACME-EU)");
        log.info("  multi-branch.user / User@123 - 2 branches same co (HO + NB), 2 roles");
        log.info("  multi-role.user / User@123   - 3 roles (manager + user + viewer) same branch");
    }

    // ── Helpers ──

    private Permission makePerm(String code, String name, String resourceType, String resource, String action, String module) {
        Permission p = new Permission();
        p.setCode(code);
        p.setName(name);
        p.setResourceType(resourceType);
        p.setResource(resource);
        p.setAction(action);
        p.setModule(module);
        p.setIsSystem(true);
        return p;
    }

    private UserAccount makeUser(String username, String rawPassword, String email,
                                  String firstName, String lastName, String phone, String avatarUrl) {
        UserAccount u = new UserAccount();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setEmail(email);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setPhone(phone);
        u.setAvatarUrl(avatarUrl);
        u.setStatus("ACTIVE");
        u.setEmailVerified(true);
        u.setFailedAttempts(0);
        userAccountRepository.save(u);
        return u;
    }

    private void assignRole(UserAccount u, Role r) {
        UserRole ur = new UserRole();
        ur.setUser(u);
        ur.setRole(r);
        userRoleRepository.save(ur);
    }

    private void assignOrg(UserAccount u, Organization o) {
        UserOrganization uo = new UserOrganization();
        uo.setUser(u);
        uo.setOrganization(o);
        userOrganizationRepository.save(uo);
    }

    private void assignCompany(UserAccount u, Company c, boolean isDefault) {
        UserCompany uc = new UserCompany();
        uc.setUser(u);
        uc.setCompany(c);
        uc.setIsDefault(isDefault);
        userCompanyRepository.save(uc);
    }

    private void assignBranch(UserAccount u, Branch b, boolean isDefault) {
        UserBranch ub = new UserBranch();
        ub.setUser(u);
        ub.setBranch(b);
        ub.setIsDefault(isDefault);
        userBranchRepository.save(ub);
    }

    private void makePref(UserAccount u, String lang, String tz, String dateFmt, String timeFmt,
                           String numFmt, String currency, String theme, int itemsPerPage) {
        UserPreference pref = new UserPreference();
        pref.setUser(u);
        pref.setLanguage(lang);
        pref.setTimezone(tz);
        pref.setDateFormat(dateFmt);
        pref.setTimeFormat(timeFmt);
        pref.setNumberFormat(numFmt);
        pref.setCurrency(currency);
        pref.setTheme(theme);
        pref.setNotificationsEnabled(true);
        pref.setItemsPerPage(itemsPerPage);
        userPreferenceRepository.save(pref);
    }
}
