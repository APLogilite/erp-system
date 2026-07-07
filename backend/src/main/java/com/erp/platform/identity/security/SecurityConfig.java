package com.erp.platform.identity.security;

import com.erp.common.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final ContextFilter contextFilter;
  private final ObjectMapper objectMapper;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                        ContextFilter contextFilter,
                        ObjectMapper objectMapper) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.contextFilter = contextFilter;
    this.objectMapper = objectMapper;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/login").permitAll()
            .requestMatchers("/api/v1/auth/refresh").permitAll()
            .requestMatchers("/api/v1/auth/logout").permitAll()
            .requestMatchers("/api/v1/auth/me").authenticated()
            .requestMatchers("/api/v1/auth/change-password").authenticated()
            .requestMatchers("/api/v1/context/current").authenticated()
            .requestMatchers("/api/v1/context/options").authenticated()
            .requestMatchers("/api/v1/context/switch").authenticated()
            .requestMatchers("/api/v1/identity/**").authenticated()
            .requestMatchers("/api/v1/auth/permissions").authenticated()
            .requestMatchers("/api/v1/auth/check-permission").authenticated()
            .requestMatchers("/api/v1/auth/permissions/invalidate").authenticated()
            .requestMatchers("/api/v1/identity/audit/**").authenticated()
            .anyRequest().permitAll()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint())
            .accessDeniedHandler(accessDeniedHandler())
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(contextFilter, JwtAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return (HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) -> {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      ApiResponse<Void> apiResponse = ApiResponse.error(
          "IDENTITY_AUTH_001", "Authentication required");
      objectMapper.writeValue(response.getWriter(), apiResponse);
    };
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return (HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) -> {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      ApiResponse<Void> apiResponse = ApiResponse.error(
          "IDENTITY_AUTH_005", "Access denied");
      objectMapper.writeValue(response.getWriter(), apiResponse);
    };
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
