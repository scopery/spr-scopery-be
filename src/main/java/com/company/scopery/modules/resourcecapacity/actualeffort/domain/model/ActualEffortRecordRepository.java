package com.company.scopery.modules.resourcecapacity.actualeffort.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface ActualEffortRecordRepository {
    ActualEffortRecord save(ActualEffortRecord r);
    Optional<ActualEffortRecord> findById(UUID id);
    List<ActualEffortRecord> findByProjectId(UUID projectId);
}
