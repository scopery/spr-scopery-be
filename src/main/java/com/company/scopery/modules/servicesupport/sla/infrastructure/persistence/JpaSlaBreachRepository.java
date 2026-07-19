package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.sla.domain.model.*;
import com.company.scopery.modules.servicesupport.sla.infrastructure.mapper.SlaPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaSlaBreachRepository implements SlaBreachRepository {
    private final SpringDataSlaBreachJpaRepository spring; private final SlaPersistenceMapper mapper;
    public JpaSlaBreachRepository(SpringDataSlaBreachJpaRepository spring, SlaPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    public SlaBreach save(SlaBreach b){return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(b)));}
    public List<SlaBreach> findByWorkspaceId(UUID workspaceId){return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();}
}
