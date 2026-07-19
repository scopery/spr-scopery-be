package com.company.scopery.modules.servicesupport.maintenance.infrastructure.persistence;
import com.company.scopery.modules.servicesupport.maintenance.domain.model.*;
import com.company.scopery.modules.servicesupport.maintenance.infrastructure.mapper.MaintenancePlanPersistenceMapper;
import org.springframework.stereotype.Repository; import java.util.*;
@Repository
public class JpaMaintenancePlanRepository implements MaintenancePlanRepository {
    private final SpringDataMaintenancePlanJpaRepository spring; private final MaintenancePlanPersistenceMapper mapper;
    public JpaMaintenancePlanRepository(SpringDataMaintenancePlanJpaRepository spring, MaintenancePlanPersistenceMapper mapper){this.spring=spring;this.mapper=mapper;}
    public MaintenancePlan save(MaintenancePlan p){return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(p)));}
    public Optional<MaintenancePlan> findById(UUID id){return spring.findById(id).map(mapper::toDomain);}
    public List<MaintenancePlan> findByWorkspaceId(UUID workspaceId){return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();}
}
