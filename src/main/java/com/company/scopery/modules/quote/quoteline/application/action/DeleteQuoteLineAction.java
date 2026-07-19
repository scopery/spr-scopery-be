package com.company.scopery.modules.quote.quoteline.application.action;

import com.company.scopery.modules.quote.calculation.QuoteRecalculator;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
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
public class DeleteQuoteLineAction {
    private final QuoteVersionRepository versions;
    private final QuoteLineRepository lines;
    private final QuoteRecalculator recalculator;
    private final QuoteAuthorizationService authorization;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public DeleteQuoteLineAction(QuoteVersionRepository versions, QuoteLineRepository lines,
                                 QuoteRecalculator recalculator, QuoteAuthorizationService authorization,
                                 QuotePlatformPublisher publisher, QuoteActivityLogger activityLogger) {
        this.versions = versions;
        this.lines = lines;
        this.recalculator = recalculator;
        this.authorization = authorization;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID projectId, UUID quoteId, UUID versionId, UUID lineId) {
        authorization.requireLineDelete(projectId);
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.isEditable()) throw QuoteExceptions.versionNotDraft(version.id());
        QuoteLine line = lines.findByIdAndVersionId(lineId, versionId)
                .orElseThrow(() -> QuoteExceptions.lineNotFound(lineId));
        lines.delete(line);
        recalculator.recalculateAndSave(version);
        publisher.enqueueVersion(version, "QUOTE_LINE_DELETED");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_LINE, lineId,
                QuoteActivityActions.QUOTE_LINE_DELETED, "Quote line deleted");
    }
}
