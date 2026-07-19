package com.company.scopery.modules.servicesupport.comment.domain.model;

import java.util.List;
import java.util.UUID;

public interface SupportCommentRepository {
    SupportComment save(SupportComment comment);
    List<SupportComment> findBySupportCaseId(UUID caseId);
}
