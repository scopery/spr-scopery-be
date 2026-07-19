package com.company.scopery.modules.servicesupport.incident.domain.model;
import java.util.List; import java.util.UUID;
public interface IncidentTimelineEntryRepository {
    IncidentTimelineEntry save(IncidentTimelineEntry entry);
    List<IncidentTimelineEntry> findByIncidentId(UUID incidentId);
}
