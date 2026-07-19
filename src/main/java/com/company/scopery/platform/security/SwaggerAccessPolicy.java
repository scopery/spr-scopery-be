package com.company.scopery.platform.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resolves whether Swagger/OpenAPI infra paths are publicly accessible.
 * Disabled (authenticated required) when {@code scopery.platform.swagger.public=false} (e.g. prod profile).
 */
@Component
public class SwaggerAccessPolicy {

    private final boolean swaggerPublic;

    public SwaggerAccessPolicy(@Value("${scopery.platform.swagger.public:true}") boolean swaggerPublic) {
        this.swaggerPublic = swaggerPublic;
    }

    public List<String> infraPublicPaths() {
        if (!swaggerPublic) {
            return List.of("/actuator/health");
        }
        return SecurityPathPolicy.defaultInfraPublicPaths();
    }

    public boolean isSwaggerPublic() {
        return swaggerPublic;
    }
}
