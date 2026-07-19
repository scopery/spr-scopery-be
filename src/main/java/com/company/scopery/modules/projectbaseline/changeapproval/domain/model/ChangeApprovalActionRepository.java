package com.company.scopery.modules.projectbaseline.changeapproval.domain.model;

import java.util.List;
import java.util.UUID;

public interface ChangeApprovalActionRepository {
    ChangeApprovalAction save(ChangeApprovalAction action);
    List<ChangeApprovalAction> findByChangeRequestId(UUID changeRequestId);
}
