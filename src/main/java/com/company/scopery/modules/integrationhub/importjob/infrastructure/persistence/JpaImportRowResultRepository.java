package com.company.scopery.modules.integrationhub.importjob.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportRowResult;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportRowResultRepository;
import com.company.scopery.modules.integrationhub.importjob.infrastructure.mapper.ImportRowResultPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaImportRowResultRepository implements ImportRowResultRepository {
    private final SpringDataImportRowResultJpaRepository spring;
    private final ImportRowResultPersistenceMapper mapper;
    public JpaImportRowResultRepository(SpringDataImportRowResultJpaRepository spring, ImportRowResultPersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ImportRowResult save(ImportRowResult r){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(r))); }
    @Override public List<ImportRowResult> findByImportJobId(UUID importJobId){ return spring.findByImportJobIdOrderByRowNumber(importJobId).stream().map(mapper::toDomain).toList(); }
    @Override public List<ImportRowResult> findByWorkspaceIdAndImportJobId(UUID workspaceId, UUID importJobId){ return spring.findByWorkspaceIdAndImportJobIdOrderByRowNumber(workspaceId, importJobId).stream().map(mapper::toDomain).toList(); }
}
