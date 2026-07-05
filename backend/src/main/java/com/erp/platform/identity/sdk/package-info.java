/**
 * Identity Platform SDK — the single entry point for business modules
 * to consume identity services.
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * @Service
 * public class SalesService {
 *     private final CurrentContextProvider context;
 *     private final PermissionProvider permission;
 *
 *     public SalesService(CurrentContextProvider context,
 *                         PermissionProvider permission) {
 *         this.context = context;
 *         this.permission = permission;
 *     }
 *
 *     public void createOrder(Order order) {
 *         permission.checkPermission("SALES", "order", "WRITE");
 *         String tenantId = context.getCurrentTenantId();
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * <h2>Architecture</h2>
 * <ul>
 *   <li><b>Providers</b> — Injectable Spring interfaces for getting
 *       current user, context, permissions, and session info.</li>
 *   <li><b>Annotations</b> — {@code @CurrentUser}, {@code @CurrentContext},
 *       {@code @EnableTenantFilter} for declarative injection / filtering.</li>
 *   <li><b>IdentityFacade</b> — Single injected bean that exposes all
 *       provider methods.</li>
 *   <li><b>IdentityClient</b> — For non-Spring contexts (schedulers,
 *       batch jobs) where no HTTP request context exists.</li>
 *   <li><b>Helpers</b> — {@code AuthorizationHelper} and {@code ContextHelper}
 *       for quick static access.</li>
 *   <li><b>Plugin system</b> — {@code PluginProvider} SPI, plus
 *       PermissionRegistry, RoleRegistry, MenuRegistry,
 *       ContextExtensionRegistry.</li>
 *   <li><b>Multi-tenant</b> — Hibernate filter definitions and
 *       {@code TenantFilterAspect} for automatic tenant/org/company
 *       filtering.</li>
 *   <li><b>Engine integrations</b> — Metadata, Workflow, Scheduler,
 *       Notification, Audit bridges.</li>
 * </ul>
 */
package com.erp.platform.identity.sdk;
