package com.erp.platform.identity.sdk.config;

import com.erp.platform.identity.sdk.resolver.CurrentContextArgumentResolver;
import com.erp.platform.identity.sdk.resolver.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class SdkWebMvcConfigurer implements WebMvcConfigurer {

    private final CurrentUserArgumentResolver currentUserArgumentResolver;
    private final CurrentContextArgumentResolver currentContextArgumentResolver;

    public SdkWebMvcConfigurer(CurrentUserArgumentResolver currentUserArgumentResolver,
                               CurrentContextArgumentResolver currentContextArgumentResolver) {
        this.currentUserArgumentResolver = currentUserArgumentResolver;
        this.currentContextArgumentResolver = currentContextArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
        resolvers.add(currentContextArgumentResolver);
    }
}
