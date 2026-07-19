package com.company.scopery.modules.servicesupport.supportcase.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.*;
import com.company.scopery.modules.servicesupport.supportcase.infrastructure.mapper.SupportCasePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSupportCaseRepository implements SupportCaseRepository {
    private final SpringDataSupportCaseJpaRepository spring; private final SupportCasePersistenceMapper mapper;
    public JpaSupportCaseRepository(SpringDataSupportCaseJpaRepository spring, SupportCasePersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    @Override public SupportCase save(SupportCase c){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(c))); }
    @Override public Optional<SupportCase> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportCase> findByWorkspaceId(UUID workspaceId){ return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList(); }
    @Override public List<SupportCase> findByProjectId(UUID projectId){ return spring.findByProjectId(projectId).stream().map(mapper::toDomain).toList(); }
}
