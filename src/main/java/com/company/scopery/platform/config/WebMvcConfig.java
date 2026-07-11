package com.company.scopery.platform.config;

import com.company.scopery.common.constant.ApiPaths;
import com.company.scopery.platform.security.AiAgentSecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final AiAgentSecurityInterceptor aiAgentSecurityInterceptor;
    public WebMvcConfig(AiAgentSecurityInterceptor aiAgentSecurityInterceptor) {
        this.aiAgentSecurityInterceptor = aiAgentSecurityInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(aiAgentSecurityInterceptor).addPathPatterns(ApiPaths.BASE_PATH + "/ai-agent/**");
    }
}
