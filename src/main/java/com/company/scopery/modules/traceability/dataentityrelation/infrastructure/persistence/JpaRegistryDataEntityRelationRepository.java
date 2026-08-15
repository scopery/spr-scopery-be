package com.company.scopery.modules.traceability.dataentityrelation.infrastructure.persistence;

import com.company.scopery.modules.traceability.dataentityrelation.domain.model.RegistryDataEntityRelation;
import com.company.scopery.modules.traceability.dataentityrelation.domain.model.RegistryDataEntityRelationRepository;
import com.company.scopery.modules.traceability.dataentityrelation.infrastructure.mapper.RegistryDataEntityRelationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryDataEntityRelationRepository implements RegistryDataEntityRelationRepository {

    private final SpringDataRegistryDataEntityRelationJpaRepository springData;
    private final RegistryDataEntityRelationPersistenceMapper mapper;

    public JpaRegistryDataEntityRelationRepository(SpringDataRegistryDataEntityRelationJpaRepository springData,
                                                    RegistryDataEntityRelationPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public RegistryDataEntityRelation save(RegistryDataEntityRelation relation) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(relation)));
    }

    @Override
    public Optional<RegistryDataEntityRelation> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistryDataEntityRelation> findByEntityId(UUID entityId) {
        return springData.findByEntityId(entityId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsBySourceEntityIdAndTargetEntityIdAndRelationType(UUID sourceEntityId, UUID targetEntityId, String relationType) {
        return springData.existsBySourceEntityIdAndTargetEntityIdAndRelationType(sourceEntityId, targetEntityId, relationType);
    }

    @Override
    public void deleteById(UUID id) {
        springData.deleteById(id);
    }
}
