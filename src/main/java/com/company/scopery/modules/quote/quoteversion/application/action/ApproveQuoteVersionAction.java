package com.company.scopery.modules.quote.quoteversion.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.quote.calculation.QuoteCalculationService;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummaryRepository;
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
import com.company.scopery.modules.profitability.shared.event.ProfitabilityRebuildRequestedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ApproveQuoteVersionAction {
    private final ProjectRepository projects;
    private final QuoteVersionRepository versions;
    private final QuoteLineRepository lines;
    private final QuoteSummaryRepository summaries;
    private final QuoteCalculationService calculationService;
    private final QuoteAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;
    private final ApplicationEventPublisher events;

    public ApproveQuoteVersionAction(ProjectRepository projects, QuoteVersionRepository versions,
                        QuoteLineRepository lines, QuoteSummaryRepository summaries,
                        QuoteCalculationService calculationService,
                        QuoteAuthorizationService authorization,
                        CurrentUserAuthorizationService currentUser,
                        QuotePlatformPublisher publisher, QuoteActivityLogger activityLogger,
                        ApplicationEventPublisher events) {
        this.projects = projects;
        this.versions = versions;
        this.lines = lines;
        this.summaries = summaries;
        this.calculationService = calculationService;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
        this.events = events;
    }

    @Transactional
    public QuoteVersionResponse execute(UUID projectId, UUID quoteId, UUID versionId) {
        authorization.requireApprove(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.projectId().equals(projectId)) {
            throw QuoteExceptions.versionNotFound(versionId);
        }
        if (version.status() != QuoteVersionStatus.SUBMITTED) throw QuoteExceptions.versionInvalidStatus(version.id(), "approve");
        var summary = summaries.findByQuoteVersionId(version.id())
                .orElseThrow(() -> QuoteExceptions.versionSummaryInvalid("Summary missing"));
        if (calculationService.requiresDiscountApproval(version.discountMethod(), version.discountPercent(),
                version.discountAmount(), summary.subtotalBeforeDiscount())) {
            authorization.requireDiscountApprove(projectId);
        }
        version = versions.save(version.approve(actorId));
        publisher.enqueueVersion(version, "QUOTE_APPROVED");
        publisher.audit(AuditEventType.QUOTE_APPROVED, actorId, project, version, "ApproveQuoteVersionAction");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, version.id(),
                QuoteActivityActions.QUOTE_APPROVED, "QUOTE_APPROVED");
        events.publishEvent(new ProfitabilityRebuildRequestedEvent(projectId, "QUOTE_APPROVED"));
        return QuoteVersionResponse.from(version);
    }
}
