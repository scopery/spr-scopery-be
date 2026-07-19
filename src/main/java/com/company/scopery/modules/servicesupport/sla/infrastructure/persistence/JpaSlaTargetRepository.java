package com.company.scopery.modules.servicesupport.sla.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaTarget;
import com.company.scopery.modules.servicesupport.sla.domain.model.SlaTargetRepository;
import com.company.scopery.modules.servicesupport.sla.infrastructure.mapper.SlaTargetPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSlaTargetRepository implements SlaTargetRepository {
    private final SpringDataSlaTargetJpaRepository spring; private final SlaTargetPersistenceMapper mapper;
    public JpaSlaTargetRepository(SpringDataSlaTargetJpaRepository spring, SlaTargetPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public SlaTarget save(SlaTarget d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<SlaTarget> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SlaTarget> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
    @Override public List<SlaTarget> findBySlaPolicyId(UUID p){ return spring.findBySlaPolicyId(p).stream().map(mapper::toDomain).toList(); }
}
