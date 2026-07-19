package com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceActivity;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceActivityRepository;
import com.company.scopery.modules.servicesupport.maintenance.infrastructure.mapper.MaintenanceActivityPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaMaintenanceActivityRepository implements MaintenanceActivityRepository {
    private final SpringDataMaintenanceActivityJpaRepository spring;
    private final MaintenanceActivityPersistenceMapper mapper;
    public JpaMaintenanceActivityRepository(SpringDataMaintenanceActivityJpaRepository spring, MaintenanceActivityPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public MaintenanceActivity save(MaintenanceActivity d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<MaintenanceActivity> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<MaintenanceActivity> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
    @Override public List<MaintenanceActivity> findByMaintenancePlanId(UUID p){ return spring.findByMaintenancePlanId(p).stream().map(mapper::toDomain).toList(); }
}
