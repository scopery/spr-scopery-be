package com.company.scopery.modules.servicesupport.assignment.domain.model;

import java.util.List;
import java.util.UUID;

public interface SupportAssignmentRepository {
    SupportAssignment save(SupportAssignment assignment);
    List<SupportAssignment> findBySupportCaseId(UUID caseId);
    List<SupportAssignment> findByWorkspaceId(UUID workspaceId);
}
