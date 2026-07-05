package com.erp.platform.identity.sdk.resolver;

import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.entity.UserAccount;
import com.erp.platform.identity.repository.UserAccountRepository;
import com.erp.platform.identity.sdk.annotation.CurrentUser;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserAccountRepository userAccountRepository;

    public CurrentUserArgumentResolver(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && UserAccount.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        RuntimeContext ctx = RuntimeContextHolder.get();
        if (ctx == null || ctx.getUserId() == null) {
            return null;
        }
        return userAccountRepository.findById(ctx.getUserId()).orElse(null);
    }
}
