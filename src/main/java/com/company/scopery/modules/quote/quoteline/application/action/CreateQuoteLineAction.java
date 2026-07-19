package com.company.scopery.modules.quote.quoteline.application.action;

import com.company.scopery.modules.quote.calculation.QuoteRecalculator;
import com.company.scopery.modules.quote.quoteline.application.response.QuoteLineResponse;
import com.company.scopery.modules.quote.quoteline.domain.enums.QuoteLineType;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.quote.shared.activity.QuoteActivityLogger;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.constant.QuoteActivityActions;
import com.company.scopery.modules.quote.shared.constant.QuoteEntityTypes;
import com.company.scopery.modules.quote.shared.error.QuoteErrorCatalog;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import com.company.scopery.modules.quote.shared.support.QuotePlatformPublisher;
import com.company.scopery.modules.quote.shared.util.QuoteEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreateQuoteLineAction {
    private final QuoteVersionRepository versions;
    private final QuoteLineRepository lines;
    private final QuoteRecalculator recalculator;
    private final QuoteAuthorizationService authorization;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public CreateQuoteLineAction(QuoteVersionRepository versions, QuoteLineRepository lines,
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
    public QuoteLineResponse execute(UUID projectId, UUID quoteId, UUID versionId, String lineType,
                                     String name, String description, BigDecimal quantity,
                                     BigDecimal unitPrice, Integer displayOrder, Boolean clientVisible,
                                     String internalNote, UUID sourceProjectPhaseId) {
        authorization.requireLineCreate(projectId);
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.isEditable()) throw QuoteExceptions.versionNotDraft(version.id());
        if (quantity == null || quantity.signum() <= 0) throw QuoteExceptions.lineInvalidQuantity();
        if (unitPrice == null || unitPrice.signum() < 0) throw QuoteExceptions.lineInvalidUnitPrice();
        QuoteLineType type = QuoteEnumParser.parseRequired(QuoteLineType.class, lineType,
                QuoteErrorCatalog.QUOTE_LINE_INVALID_QUANTITY.code(), "lineType");
        int order = displayOrder == null ? lines.findByQuoteVersionId(versionId).size() : displayOrder;
        QuoteLine line = lines.save(QuoteLine.create(
                version.id(), projectId, null, sourceProjectPhaseId, type, name, description,
                quantity, unitPrice, version.currencyCode(), order,
                clientVisible == null || clientVisible, internalNote));
        recalculator.recalculateAndSave(version);
        publisher.enqueueVersion(version, "QUOTE_LINE_CREATED");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_LINE, line.id(),
                QuoteActivityActions.QUOTE_LINE_CREATED, "Quote line created");
        return QuoteLineResponse.from(line);
    }
}
