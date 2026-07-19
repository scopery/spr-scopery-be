package com.company.scopery.modules.servicesupport.handover.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.handover.domain.model.ServiceHandoverPackage;
import com.company.scopery.modules.servicesupport.handover.domain.model.ServiceHandoverPackageRepository;
import com.company.scopery.modules.servicesupport.handover.infrastructure.mapper.ServiceHandoverPackagePersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaServiceHandoverPackageRepository implements ServiceHandoverPackageRepository {
    private final SpringDataServiceHandoverPackageJpaRepository spring;
    private final ServiceHandoverPackagePersistenceMapper mapper;
    public JpaServiceHandoverPackageRepository(SpringDataServiceHandoverPackageJpaRepository spring, ServiceHandoverPackagePersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public ServiceHandoverPackage save(ServiceHandoverPackage d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<ServiceHandoverPackage> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ServiceHandoverPackage> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
}
