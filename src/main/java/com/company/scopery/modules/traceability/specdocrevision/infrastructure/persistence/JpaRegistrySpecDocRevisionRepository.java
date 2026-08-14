package com.company.scopery.modules.traceability.specdocrevision.infrastructure.persistence;

import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevision;
import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevisionRepository;
import com.company.scopery.modules.traceability.specdocrevision.infrastructure.mapper.RegistrySpecDocRevisionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistrySpecDocRevisionRepository implements RegistrySpecDocRevisionRepository {

    private final SpringDataRegistrySpecDocRevisionJpaRepository springData;
    private final RegistrySpecDocRevisionPersistenceMapper mapper;

    public JpaRegistrySpecDocRevisionRepository(SpringDataRegistrySpecDocRevisionJpaRepository springData,
                                                RegistrySpecDocRevisionPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<RegistrySpecDocRevision> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public List<RegistrySpecDocRevision> findByDocumentIdOrderByDisplayOrderAsc(UUID documentId) {
        return springData.findByDocumentIdOrderByDisplayOrderAsc(documentId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public RegistrySpecDocRevision save(RegistrySpecDocRevision revision) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(revision)));
    }

    @Override
    public void delete(UUID id) {
        springData.deleteById(id);
    }
}
