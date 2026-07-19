package com.company.scopery.modules.integrationhub.mapping.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.mapping.domain.model.ExternalIdMapping;
import com.company.scopery.modules.integrationhub.mapping.domain.model.ExternalIdMappingRepository;
import com.company.scopery.modules.integrationhub.mapping.infrastructure.mapper.ExternalIdMappingPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaExternalIdMappingRepository implements ExternalIdMappingRepository {
    private final SpringDataExternalIdMappingJpaRepository spring;
    private final ExternalIdMappingPersistenceMapper mapper;
    public JpaExternalIdMappingRepository(SpringDataExternalIdMappingJpaRepository spring, ExternalIdMappingPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ExternalIdMapping save(ExternalIdMapping m){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(m))); }
    @Override public Optional<ExternalIdMapping> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ExternalIdMapping> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public List<ExternalIdMapping> findByWorkspaceIdAndScoperyObjectTypeAndScoperyObjectId(UUID workspaceId, String type, UUID objectId){
        return spring.findByWorkspaceIdAndScoperyObjectTypeAndScoperyObjectId(workspaceId, type, objectId).stream().map(mapper::toDomain).toList();
    }
}
