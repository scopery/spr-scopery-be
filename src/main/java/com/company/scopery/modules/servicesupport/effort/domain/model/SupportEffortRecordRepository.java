package com.company.scopery.modules.servicesupport.effort.domain.model;

import java.util.List;
import java.util.UUID;

public interface SupportEffortRecordRepository {
    SupportEffortRecord save(SupportEffortRecord record);
    java.util.Optional<SupportEffortRecord> findById(UUID id);
    List<SupportEffortRecord> findBySupportCaseId(UUID caseId);
    List<SupportEffortRecord> findByWorkspaceId(UUID workspaceId);
}
