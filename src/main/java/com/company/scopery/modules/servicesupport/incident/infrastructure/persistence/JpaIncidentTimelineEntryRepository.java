package com.company.scopery.modules.servicesupport.incident.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.incident.domain.model.IncidentTimelineEntry;
import com.company.scopery.modules.servicesupport.incident.domain.model.IncidentTimelineEntryRepository;
import com.company.scopery.modules.servicesupport.incident.infrastructure.mapper.IncidentTimelineEntryPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.UUID;
@Repository
public class JpaIncidentTimelineEntryRepository implements IncidentTimelineEntryRepository {
    private final SpringDataIncidentTimelineEntryJpaRepository spring;
    private final IncidentTimelineEntryPersistenceMapper mapper;
    public JpaIncidentTimelineEntryRepository(SpringDataIncidentTimelineEntryJpaRepository spring, IncidentTimelineEntryPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public IncidentTimelineEntry save(IncidentTimelineEntry d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public List<IncidentTimelineEntry> findByIncidentId(UUID incidentId){ return spring.findByIncidentId(incidentId).stream().map(mapper::toDomain).toList(); }
}
