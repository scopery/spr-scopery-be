package com.company.scopery.modules.quote.quoteversion.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quote.quoteversion.application.response.QuoteVersionResponse;
import com.company.scopery.modules.quote.quoteversion.domain.enums.QuoteVersionStatus;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.quote.shared.activity.QuoteActivityLogger;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.constant.QuoteActivityActions;
import com.company.scopery.modules.quote.shared.constant.QuoteEntityTypes;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import com.company.scopery.modules.quote.shared.support.QuotePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RejectQuoteVersionAction {
    private final ProjectRepository projects;
    private final QuoteVersionRepository versions;
    private final QuoteAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public RejectQuoteVersionAction(ProjectRepository projects, QuoteVersionRepository versions,
                                    QuoteAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser,
                                    QuotePlatformPublisher publisher, QuoteActivityLogger activityLogger) {
        this.projects = projects;
        this.versions = versions;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public QuoteVersionResponse execute(UUID projectId, UUID quoteId, UUID versionId, String reason) {
        authorization.requireReject(projectId);
        if (reason == null || reason.isBlank()) {
            throw QuoteExceptions.rejectionReasonRequired();
        }
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (version.status() != QuoteVersionStatus.SUBMITTED) {
            throw QuoteExceptions.versionInvalidStatus(version.id(), "reject");
        }
        version = versions.save(version.reject(actorId, reason.trim()));
        publisher.enqueueVersion(version, "QUOTE_REJECTED");
        publisher.audit(AuditEventType.QUOTE_REJECTED, actorId, project, version, "Quote rejected");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, version.id(),
                QuoteActivityActions.QUOTE_REJECTED, "Quote rejected");
        return QuoteVersionResponse.from(version);
    }
}
