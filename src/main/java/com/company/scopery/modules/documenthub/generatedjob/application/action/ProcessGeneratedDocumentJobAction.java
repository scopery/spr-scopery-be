package com.company.scopery.modules.documenthub.generatedjob.application.action;

import com.company.scopery.modules.documenthub.document.application.action.CreateDocumentAction;
import com.company.scopery.modules.documenthub.document.application.command.CreateDocumentCommand;
import com.company.scopery.modules.documenthub.document.application.response.DocumentResponse;
import com.company.scopery.modules.documenthub.generatedjob.application.response.GeneratedDocumentJobResponse;
import com.company.scopery.modules.documenthub.generatedjob.domain.enums.GeneratedDocumentJobStatus;
import com.company.scopery.modules.documenthub.generatedjob.domain.model.GeneratedDocumentJobRepository;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.template.infrastructure.persistence.DocumentTemplateVersionJpaEntity;
import com.company.scopery.modules.documenthub.template.infrastructure.persistence.SpringDataDocumentTemplateVersionJpaRepository;
import com.company.scopery.modules.documenthub.version.application.action.UploadDocumentVersionAction;
import com.company.scopery.modules.documenthub.version.application.command.UploadDocumentVersionCommand;
import com.company.scopery.modules.notification.emailtemplate.application.service.EmailTemplateRenderer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ProcessGeneratedDocumentJobAction {
    private final GeneratedDocumentJobRepository jobs;
    private final SpringDataDocumentTemplateVersionJpaRepository templateVersions;
    private final CreateDocumentAction createDocument;
    private final UploadDocumentVersionAction uploadVersion;
    private final EmailTemplateRenderer templateRenderer;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;

    public ProcessGeneratedDocumentJobAction(GeneratedDocumentJobRepository jobs,
                                             SpringDataDocumentTemplateVersionJpaRepository templateVersions,
                                             CreateDocumentAction createDocument,
                                             UploadDocumentVersionAction uploadVersion,
                                             EmailTemplateRenderer templateRenderer,
                                             DocumentHubAuthorizationService authorization,
                                             DocumentHubActivityLogger activityLogger) {
        this.jobs = jobs;
        this.templateVersions = templateVersions;
        this.createDocument = createDocument;
        this.uploadVersion = uploadVersion;
        this.templateRenderer = templateRenderer;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public GeneratedDocumentJobResponse execute(UUID projectId, UUID jobId, Map<String, Object> variables) {
        authorization.requireUpdate(projectId);
        var job = jobs.findByIdAndProjectId(jobId, projectId).orElseThrow(() -> DocumentHubExceptions.jobNotFound(jobId));
        if (job.status() != GeneratedDocumentJobStatus.QUEUED && job.status() != GeneratedDocumentJobStatus.RUNNING) {
            throw DocumentHubExceptions.immutable(jobId);
        }
        var running = jobs.save(job.markRunning());
        DocumentTemplateVersionJpaEntity templateVersion = templateVersions
                .findByIdAndTemplateId(running.templateVersionId(), running.templateId())
                .orElseThrow(() -> DocumentHubExceptions.templateNotFound(running.templateId()));
        Map<String, Object> payload = variables == null ? Map.of() : new LinkedHashMap<>(variables);
        payload.putIfAbsent("projectId", projectId);
        payload.putIfAbsent("jobId", jobId);
        String rendered = templateRenderer.render(templateVersion.getBodyTemplate(), payload);
        DocumentResponse document = createDocument.execute(new CreateDocumentCommand(
                projectId, null, "GENERATED", "gen-" + jobId, "Generated document " + jobId, null, null));
        String storageKey = "generated-inline/" + jobId;
        uploadVersion.execute(new UploadDocumentVersionCommand(
                projectId,
                document.id(),
                storageKey,
                "generated-" + jobId + ".txt",
                "text/plain",
                (long) rendered.getBytes(StandardCharsets.UTF_8).length,
                null,
                rendered));
        var saved = jobs.save(running.succeed(document.id()));
        activityLogger.logSuccess(DocumentHubEntityTypes.GENERATED_JOB, saved.id(),
                DocumentHubActivityActions.GENERATION_COMPLETED, "Generation rendered and stored");
        return GeneratedDocumentJobResponse.from(saved);
    }
}
