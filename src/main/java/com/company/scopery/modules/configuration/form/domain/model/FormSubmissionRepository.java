package com.company.scopery.modules.configuration.form.domain.model;
import java.util.*; import java.util.UUID;
public interface FormSubmissionRepository {
    FormSubmission save(FormSubmission s);
    Optional<FormSubmission> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<FormSubmission> findByWorkspaceId(UUID workspaceId);
}
