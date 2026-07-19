package com.company.scopery.modules.raid.raiditem.application.response;
import com.company.scopery.modules.raid.raiditem.domain.model.RaidItem;
import java.time.Instant; import java.util.UUID;
public record RaidItemResponse(UUID id, UUID projectId, String type, String code, String title, String description,
        String status, UUID ownerUserId, String severity, String probability, String impact, Integer riskScore,
        String riskScoreFormulaVersion, String riskResponseStrategy, String escalationLevel, UUID linkedChangeRequestId,
        Instant createdAt, Instant updatedAt) {
    public static RaidItemResponse from(RaidItem i) {
        return new RaidItemResponse(i.id(), i.projectId(), i.type().name(), i.code(), i.title(), i.description(),
                i.status().name(), i.ownerUserId(), i.severity(),
                i.probability()==null?null:i.probability().name(), i.impact()==null?null:i.impact().name(),
                i.riskScore(), i.riskScoreFormulaVersion(),
                i.riskResponseStrategy()==null?null:i.riskResponseStrategy().name(),
                i.escalationLevel()==null?null:i.escalationLevel().name(), i.linkedChangeRequestId(),
                i.createdAt(), i.updatedAt());
    }
}
