package com.company.scopery.modules.quote.quote.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quote.quote.application.response.QuoteResponse;
import com.company.scopery.modules.quote.quote.domain.model.Quote;
import com.company.scopery.modules.quote.quote.domain.model.QuoteRepository;
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
public class ArchiveQuoteAction {

    private final ProjectRepository projects;
    private final QuoteRepository quotes;
    private final QuoteAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public ArchiveQuoteAction(ProjectRepository projects,
                              QuoteRepository quotes,
                              QuoteAuthorizationService authorization,
                              CurrentUserAuthorizationService currentUser,
                              QuotePlatformPublisher publisher,
                              QuoteActivityLogger activityLogger) {
        this.projects = projects;
        this.quotes = quotes;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public QuoteResponse execute(UUID projectId, UUID quoteId) {
        authorization.requireArchive(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        Quote quote = quotes.findByIdAndProjectId(quoteId, projectId)
                .orElseThrow(() -> QuoteExceptions.quoteNotFound(quoteId));
        quote = quotes.save(quote.archive(actorId));
        if (quoteId.equals(project.currentQuoteId())) {
            projects.save(project.withCurrentQuoteIds(null, null));
        }
        publisher.enqueueQuote(quote, "QUOTE_ARCHIVED");
        publisher.auditQuote(AuditEventType.QUOTE_ARCHIVED, actorId, project, quote, "Quote archived");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE, quote.id(),
                QuoteActivityActions.QUOTE_ARCHIVED, "Quote archived");
        return QuoteResponse.from(quote);
    }
}
