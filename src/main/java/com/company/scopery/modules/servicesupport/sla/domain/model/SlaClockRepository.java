package com.company.scopery.modules.servicesupport.sla.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SlaClockRepository {
    SlaClock save(SlaClock c); Optional<SlaClock> findById(UUID id); List<SlaClock> findByWorkspaceId(UUID workspaceId);
    List<SlaClock> findBySupportCaseId(UUID caseId);
    List<SlaClock> findByStatusIn(List<String> statuses);
}
