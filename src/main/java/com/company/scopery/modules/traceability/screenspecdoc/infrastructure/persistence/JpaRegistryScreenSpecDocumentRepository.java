package com.company.scopery.modules.traceability.screenspecdoc.infrastructure.persistence;

import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocument;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocumentRepository;
import com.company.scopery.modules.traceability.screenspecdoc.infrastructure.mapper.RegistryScreenSpecDocumentPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRegistryScreenSpecDocumentRepository implements RegistryScreenSpecDocumentRepository {

    private final SpringDataRegistryScreenSpecDocumentJpaRepository springData;
    private final RegistryScreenSpecDocumentPersistenceMapper mapper;

    public JpaRegistryScreenSpecDocumentRepository(
            SpringDataRegistryScreenSpecDocumentJpaRepository springData,
            RegistryScreenSpecDocumentPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public Optional<RegistryScreenSpecDocument> findByIdAndWorkspaceId(UUID id, UUID workspaceId) {
        return springData.findByIdAndWorkspaceId(id, workspaceId).map(mapper::toDomain);
    }

    @Override
    public Optional<RegistryScreenSpecDocument> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProjectIdAndDocumentCode(UUID projectId, String documentCode) {
        return springData.existsByProjectIdAndDocumentCode(projectId, documentCode);
    }

    @Override
    public List<RegistryScreenSpecDocument> findByProjectIdOrderByCreatedAtDesc(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<RegistryScreenSpecDocument> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId) {
        return springData.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public RegistryScreenSpecDocument save(RegistryScreenSpecDocument doc) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(doc)));
    }

    @Override
    public void delete(UUID id) {
        springData.deleteById(id);
    }
}
