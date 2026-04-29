package com.erp.modules.sales.controller;

import com.erp.config.ApiVersionConfig;
import com.erp.modules.sales.service.SalesService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/sales")
public class SalesController {

  private final SalesService salesService;

  public SalesController(SalesService salesService) {
    this.salesService = salesService;
  }
}
