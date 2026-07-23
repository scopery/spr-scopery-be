package com.company.scopery.modules.traceability.businessrule.domain.model;

import com.company.scopery.modules.traceability.businessrule.domain.enums.BusinessRuleSeverity;
import com.company.scopery.modules.traceability.businessrule.domain.enums.BusinessRuleStatus;

import java.time.Instant;
import java.util.UUID;

public record BusinessRule(
        UUID id,
        UUID functionalItemId,
        UUID projectId,
        String code,
        String title,
        String description,
        BusinessRuleSeverity severity,
        BusinessRuleStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static BusinessRule create(
            UUID functionalItemId,
            UUID projectId,
            String code,
            String title,
            String description,
            BusinessRuleSeverity severity
    ) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new BusinessRule(
                UUID.randomUUID(),
                functionalItemId,
                projectId,
                code,
                title,
                description,
                severity,
                BusinessRuleStatus.DRAFT,
                0,
                null,
                null
        );
    }

    public BusinessRule withUpdated(
            String title,
            String description,
            BusinessRuleSeverity severity,
            BusinessRuleStatus status
    ) {
        return new BusinessRule(
                this.id,
                this.functionalItemId,
                this.projectId,
                this.code,
                title,
                description,
                severity,
                status,
                this.version,
                this.createdAt,
                Instant.now()
        );
    }
}
