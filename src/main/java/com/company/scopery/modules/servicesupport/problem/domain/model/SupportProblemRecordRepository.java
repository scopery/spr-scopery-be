package com.company.scopery.modules.servicesupport.problem.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SupportProblemRecordRepository {
    SupportProblemRecord save(SupportProblemRecord problem);
    Optional<SupportProblemRecord> findById(UUID id);
    List<SupportProblemRecord> findByWorkspaceId(UUID workspaceId);
}
