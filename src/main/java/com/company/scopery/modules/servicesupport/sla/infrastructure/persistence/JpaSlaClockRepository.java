package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.sla.domain.model.*;
import com.company.scopery.modules.servicesupport.sla.infrastructure.mapper.SlaPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaSlaClockRepository implements SlaClockRepository {
    private final SpringDataSlaClockJpaRepository spring; private final SlaPersistenceMapper mapper;
    public JpaSlaClockRepository(SpringDataSlaClockJpaRepository spring, SlaPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    public SlaClock save(SlaClock c){return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(c)));}
    public Optional<SlaClock> findById(UUID id){return spring.findById(id).map(mapper::toDomain);}
    public List<SlaClock> findByWorkspaceId(UUID workspaceId){return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();}
    public List<SlaClock> findBySupportCaseId(UUID caseId){return spring.findBySupportCaseId(caseId).stream().map(mapper::toDomain).toList();}
    public List<SlaClock> findByStatusIn(List<String> statuses){return spring.findByStatusIn(statuses).stream().map(mapper::toDomain).toList();}
}
