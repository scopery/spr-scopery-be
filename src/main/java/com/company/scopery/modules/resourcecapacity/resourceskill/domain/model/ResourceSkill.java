package com.company.scopery.modules.resourcecapacity.resourceskill.domain.model;
import com.company.scopery.modules.resourcecapacity.resourceskill.domain.enums.ResourceSkillStatus;
import java.time.Instant; import java.util.UUID;
public record ResourceSkill(UUID id, UUID workspaceId, String skillCode, String name, String description,
        UUID defaultRateCardId, ResourceSkillStatus status, Instant archivedAt, UUID archivedBy,
        int version, Instant createdAt, Instant updatedAt) {
    public static ResourceSkill create(UUID workspaceId, String code, String name, String description, UUID rateCardId) {
        Instant now = Instant.now();
        return new ResourceSkill(UUID.randomUUID(), workspaceId, code, name, description, rateCardId,
                ResourceSkillStatus.ACTIVE, null, null, 0, now, now);
    }
    public ResourceSkill archive(UUID actor) {
        return new ResourceSkill(id, workspaceId, skillCode, name, description, defaultRateCardId,
                ResourceSkillStatus.ARCHIVED, Instant.now(), actor, version, createdAt, Instant.now());
    }
}
