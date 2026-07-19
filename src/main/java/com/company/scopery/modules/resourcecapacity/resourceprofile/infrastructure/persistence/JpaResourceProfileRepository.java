package com.company.scopery.modules.resourcecapacity.resourceprofile.infrastructure.persistence;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfile;
import com.company.scopery.modules.resourcecapacity.resourceprofile.domain.model.ResourceProfileRepository;
import com.company.scopery.modules.resourcecapacity.resourceprofile.infrastructure.mapper.ResourceProfilePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaResourceProfileRepository implements ResourceProfileRepository {
    private final SpringDataResourceProfileJpaRepository spring; private final ResourceProfilePersistenceMapper mapper;
    public JpaResourceProfileRepository(SpringDataResourceProfileJpaRepository spring, ResourceProfilePersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ResourceProfile save(ResourceProfile p) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(p))); }
    @Override public Optional<ResourceProfile> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ResourceProfile> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
    @Override public boolean existsByWorkspaceIdAndLinkedUserId(UUID workspaceId, UUID linkedUserId) {
        return spring.existsByWorkspaceIdAndLinkedUserId(workspaceId, linkedUserId);
    }
    @Override public Optional<ResourceProfile> findByWorkspaceIdAndLinkedUserId(UUID workspaceId, UUID linkedUserId) {
        return spring.findByWorkspaceIdAndLinkedUserId(workspaceId, linkedUserId).map(mapper::toDomain);
    }
}
