package com.company.scopery.modules.resourcecapacity.resourcerole.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.resourcerole.domain.model.ResourceRole;
import com.company.scopery.modules.resourcecapacity.resourcerole.domain.model.ResourceRoleRepository;
import com.company.scopery.modules.resourcecapacity.resourcerole.infrastructure.mapper.ResourceRolePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaResourceRoleRepository implements ResourceRoleRepository {
    private final SpringDataResourceRoleJpaRepository spring; private final ResourceRolePersistenceMapper mapper;
    public JpaResourceRoleRepository(SpringDataResourceRoleJpaRepository spring, ResourceRolePersistenceMapper mapper) { this.spring=spring; this.mapper=mapper; }
    @Override public ResourceRole save(ResourceRole e) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<ResourceRole> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ResourceRole> findByWorkspaceId(UUID workspaceId) { return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByWorkspaceIdAndRoleCode(UUID workspaceId, String code) { return spring.existsByWorkspaceIdAndRoleCode(workspaceId, code); }
}
