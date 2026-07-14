package com.erp;

import com.erp.platform.identity.entity.*;
import com.erp.platform.identity.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final RoleOrganizationRepository roleOrganizationRepository;
    private final RoleCompanyRepository roleCompanyRepository;
    private final RoleBranchRepository roleBranchRepository;
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
                            RoleOrganizationRepository roleOrganizationRepository,
                            RoleCompanyRepository roleCompanyRepository,
                            RoleBranchRepository roleBranchRepository,
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
        this.roleOrganizationRepository = roleOrganizationRepository;
        this.roleCompanyRepository = roleCompanyRepository;
        this.roleBranchRepository = roleBranchRepository;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Check if seed data already exists by looking for known roles
        // (checks both users and roles to handle partially-seeded databases)
        if (userAccountRepository.count() > 0 || roleRepository.findByCode("tnt_admin").isPresent()) {
            log.info("Identity seed data already exists, skipping.");
            return;
        }
        log.info("Creating identity seed data...");

        // ── Tenants ──
        Tenant sysTenant = makeTenant("SYS", "System", "system.erp.local", "en", "UTC", "USD");
        Tenant acme = makeTenant("ACME", "Acme Corporation", "acme.example.com", "en", "America/New_York", "USD");
        Tenant globex = makeTenant("GLOBEX", "Globex Industries", "globex.example.com", "en", "Europe/London", "EUR");

        // ── Organizations ──
        Organization sysOrg = makeOrg("SYS-ORG", "System Organization", sysTenant, null, 0, "/SYS-ORG");
        Organization acmeGlobal = makeOrg("ACME-GLOBAL", "Acme Global", acme, null, 0, "/ACME-GLOBAL");
        Organization acmeApac = makeOrg("ACME-APAC", "Acme APAC", acme, null, 0, "/ACME-APAC");
        Organization globexCorp = makeOrg("GLOBEX-CORP", "Globex Corp", globex, null, 0, "/GLOBEX-CORP");

        // ── Companies ──
        Company acmeInc = makeCompany("ACME-INC", "Acme Inc.", acmeGlobal, acme, "TAX-US-001", "USD", "123 Main Street, New York, NY 10001");
        Company acmeEu = makeCompany("ACME-EU", "Acme Europe GmbH", acmeGlobal, acme, "TAX-EU-001", "EUR", "50 Berliner Strasse, Berlin, Germany");
        Company apacInc = makeCompany("APAC-INC", "Acme APAC Inc.", acmeApac, acme, "TAX-IN-001", "INR", "42 Tech Park, Mumbai, India");
        Company globexLtd = makeCompany("GLOBEX-LTD", "Globex Ltd.", globexCorp, globex, "TAX-UK-001", "GBP", "1 London Bridge, London, UK");

        // ── Branches ──
        Branch ho = makeBranch("HO", "Head Office", acmeInc, acme, true);
        Branch nb = makeBranch("NB", "North Branch", acmeInc, acme, false);
        Branch euHq = makeBranch("EU-HQ", "Europe HQ", acmeEu, acme, true);
        Branch apacHq = makeBranch("APAC-HQ", "APAC Headquarters", apacInc, acme, true);
        Branch gxHq = makeBranch("GX-HQ", "Globex HQ", globexLtd, globex, true);

        // ── Departments ──
        makeDept("ENG", "Engineering", ho, acme, "CC-ENG");
        makeDept("SALES", "Sales", ho, acme, "CC-SALES");
        makeDept("FIN", "Finance", ho, acme, "CC-FIN");
        makeDept("LEGAL", "Legal", ho, acme, "CC-LEGAL");
        makeDept("IT", "Information Technology", ho, acme, "CC-IT");
        makeDept("HR", "Human Resources", ho, acme, "CC-HR");
        makeDept("SUPPORT", "Customer Support", nb, acme, "CC-SUPPORT");
        makeDept("LOGISTICS", "Logistics", nb, acme, "CC-LOGISTICS");
        makeDept("SERVICE", "Field Service", nb, acme, "CC-SERVICE");
        makeDept("EU-DEV", "Development", euHq, acme, "CC-EU-DEV");
        makeDept("EU-SALES", "European Sales", euHq, acme, "CC-EU-SALES");
        makeDept("EU-MARKETING", "European Marketing", euHq, acme, "CC-EU-MKT");
        makeDept("APAC-ENG", "APAC Engineering", apacHq, acme, "CC-APAC-ENG");
        makeDept("APAC-HR", "APAC Human Resources", apacHq, acme, "CC-APAC-HR");
        makeDept("GX-OPS", "Operations", gxHq, globex, "CC-GX-OPS");
        makeDept("GX-FINANCE", "Finance", gxHq, globex, "CC-GX-FIN");
        makeDept("GX-HR", "Human Resources", gxHq, globex, "CC-GX-HR");
        makeDept("GX-SALES", "Sales", gxHq, globex, "CC-GX-SALES");

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
            for (Permission p : group) { permissionRepository.save(p); }
        }

        // ── Roles (under SYS tenant as templates) ──
        Role sysAdminRole = makeRole("sys_admin", "System Administrator", "Full system access", true, sysTenant);
        Role tntAdminRole = makeRole("tnt_admin", "Tenant Administrator", "Tenant-level admin access", true, sysTenant);
        Role userRole = makeRole("user", "Regular User", "Standard user with basic permissions", true, sysTenant);
        Role viewerRole = makeRole("viewer", "Read Only", "Read-only access", true, sysTenant);
        Role managerRole = makeRole("manager", "Manager", "Manager with elevated access", true, sysTenant);
        Role salesExecRole = makeRole("sales_executive", "Sales Executive", "Sales team member", true, sysTenant);
        Role warehouseOpRole = makeRole("warehouse_op", "Warehouse Operator", "Warehouse operations", true, sysTenant);
        Role hrManagerRole = makeRole("hr_manager", "HR Manager", "Human resources manager", true, sysTenant);

        // ── Role-Permission assignments ──
        // tnt_admin gets all permissions
        for (Permission[] group : permGroups) {
            for (Permission p : group) { assignRolePermission(tntAdminRole, p); }
        }
        // user gets read + basic
        for (String code : new String[]{"user:read", "role:read", "perm:read", "tenant:read", "org:read",
                "company:read", "branch:read", "dept:read", "session:read", "report:read", "dashboard:read", "audit:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> assignRolePermission(userRole, p));
        }
        // viewer gets read-only
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "report:read", "dashboard:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> assignRolePermission(viewerRole, p));
        }
        // manager gets user permissions + more
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "session:read", "report:read",
                "dashboard:read", "audit:read", "user:create", "user:update"}) {
            permissionRepository.findByCode(code).ifPresent(p -> assignRolePermission(managerRole, p));
        }
        // sales_executive gets read
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "report:read", "dashboard:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> assignRolePermission(salesExecRole, p));
        }
        // warehouse_op gets read
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "report:read", "dashboard:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> assignRolePermission(warehouseOpRole, p));
        }
        // hr_manager gets read
        for (String code : new String[]{"user:read", "role:read", "perm:read", "org:read",
                "company:read", "branch:read", "dept:read", "report:read", "dashboard:read"}) {
            permissionRepository.findByCode(code).ifPresent(p -> assignRolePermission(hrManagerRole, p));
        }

        // ── Role-Organization scopes (only for roles that need restricted access) ──
        // sys_admin role → NO RoleOrg → full access to SYS tenant (admin user only)
        // tnt_admin role → NO RoleOrg → full tenant access (template)
        // user role → NO RoleOrg → full tenant access (template)
        // viewer role → NO RoleOrg → full tenant access (template)

        // ── Create ACME-specific roles (clones of templates) ──
        Role acmeTntAdminRole = makeRole("tnt_admin", "Tenant Administrator", "Full ACME access", false, acme);
        Role acmeUserRole = makeRole("user", "Regular User", "Standard user for ACME", false, acme);
        Role acmeViewerRole = makeRole("viewer", "Read Only", "Read-only for ACME", false, acme);
        Role acmeManagerRole = makeRole("manager", "Manager", "Manager for ACME", false, acme);
        Role acmeSalesExecRole = makeRole("sales_executive", "Sales Executive", "Sales for ACME", false, acme);
        Role acmeWarehouseOpRole = makeRole("warehouse_op", "Warehouse Operator", "Warehouse for ACME", false, acme);

        // ACME roles with restricted scopes
        assignRoleOrg(acmeUserRole, acmeGlobal);
        assignRoleCo(acmeUserRole, acmeInc);
        assignRoleBranch(acmeUserRole, ho);

        assignRoleOrg(acmeViewerRole, acmeGlobal);

        assignRoleOrg(acmeManagerRole, acmeGlobal);
        assignRoleCo(acmeManagerRole, acmeInc);
        assignRoleBranch(acmeManagerRole, ho);

        assignRoleOrg(acmeSalesExecRole, acmeGlobal);
        assignRoleCo(acmeSalesExecRole, acmeInc);
        assignRoleBranch(acmeSalesExecRole, ho);

        assignRoleOrg(acmeWarehouseOpRole, acmeGlobal);
        assignRoleCo(acmeWarehouseOpRole, acmeInc);
        assignRoleBranch(acmeWarehouseOpRole, nb);

        // ── Create GLOBEX-specific roles ──
        Role globexTntAdminRole = makeRole("tnt_admin", "Tenant Administrator", "Full GLOBEX access", false, globex);
        Role globexUserRole = makeRole("user", "Regular User", "Standard user for GLOBEX", false, globex);
        Role globexViewerRole = makeRole("viewer", "Read Only", "Read-only for GLOBEX", false, globex);

        assignRoleOrg(globexUserRole, globexCorp);
        assignRoleCo(globexUserRole, globexLtd);
        assignRoleBranch(globexUserRole, gxHq);

        assignRoleOrg(globexViewerRole, globexCorp);

        // ── Users ──
        UserAccount admin = makeUserWithBirthDate("admin", "Admin@123", "admin@acme.com", "System", "Administrator", "1985-03-15");
        UserAccount auto = makeUser("auto.user", "User@123", "auto.user@acme.com", "Auto", "User");
        UserAccount superU = makeUser("super.user", "User@123", "super.user@erp.com", "Super", "User");
        UserAccount jane = makeUserWithBirthDate("jane.smith", "User@123", "jane.smith@acme.com", "Jane", "Smith", "1990-07-22");
        UserAccount john = makeUserWithBirthDate("john.doe", "User@123", "john.doe@acme.com", "John", "Doe", "1988-11-08");
        UserAccount alice = makeUserWithBirthDate("alice.johnson", "User@123", "alice.johnson@acme.com", "Alice", "Johnson", "1992-05-30");
        UserAccount multiOrg = makeUser("multi-org.user", "User@123", "multi-org.user@acme.com", "Multi", "Org");
        UserAccount multiCo = makeUser("multi-co.user", "User@123", "multi-co.user@acme.com", "Multi", "Company");
        UserAccount multiBranch = makeUser("multi-branch.user", "User@123", "multi-branch.user@acme.com", "Multi", "Branch");
        UserAccount multiRole = makeUser("multi-role.user", "User@123", "multi-role.user@acme.com", "Multi", "Role");
        UserAccount bob = makeUser("bob.wilson", "User@123", "bob.wilson@globex.com", "Bob", "Wilson");
        UserAccount charlie = makeUser("charlie.brown", "User@123", "charlie.brown@globex.com", "Charlie", "Brown");
        UserAccount diana = makeUserWithBirthDate("diana.prince", "User@123", "diana.prince@globex.com", "Diana", "Prince", "1987-12-01");

        // ── User-Role assignments ──
        assignUserRole(admin, sysAdminRole);
        assignUserRole(auto, acmeUserRole);
        assignUserRole(superU, sysAdminRole);
        assignUserRole(jane, acmeTntAdminRole);
        assignUserRole(john, acmeUserRole);
        assignUserRole(alice, acmeUserRole);
        assignUserRole(multiOrg, acmeUserRole);
        assignUserRole(multiCo, acmeUserRole);
        assignUserRole(multiBranch, acmeSalesExecRole);
        assignUserRole(multiBranch, acmeWarehouseOpRole);
        assignUserRole(multiRole, acmeManagerRole);
        assignUserRole(multiRole, acmeUserRole);
        assignUserRole(multiRole, acmeViewerRole);
        assignUserRole(bob, globexUserRole);
        assignUserRole(charlie, globexUserRole);
        assignUserRole(diana, globexTntAdminRole);

        // ── User Preferences ──
        makePref(admin, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(auto, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(superU, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(jane, "de", "Europe/Berlin", "DD.MM.YYYY", "HH:mm", "#.##0,00", "EUR", "dark", 50);
        makePref(john, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(alice, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(multiOrg, "en", "Asia/Kolkata", "DD/MM/YYYY", "HH:mm", "#,##0.00", "INR", "light", 25);
        makePref(multiCo, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(multiBranch, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(multiRole, "en", "America/New_York", "YYYY-MM-DD", "HH:mm", "#,##0.00", "USD", "light", 25);
        makePref(bob, "en", "Europe/London", "DD/MM/YYYY", "HH:mm", "#,##0.00", "GBP", "light", 25);
        makePref(charlie, "en", "Europe/London", "DD/MM/YYYY", "HH:mm", "#,##0.00", "GBP", "light", 25);
        makePref(diana, "en", "Europe/London", "DD/MM/YYYY", "HH:mm", "#,##0.00", "GBP", "dark", 50);

        log.info("Identity seed data created successfully.");
        log.info("  admin          / Admin@123   - System admin (SYS tenant only)");
        log.info("  auto.user      / User@123    - Single profile → auto-route");
        log.info("  super.user     / User@123    - System admin (same as admin)");
        log.info("  jane.smith     / User@123    - ACME tnt_admin (full ACME access)");
        log.info("  john.doe       / User@123    - ACME restricted (ACME-GLOBAL → ACME-INC → HO)");
        log.info("  alice.johnson  / User@123    - ACME restricted (same as john.doe)");
        log.info("  multi-org.user / User@123    - ACME restricted (ACME-GLOBAL scope)");
        log.info("  multi-co.user  / User@123    - ACME restricted (2 companies)");
        log.info("  multi-branch.user / User@123 - 2 roles → HO + NB branches");
        log.info("  multi-role.user / User@123   - 3 roles, same scope");
        log.info("  bob.wilson     / User@123    - GLOBEX restricted (GLOBEX-CORP → GLOBEX-LTD → GX-HQ)");
        log.info("  charlie.brown  / User@123    - GLOBEX restricted (same as bob)");
        log.info("  diana.prince   / User@123    - GLOBEX tnt_admin (full GLOBEX access)");
    }

    // ── Helpers ──

    private Tenant makeTenant(String code, String name, String domain, String lang, String tz, String currency) {
        Tenant t = new Tenant(); t.setCode(code); t.setName(name); t.setDomain(domain);
        t.setDefaultLanguage(lang); t.setDefaultTimezone(tz); t.setDefaultCurrency(currency);
        return tenantRepository.save(t);
    }

    private Organization makeOrg(String code, String name, Tenant tenant, Organization parent, int level, String path) {
        Organization o = new Organization(); o.setCode(code); o.setName(name); o.setTenant(tenant);
        o.setParent(parent); o.setLevel(level); o.setPath(path);
        return organizationRepository.save(o);
    }

    private Company makeCompany(String code, String name, Organization org, Tenant tenant, String taxId, String currency, String address) {
        Company c = new Company(); c.setCode(code); c.setName(name); c.setOrganization(org); c.setTenant(tenant);
        c.setTaxId(taxId); c.setCurrency(currency); c.setAddress(address);
        c.setRegistrationNumber("REG-" + code);
        return companyRepository.save(c);
    }

    private Branch makeBranch(String code, String name, Company company, Tenant tenant, boolean isHeadOffice) {
        Branch b = new Branch(); b.setCode(code); b.setName(name); b.setCompany(company); b.setTenant(tenant);
        b.setIsHeadOffice(isHeadOffice);
        return branchRepository.save(b);
    }

    private void makeDept(String code, String name, Branch branch, Tenant tenant, String costCenter) {
        Department d = new Department(); d.setCode(code); d.setName(name); d.setBranch(branch);
        d.setTenant(tenant); d.setCostCenter(costCenter); d.setLevel(0);
        departmentRepository.save(d);
    }

    private Permission makePerm(String code, String name, String resourceType, String resource, String action, String module) {
        Permission p = new Permission(); p.setCode(code); p.setName(name);
        p.setResourceType(resourceType); p.setResource(resource); p.setAction(action);
        p.setModule(module); p.setIsSystem(true);
        return p;
    }

    private Role makeRole(String code, String name, String description, boolean isSystem, Tenant tenant) {
        Role r = new Role(); r.setCode(code); r.setName(name); r.setDescription(description);
        r.setIsSystem(isSystem); r.setTenant(tenant);
        return roleRepository.save(r);
    }

    private void assignRolePermission(Role role, Permission perm) {
        RolePermission rp = new RolePermission(); rp.setRole(role); rp.setPermission(perm);
        rolePermissionRepository.save(rp);
    }

    private void assignRoleOrg(Role role, Organization org) {
        RoleOrganization ro = new RoleOrganization(); ro.setRole(role); ro.setOrganization(org);
        roleOrganizationRepository.save(ro);
    }

    private void assignRoleCo(Role role, Company company) {
        RoleCompany rc = new RoleCompany(); rc.setRole(role); rc.setCompany(company);
        roleCompanyRepository.save(rc);
    }

    private void assignRoleBranch(Role role, Branch branch) {
        RoleBranch rb = new RoleBranch(); rb.setRole(role); rb.setBranch(branch);
        roleBranchRepository.save(rb);
    }

    private UserAccount makeUser(String username, String rawPassword, String email, String firstName, String lastName) {
        UserAccount u = new UserAccount(); u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword)); u.setEmail(email);
        u.setFirstName(firstName); u.setLastName(lastName); u.setStatus("ACTIVE");
        u.setEmailVerified(true); u.setFailedAttempts(0);
        return userAccountRepository.save(u);
    }

    private UserAccount makeUserWithBirthDate(String username, String rawPassword, String email, String firstName, String lastName, String birthDate) {
        UserAccount u = makeUser(username, rawPassword, email, firstName, lastName);
        if (birthDate != null) u.setBirthDate(java.time.LocalDate.parse(birthDate));
        return userAccountRepository.save(u);
    }

    private void assignUserRole(UserAccount u, Role r) {
        UserRole ur = new UserRole(); ur.setUser(u); ur.setRole(r);
        userRoleRepository.save(ur);
    }

    private void makePref(UserAccount u, String lang, String tz, String dateFmt, String timeFmt,
                           String numFmt, String currency, String theme, int itemsPerPage) {
        UserPreference pref = new UserPreference(); pref.setUser(u);
        pref.setLanguage(lang); pref.setTimezone(tz); pref.setDateFormat(dateFmt);
        pref.setTimeFormat(timeFmt); pref.setNumberFormat(numFmt); pref.setCurrency(currency);
        pref.setTheme(theme); pref.setNotificationsEnabled(true); pref.setItemsPerPage(itemsPerPage);
        userPreferenceRepository.save(pref);
    }
}
