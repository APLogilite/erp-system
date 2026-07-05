package com.erp.platform.identity.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

  String resourceType() default "MODULE";

  String resource();

  String action() default "READ";

  String message() default "Access denied";
}
