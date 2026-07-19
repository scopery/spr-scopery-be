package com.company.scopery.modules.integrationhub.mapping.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.mapping.domain.model.DataMappingProfile;
import com.company.scopery.modules.integrationhub.mapping.domain.model.DataMappingProfileRepository;
import com.company.scopery.modules.integrationhub.mapping.infrastructure.mapper.DataMappingProfilePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaDataMappingProfileRepository implements DataMappingProfileRepository {
    private final SpringDataDataMappingProfileJpaRepository spring;
    private final DataMappingProfilePersistenceMapper mapper;
    public JpaDataMappingProfileRepository(SpringDataDataMappingProfileJpaRepository spring, DataMappingProfilePersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public DataMappingProfile save(DataMappingProfile p){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(p))); }
    @Override public Optional<DataMappingProfile> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public Optional<DataMappingProfile> findByWorkspaceIdAndMappingCode(UUID workspaceId, String code){ return spring.findByWorkspaceIdAndMappingCode(workspaceId, code).map(mapper::toDomain); }
    @Override public List<DataMappingProfile> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByWorkspaceIdAndMappingCode(UUID workspaceId, String code){ return spring.existsByWorkspaceIdAndMappingCode(workspaceId, code); }
}
