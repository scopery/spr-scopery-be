package com.company.scopery.modules.quote.quoteversion.application.action;

import com.company.scopery.modules.quote.calculation.QuoteRecalculator;
import com.company.scopery.modules.quote.quotesummary.application.response.QuoteSummaryResponse;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummary;
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
public class RecalculateQuoteVersionAction {
    private final QuoteVersionRepository versions;
    private final QuoteRecalculator recalculator;
    private final QuoteAuthorizationService authorization;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public RecalculateQuoteVersionAction(QuoteVersionRepository versions, QuoteRecalculator recalculator,
                                         QuoteAuthorizationService authorization,
                                         QuotePlatformPublisher publisher, QuoteActivityLogger activityLogger) {
        this.versions = versions;
        this.recalculator = recalculator;
        this.authorization = authorization;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public QuoteSummaryResponse execute(UUID projectId, UUID quoteId, UUID versionId) {
        authorization.requireRecalculate(projectId);
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.isEditable()) {
            throw QuoteExceptions.versionNotDraft(version.id());
        }
        QuoteSummary summary = recalculator.recalculateAndSave(version);
        publisher.enqueueVersion(version, "QUOTE_RECALCULATED");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, version.id(),
                QuoteActivityActions.QUOTE_RECALCULATED, "Quote recalculated");
        boolean margin = authorization.canViewMargin(projectId);
        return QuoteSummaryResponse.from(summary, margin);
    }
}
