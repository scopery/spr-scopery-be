package com.company.scopery.modules.resourcecapacity.resourceskill.infrastructure.mapper;
import com.company.scopery.modules.resourcecapacity.resourceskill.domain.enums.ResourceSkillStatus;
import com.company.scopery.modules.resourcecapacity.resourceskill.domain.model.ResourceSkill;
import com.company.scopery.modules.resourcecapacity.resourceskill.infrastructure.persistence.ResourceSkillJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ResourceSkillPersistenceMapper {
    public ResourceSkillJpaEntity toJpaEntity(ResourceSkill d) {
        ResourceSkillJpaEntity e = new ResourceSkillJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setSkillCode(d.skillCode());
        e.setName(d.name()); e.setDescription(d.description()); e.setDefaultRateCardId(d.defaultRateCardId());
        e.setStatus(d.status().name()); e.setArchivedAt(d.archivedAt()); e.setArchivedBy(d.archivedBy());
        e.setVersion(d.version()); e.setCreatedAt(d.createdAt()); return e;
    }
    public ResourceSkill toDomain(ResourceSkillJpaEntity e) {
        return new ResourceSkill(e.getId(), e.getWorkspaceId(), e.getSkillCode(), e.getName(), e.getDescription(),
                e.getDefaultRateCardId(), ResourceSkillStatus.valueOf(e.getStatus()), e.getArchivedAt(), e.getArchivedBy(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
