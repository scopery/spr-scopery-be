package com.company.scopery.modules.documenthub.generatedjob.application.action;
import com.company.scopery.modules.documenthub.generatedjob.application.response.GeneratedDocumentJobResponse;
import com.company.scopery.modules.documenthub.generatedjob.domain.enums.GeneratedDocumentJobStatus;
import com.company.scopery.modules.documenthub.generatedjob.domain.model.GeneratedDocumentJobRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CompleteGeneratedDocumentJobAction {
    private final GeneratedDocumentJobRepository repo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    public CompleteGeneratedDocumentJobAction(GeneratedDocumentJobRepository repo, DocumentHubAuthorizationService authorization, DocumentHubActivityLogger activityLogger) {
        this.repo=repo; this.authorization=authorization; this.activityLogger=activityLogger;
    }
    @Transactional
    public GeneratedDocumentJobResponse execute(UUID projectId, UUID id, UUID outputDocumentId) {
        authorization.requireUpdate(projectId);
        var e = repo.findByIdAndProjectId(id, projectId).orElseThrow(() -> DocumentHubExceptions.jobNotFound(id));
        if (e.status() != GeneratedDocumentJobStatus.QUEUED && e.status() != GeneratedDocumentJobStatus.RUNNING)
            throw DocumentHubExceptions.immutable(id);
        var saved = repo.save(e.succeed(outputDocumentId));
        activityLogger.logSuccess(DocumentHubEntityTypes.GENERATED_JOB, saved.id(), DocumentHubActivityActions.GENERATION_COMPLETED, "Generation completed");
        return GeneratedDocumentJobResponse.from(saved);
    }
}
