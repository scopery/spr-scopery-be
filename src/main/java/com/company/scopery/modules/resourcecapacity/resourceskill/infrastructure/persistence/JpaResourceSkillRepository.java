package com.company.scopery.modules.resourcecapacity.resourceskill.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.resourceskill.domain.model.ResourceSkill;
import com.company.scopery.modules.resourcecapacity.resourceskill.domain.model.ResourceSkillRepository;
import com.company.scopery.modules.resourcecapacity.resourceskill.infrastructure.mapper.ResourceSkillPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaResourceSkillRepository implements ResourceSkillRepository {
    private final SpringDataResourceSkillJpaRepository spring; private final ResourceSkillPersistenceMapper mapper;
    public JpaResourceSkillRepository(SpringDataResourceSkillJpaRepository spring, ResourceSkillPersistenceMapper mapper) { this.spring=spring; this.mapper=mapper; }
    @Override public ResourceSkill save(ResourceSkill e) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ResourceSkill> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ResourceSkill> findByWorkspaceId(UUID workspaceId) { return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByWorkspaceIdAndSkillCode(UUID workspaceId, String code) { return spring.existsByWorkspaceIdAndSkillCode(workspaceId, code); }
}
