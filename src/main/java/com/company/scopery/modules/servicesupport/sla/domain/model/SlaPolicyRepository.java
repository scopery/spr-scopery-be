package com.company.scopery.modules.servicesupport.sla.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SlaPolicyRepository {
    SlaPolicy save(SlaPolicy p); Optional<SlaPolicy> findById(UUID id); List<SlaPolicy> findByWorkspaceId(UUID workspaceId);
}
