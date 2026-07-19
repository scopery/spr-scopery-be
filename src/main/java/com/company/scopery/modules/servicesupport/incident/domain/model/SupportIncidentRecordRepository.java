package com.company.scopery.modules.servicesupport.incident.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SupportIncidentRecordRepository {
    SupportIncidentRecord save(SupportIncidentRecord incident);
    Optional<SupportIncidentRecord> findById(UUID id);
    List<SupportIncidentRecord> findByWorkspaceId(UUID workspaceId);
}
