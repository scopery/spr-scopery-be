package com.company.scopery.modules.resourcecapacity.resourceskill.application.response;
import com.company.scopery.modules.resourcecapacity.resourceskill.domain.model.ResourceSkill;
import java.time.Instant; import java.util.UUID;
public record ResourceSkillResponse(UUID id, UUID workspaceId, String skillCode, String name, String status, Instant createdAt) {
    public static ResourceSkillResponse from(ResourceSkill e) { return new ResourceSkillResponse(e.id(), e.workspaceId(), e.skillCode(), e.name(), e.status().name(), e.createdAt()); }
}
