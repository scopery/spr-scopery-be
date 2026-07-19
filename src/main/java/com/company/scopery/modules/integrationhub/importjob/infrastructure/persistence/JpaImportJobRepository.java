package com.company.scopery.modules.integrationhub.importjob.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.importjob.domain.model.*;
import com.company.scopery.modules.integrationhub.importjob.infrastructure.mapper.ImportJobPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaImportJobRepository implements ImportJobRepository {
    private final SpringDataImportJobJpaRepository spring; private final ImportJobPersistenceMapper mapper;
    public JpaImportJobRepository(SpringDataImportJobJpaRepository spring, ImportJobPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public ImportJob save(ImportJob j){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(j))); }
    @Override public Optional<ImportJob> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ImportJob> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
