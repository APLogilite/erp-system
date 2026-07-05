package com.erp.common.utils;

import java.util.UUID;

/**
 * Utility helpers for UUID generation.
 */
public final class UuidUtils {
  private UuidUtils() {}

  public static UUID newUuid() {
    return UUID.randomUUID();
  }
}
