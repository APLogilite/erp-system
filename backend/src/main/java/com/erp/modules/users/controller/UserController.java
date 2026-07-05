package com.erp.modules.users.controller;

import com.erp.config.ApiVersionConfig;
import com.erp.modules.users.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }
}
