package com.company.scopery.modules.servicesupport.incident.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.incident.domain.model.SupportIncidentRecord;
import com.company.scopery.modules.servicesupport.incident.domain.model.SupportIncidentRecordRepository;
import com.company.scopery.modules.servicesupport.incident.infrastructure.mapper.SupportIncidentRecordPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSupportIncidentRecordRepository implements SupportIncidentRecordRepository {
    private final SpringDataSupportIncidentRecordJpaRepository spring;
    private final SupportIncidentRecordPersistenceMapper mapper;
    public JpaSupportIncidentRecordRepository(SpringDataSupportIncidentRecordJpaRepository spring, SupportIncidentRecordPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public SupportIncidentRecord save(SupportIncidentRecord d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<SupportIncidentRecord> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportIncidentRecord> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
}
