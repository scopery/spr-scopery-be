package com.company.scopery.modules.traceability.businessrule.application.response;

import com.company.scopery.modules.traceability.businessrule.domain.model.BusinessRule;

import java.time.Instant;
import java.util.UUID;

public record BusinessRuleResponse(
        UUID id,
        UUID functionalItemId,
        UUID projectId,
        String code,
        String title,
        String description,
        String severity,
        String status,
        Instant createdAt,
        Instant updatedAt
) {

    public static BusinessRuleResponse from(BusinessRule rule) {
        return new BusinessRuleResponse(
                rule.id(),
                rule.functionalItemId(),
                rule.projectId(),
                rule.code(),
                rule.title(),
                rule.description(),
                rule.severity() != null ? rule.severity().name() : null,
                rule.status() != null ? rule.status().name() : null,
                rule.createdAt(),
                rule.updatedAt()
        );
    }
}
