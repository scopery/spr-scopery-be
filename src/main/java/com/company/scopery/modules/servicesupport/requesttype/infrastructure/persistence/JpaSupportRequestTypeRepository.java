package com.company.scopery.modules.servicesupport.requesttype.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.requesttype.domain.model.SupportRequestType;
import com.company.scopery.modules.servicesupport.requesttype.domain.model.SupportRequestTypeRepository;
import com.company.scopery.modules.servicesupport.requesttype.infrastructure.mapper.SupportRequestTypePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaSupportRequestTypeRepository implements SupportRequestTypeRepository {
    private final SpringDataSupportRequestTypeJpaRepository spring;
    private final SupportRequestTypePersistenceMapper mapper;
    public JpaSupportRequestTypeRepository(SpringDataSupportRequestTypeJpaRepository spring, SupportRequestTypePersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public SupportRequestType save(SupportRequestType d) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<SupportRequestType> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<SupportRequestType> findByWorkspaceId(UUID w) { return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
    @Override public boolean existsByWorkspaceIdAndTypeCode(UUID w, String code) { return spring.existsByWorkspaceIdAndTypeCode(w, code); }
}
