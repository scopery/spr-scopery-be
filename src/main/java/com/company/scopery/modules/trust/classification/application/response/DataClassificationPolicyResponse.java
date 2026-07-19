package com.company.scopery.modules.trust.classification.application.response;
import com.company.scopery.modules.trust.classification.domain.model.DataClassificationPolicy;
import java.time.Instant; import java.util.UUID;
public record DataClassificationPolicyResponse(UUID id, UUID workspaceId, String policyCode,
        String name, String defaultClassification, boolean enabled, Instant createdAt) {
    public static DataClassificationPolicyResponse from(DataClassificationPolicy p) {
        return new DataClassificationPolicyResponse(p.id(), p.workspaceId(), p.policyCode(),
                p.name(), p.defaultClassification(), p.enabled(), p.createdAt());
    }
}
