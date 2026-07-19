package com.company.scopery.modules.integrationhub.exportjob.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.exportjob.domain.model.*;
import com.company.scopery.modules.integrationhub.exportjob.infrastructure.mapper.ExportJobPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaExportJobRepository implements ExportJobRepository {
    private final SpringDataExportJobJpaRepository spring; private final ExportJobPersistenceMapper mapper;
    public JpaExportJobRepository(SpringDataExportJobJpaRepository spring, ExportJobPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public ExportJob save(ExportJob j){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(j))); }
    @Override public Optional<ExportJob> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ExportJob> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
}
