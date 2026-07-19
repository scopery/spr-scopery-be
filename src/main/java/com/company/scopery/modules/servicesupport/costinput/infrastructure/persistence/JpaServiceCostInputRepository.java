package com.company.scopery.modules.servicesupport.costinput.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.costinput.domain.model.ServiceCostInput;
import com.company.scopery.modules.servicesupport.costinput.domain.model.ServiceCostInputRepository;
import com.company.scopery.modules.servicesupport.costinput.infrastructure.mapper.ServiceCostInputPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaServiceCostInputRepository implements ServiceCostInputRepository {
    private final SpringDataServiceCostInputJpaRepository spring;
    private final ServiceCostInputPersistenceMapper mapper;
    public JpaServiceCostInputRepository(SpringDataServiceCostInputJpaRepository spring, ServiceCostInputPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public ServiceCostInput save(ServiceCostInput d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<ServiceCostInput> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ServiceCostInput> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
    @Override public List<ServiceCostInput> findBySupportCaseId(UUID c){ return spring.findBySupportCaseId(c).stream().map(mapper::toDomain).toList(); }
}
