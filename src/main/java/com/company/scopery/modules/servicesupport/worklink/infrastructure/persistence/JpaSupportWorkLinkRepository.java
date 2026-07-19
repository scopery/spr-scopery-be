package com.company.scopery.modules.servicesupport.worklink.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.worklink.domain.model.SupportWorkLink;
import com.company.scopery.modules.servicesupport.worklink.domain.model.SupportWorkLinkRepository;
import com.company.scopery.modules.servicesupport.worklink.infrastructure.mapper.SupportWorkLinkPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSupportWorkLinkRepository implements SupportWorkLinkRepository {
    private final SpringDataSupportWorkLinkJpaRepository spring;
    private final SupportWorkLinkPersistenceMapper mapper;
    public JpaSupportWorkLinkRepository(SpringDataSupportWorkLinkJpaRepository spring, SupportWorkLinkPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public SupportWorkLink save(SupportWorkLink d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<SupportWorkLink> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportWorkLink> findBySupportCaseId(UUID c){ return spring.findBySupportCaseId(c).stream().map(mapper::toDomain).toList(); }
    @Override public List<SupportWorkLink> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
}
