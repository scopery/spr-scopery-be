package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.sla.domain.model.*;
import com.company.scopery.modules.servicesupport.sla.infrastructure.mapper.SlaPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaSlaPolicyRepository implements SlaPolicyRepository {
    private final SpringDataSlaPolicyJpaRepository spring; private final SlaPersistenceMapper mapper;
    public JpaSlaPolicyRepository(SpringDataSlaPolicyJpaRepository spring, SlaPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    public SlaPolicy save(SlaPolicy p){return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(p)));}
    public Optional<SlaPolicy> findById(UUID id){return spring.findById(id).map(mapper::toDomain);}
    public List<SlaPolicy> findByWorkspaceId(UUID workspaceId){return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();}
}
