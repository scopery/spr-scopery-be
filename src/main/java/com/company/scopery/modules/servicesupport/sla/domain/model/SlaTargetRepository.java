package com.company.scopery.modules.servicesupport.sla.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SlaTargetRepository {
    SlaTarget save(SlaTarget target);
    Optional<SlaTarget> findById(UUID id);
    List<SlaTarget> findByWorkspaceId(UUID workspaceId);
    List<SlaTarget> findBySlaPolicyId(UUID slaPolicyId);
}
