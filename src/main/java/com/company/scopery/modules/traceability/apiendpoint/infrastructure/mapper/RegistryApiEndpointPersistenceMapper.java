package com.company.scopery.modules.traceability.apiendpoint.infrastructure.mapper;
import com.company.scopery.modules.traceability.apiendpoint.domain.enums.RegistryApiEndpointStatus;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import com.company.scopery.modules.traceability.apiendpoint.infrastructure.persistence.RegistryApiEndpointJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class RegistryApiEndpointPersistenceMapper {
    public RegistryApiEndpoint toDomain(RegistryApiEndpointJpaEntity e) {
        return new RegistryApiEndpoint(e.getId(), e.getApplicationId(), e.getProjectId(),
                e.getMethod(), e.getPathPattern(), e.getName(),
                e.getDescription(), e.getRequestParamsJson(), e.getResponseSchemaJson(),
                e.getStatus() != null ? RegistryApiEndpointStatus.valueOf(e.getStatus()) : RegistryApiEndpointStatus.ACTIVE,
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public RegistryApiEndpointJpaEntity toJpaEntity(RegistryApiEndpoint d) {
        RegistryApiEndpointJpaEntity e = new RegistryApiEndpointJpaEntity();
        e.setId(d.id()); e.setApplicationId(d.applicationId()); e.setProjectId(d.projectId());
        e.setMethod(d.method()); e.setPathPattern(d.pathPattern()); e.setName(d.name());
        e.setDescription(d.description());
        e.setRequestParamsJson(d.requestParamsJson());
        e.setResponseSchemaJson(d.responseSchemaJson());
        e.setStatus(d.status().name());
        if (d.createdAt() != null) {
            e.setVersion(d.version());
            e.setCreatedAt(d.createdAt());
        }
        return e;
    }
}
