package com.company.scopery.modules.quote.quoteversion.application.action;

import com.company.scopery.modules.quote.calculation.QuoteRecalculator;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTerm;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTermRepository;
import com.company.scopery.modules.quote.quoteversion.application.response.QuoteVersionResponse;
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
public class DuplicateQuoteVersionAction {
    private final QuoteVersionRepository versions;
    private final QuoteLineRepository lines;
    private final QuoteTermRepository terms;
    private final QuoteRecalculator recalculator;
    private final QuoteAuthorizationService authorization;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public DuplicateQuoteVersionAction(QuoteVersionRepository versions, QuoteLineRepository lines,
                                       QuoteTermRepository terms, QuoteRecalculator recalculator,
                                       QuoteAuthorizationService authorization,
                                       QuotePlatformPublisher publisher, QuoteActivityLogger activityLogger) {
        this.versions = versions;
        this.lines = lines;
        this.terms = terms;
        this.recalculator = recalculator;
        this.authorization = authorization;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public QuoteVersionResponse execute(UUID projectId, UUID quoteId, UUID versionId) {
        authorization.requireVersionDuplicate(projectId);
        QuoteVersion source = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!source.projectId().equals(projectId)) {
            throw QuoteExceptions.versionNotFound(versionId);
        }
        int next = versions.nextVersionNumber(quoteId);
        QuoteVersion copy = QuoteVersion.create(
                source.quoteId(), source.projectId(), source.financeScenarioId(), next,
                source.titleSnapshot(), source.clientSnapshotJson(), source.financeSnapshotJson(),
                source.currencyCode(), source.targetMarginPercent(), source.pricingMethod(),
                source.costBaseMethod(), source.discountMethod(), source.discountPercent(),
                source.discountAmount(), source.discountReason(), source.validUntil(),
                source.proposalTitle(), source.proposalNotes());
        copy = versions.save(copy);
        for (QuoteLine line : lines.findByQuoteVersionId(source.id())) {
            lines.save(QuoteLine.create(
                    copy.id(), copy.projectId(), line.sourcePhaseFinanceId(), line.sourceProjectPhaseId(),
                    line.lineType(), line.name(), line.description(), line.quantity(), line.unitPrice(),
                    line.currencyCode(), line.displayOrder(), line.clientVisible(), line.internalNote()));
        }
        for (QuoteTerm term : terms.findByQuoteVersionId(source.id())) {
            terms.save(QuoteTerm.create(
                    copy.id(), copy.projectId(), term.termType(), term.title(), term.content(),
                    term.displayOrder(), term.clientVisible()));
        }
        recalculator.recalculateAndSave(copy);
        publisher.enqueueVersion(copy, "QUOTE_VERSION_DUPLICATED");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, copy.id(),
                QuoteActivityActions.QUOTE_VERSION_DUPLICATED, "Quote version duplicated");
        return QuoteVersionResponse.from(copy);
    }
}
