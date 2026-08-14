package com.company.scopery.modules.traceability.validationruletype.application.response;

import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;

import java.time.Instant;
import java.util.UUID;

public record RegistryValidationRuleTypeResponse(
        UUID id,
        UUID workspaceId,
        String code,
        String name,
        String category,
        String paramSchemaJson,
        String defaultMessage,
        String description,
        boolean isSystem,
        String status,
        int displayOrder,
        Instant createdAt
) {
    public static RegistryValidationRuleTypeResponse from(RegistryValidationRuleType r) {
        return new RegistryValidationRuleTypeResponse(
                r.id(),
                r.workspaceId(),
                r.code(),
                r.name(),
                r.category(),
                r.paramSchemaJson(),
                r.defaultMessage(),
                r.description(),
                r.isSystem(),
                r.status().name(),
                r.displayOrder(),
                r.createdAt()
        );
    }
}
