package com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceWindow;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.MaintenanceWindowRepository;
import com.company.scopery.modules.servicesupport.maintenance.infrastructure.mapper.MaintenanceWindowPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public class JpaMaintenanceWindowRepository implements MaintenanceWindowRepository {
    private final SpringDataMaintenanceWindowJpaRepository spring;
    private final MaintenanceWindowPersistenceMapper mapper;
    public JpaMaintenanceWindowRepository(SpringDataMaintenanceWindowJpaRepository spring, MaintenanceWindowPersistenceMapper mapper){
        this.spring=spring; this.mapper=mapper;
    }
    @Override public MaintenanceWindow save(MaintenanceWindow d){ return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(d))); }
    @Override public Optional<MaintenanceWindow> findById(UUID id){ return spring.findById(id).map(mapper::toDomain); }
    @Override public List<MaintenanceWindow> findByWorkspaceId(UUID w){ return spring.findByWorkspaceId(w).stream().map(mapper::toDomain).toList(); }
}
