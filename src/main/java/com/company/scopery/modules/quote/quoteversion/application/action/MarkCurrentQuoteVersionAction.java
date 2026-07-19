package com.company.scopery.modules.quote.quoteversion.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quote.quote.domain.model.Quote;
import com.company.scopery.modules.quote.quote.domain.model.QuoteRepository;
import com.company.scopery.modules.quote.quoteversion.application.response.QuoteVersionResponse;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.quote.shared.activity.QuoteActivityLogger;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.constant.QuoteActivityActions;
import com.company.scopery.modules.quote.shared.constant.QuoteEntityTypes;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import com.company.scopery.modules.quote.shared.support.QuotePlatformPublisher;
import com.company.scopery.modules.profitability.shared.event.ProfitabilityRebuildRequestedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class MarkCurrentQuoteVersionAction {
    private final ProjectRepository projects;
    private final QuoteRepository quotes;
    private final QuoteVersionRepository versions;
    private final QuoteAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;
    private final ApplicationEventPublisher events;

    public MarkCurrentQuoteVersionAction(ProjectRepository projects, QuoteRepository quotes,
                                         QuoteVersionRepository versions,
                                         QuoteAuthorizationService authorization,
                                         CurrentUserAuthorizationService currentUser,
                                         QuotePlatformPublisher publisher, QuoteActivityLogger activityLogger,
                                         ApplicationEventPublisher events) {
        this.projects = projects;
        this.quotes = quotes;
        this.versions = versions;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
        this.events = events;
    }

    @Transactional
    public QuoteVersionResponse execute(UUID projectId, UUID quoteId, UUID versionId) {
        authorization.requireMarkCurrent(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        Quote quote = quotes.findByIdAndProjectId(quoteId, projectId)
                .orElseThrow(() -> QuoteExceptions.quoteNotFound(quoteId));
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));

        for (QuoteVersion current : versions.findCurrentFlagged(quoteId)) {
            if (!current.id().equals(version.id())) {
                versions.save(current.withCurrentFlag(false));
            }
        }
        version = versions.save(version.withCurrentFlag(true));
        quotes.save(quote.withCurrentVersionId(version.id()));
        projects.save(project.withCurrentQuoteIds(quote.id(), version.id()));

        publisher.enqueueVersion(version, "QUOTE_VERSION_MARKED_CURRENT");
        publisher.audit(AuditEventType.QUOTE_MARKED_CURRENT, actorId, project, version, "Quote marked current");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, version.id(),
                QuoteActivityActions.QUOTE_MARKED_CURRENT, "Quote version marked current");
        events.publishEvent(new ProfitabilityRebuildRequestedEvent(projectId, "QUOTE_MARKED_CURRENT"));
        return QuoteVersionResponse.from(version);
    }
}
