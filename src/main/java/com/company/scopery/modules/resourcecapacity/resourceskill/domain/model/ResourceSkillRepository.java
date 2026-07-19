package com.company.scopery.modules.resourcecapacity.resourceskill.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ResourceSkillRepository {
    ResourceSkill save(ResourceSkill e);
    Optional<ResourceSkill> findById(UUID id);
    List<ResourceSkill> findByWorkspaceId(UUID workspaceId);
    boolean existsByWorkspaceIdAndSkillCode(UUID workspaceId, String code);
}
