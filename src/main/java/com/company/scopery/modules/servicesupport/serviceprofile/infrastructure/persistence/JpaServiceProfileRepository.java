package com.company.scopery.modules.servicesupport.serviceprofile.infrastructure.persistence;

import com.company.scopery.modules.servicesupport.serviceprofile.domain.model.ServiceProfile;
import com.company.scopery.modules.servicesupport.serviceprofile.domain.model.ServiceProfileRepository;
import com.company.scopery.modules.servicesupport.serviceprofile.infrastructure.mapper.ServiceProfilePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaServiceProfileRepository implements ServiceProfileRepository {
    private final SpringDataServiceProfileJpaRepository spring;
    private final ServiceProfilePersistenceMapper mapper;
    public JpaServiceProfileRepository(SpringDataServiceProfileJpaRepository spring, ServiceProfilePersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }
    @Override public ServiceProfile save(ServiceProfile p) { return mapper.toDomain(spring.saveAndFlush(mapper.toJpa(p))); }
    @Override public Optional<ServiceProfile> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ServiceProfile> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
