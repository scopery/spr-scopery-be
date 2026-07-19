package com.company.scopery.modules.documenthub.generatedjob.infrastructure.persistence;
import com.company.scopery.modules.documenthub.generatedjob.domain.model.*;
import com.company.scopery.modules.documenthub.generatedjob.infrastructure.mapper.GeneratedDocumentJobPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaGeneratedDocumentJobRepository implements GeneratedDocumentJobRepository {
    private final SpringDataGeneratedDocumentJobJpaRepository springData;
    private final GeneratedDocumentJobPersistenceMapper mapper;
    public JpaGeneratedDocumentJobRepository(SpringDataGeneratedDocumentJobJpaRepository springData, GeneratedDocumentJobPersistenceMapper mapper) {
        this.springData=springData; this.mapper=mapper;
    }
    @Override public GeneratedDocumentJob save(GeneratedDocumentJob e) { return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(e))); }
    @Override public Optional<GeneratedDocumentJob> findByIdAndProjectId(UUID id, UUID projectId) {
        return springData.findByIdAndProjectId(id, projectId).map(mapper::toDomain);
    }
    @Override public List<GeneratedDocumentJob> findByProjectId(UUID projectId) {
        return springData.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(mapper::toDomain).toList();
    }
}
