package com.company.scopery.modules.integrationhub.importtemplate.infrastructure.persistence;
import com.company.scopery.modules.integrationhub.importtemplate.domain.model.ImportTemplate;
import com.company.scopery.modules.integrationhub.importtemplate.domain.model.ImportTemplateRepository;
import com.company.scopery.modules.integrationhub.importtemplate.infrastructure.mapper.ImportTemplatePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaImportTemplateRepository implements ImportTemplateRepository {
    private final SpringDataImportTemplateJpaRepository spring;
    private final ImportTemplatePersistenceMapper mapper;
    public JpaImportTemplateRepository(SpringDataImportTemplateJpaRepository spring, ImportTemplatePersistenceMapper mapper){
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ImportTemplate save(ImportTemplate t){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(t))); }
    @Override public Optional<ImportTemplate> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ImportTemplate> findByWorkspaceIdOrGlobal(UUID workspaceId){ return spring.findByWorkspaceIdOrGlobal(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public List<ImportTemplate> findAll(){ return spring.findAll().stream().map(mapper::toDomain).toList(); }
}
