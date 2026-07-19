package com.company.scopery.modules.servicesupport.sla.domain.model;
import java.util.List; import java.util.UUID;
public interface SlaBreachRepository {
    SlaBreach save(SlaBreach b); List<SlaBreach> findByWorkspaceId(UUID workspaceId);
}
