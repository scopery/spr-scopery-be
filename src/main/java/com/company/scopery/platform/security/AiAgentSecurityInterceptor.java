package com.company.scopery.platform.security;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AiAgentSecurityInterceptor implements HandlerInterceptor {
    private final IamSystemAuthorizationService authorizationService;
    public AiAgentSecurityInterceptor(IamSystemAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String required = requiredAuthority(path, method);
        if (required != null) authorizationService.requireSystemRight(required);
        return true;
    }
    private String requiredAuthority(String path, String method) {
        if (path.contains("/provider-secrets")) return "AI_PROVIDER_SECRET_MANAGE";
        if (path.contains("/playground") && !HttpMethod.GET.matches(method)) return "AI_PLAYGROUND_RUN";
        if (path.contains("/execution-logs") || path.contains("/executions")) return "AI_EXECUTION_VIEW_OR_RUN";
        if (path.contains("/prompt-versions") && !HttpMethod.GET.matches(method)) return "AI_PROMPT_PUBLISH";
        if (path.contains("/event-configs") && !HttpMethod.GET.matches(method)) return "AI_EVENT_CONFIG_MANAGE";
        if ((path.contains("/providers") || path.contains("/deployments")) && !HttpMethod.GET.matches(method)) {
            return "AI_PLATFORM_MANAGE";
        }
        return null;
    }
}
