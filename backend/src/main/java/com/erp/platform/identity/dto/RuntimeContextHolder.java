package com.erp.platform.identity.dto;

/**
 * ThreadLocal holder for RuntimeContext.
 *
 * Set at the beginning of each authenticated HTTP request via ContextFilter.
 * Cleared automatically in a finally block after the request completes.
 * Business modules retrieve the context via RuntimeContextHolder.get() or
 * by injecting it as a method parameter.
 */
public final class RuntimeContextHolder {

  private static final ThreadLocal<RuntimeContext> CONTEXT = new ThreadLocal<>();

  private RuntimeContextHolder() {}

  public static void set(RuntimeContext context) {
    CONTEXT.set(context);
  }

  public static RuntimeContext get() {
    return CONTEXT.get();
  }

  public static void clear() {
    CONTEXT.remove();
  }
}
