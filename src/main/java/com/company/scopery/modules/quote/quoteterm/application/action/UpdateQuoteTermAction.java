package com.company.scopery.modules.quote.quoteterm.application.action;

import com.company.scopery.modules.quote.quoteterm.application.response.QuoteTermResponse;
import com.company.scopery.modules.quote.quoteterm.domain.enums.QuoteTermType;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTerm;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTermRepository;
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

import java.util.UUID;

@Component
public class UpdateQuoteTermAction {
    private final QuoteVersionRepository versions;
    private final QuoteTermRepository terms;
    private final QuoteAuthorizationService authorization;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public UpdateQuoteTermAction(QuoteVersionRepository versions, QuoteTermRepository terms,
                                 QuoteAuthorizationService authorization, QuotePlatformPublisher publisher,
                                 QuoteActivityLogger activityLogger) {
        this.versions = versions;
        this.terms = terms;
        this.authorization = authorization;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public QuoteTermResponse execute(UUID projectId, UUID quoteId, UUID versionId, UUID termId,
                                     String termType, String title, String content,
                                     Integer displayOrder, Boolean clientVisible) {
        authorization.requireTermUpdate(projectId);
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.isEditable()) throw QuoteExceptions.versionNotDraft(version.id());
        QuoteTerm term = terms.findByIdAndVersionId(termId, versionId)
                .orElseThrow(() -> QuoteExceptions.termNotFound(termId));
        if (content != null && content.isBlank()) throw QuoteExceptions.termContentRequired();
        QuoteTermType type = termType == null || termType.isBlank() ? null
                : QuoteEnumParser.parseRequired(QuoteTermType.class, termType,
                QuoteErrorCatalog.QUOTE_TERM_CONTENT_REQUIRED.code(), "termType");
        term = terms.save(term.update(type, title, content, displayOrder, clientVisible));
        publisher.enqueueVersion(version, "QUOTE_TERM_UPDATED");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_TERM, term.id(),
                QuoteActivityActions.QUOTE_TERM_UPDATED, "Quote term updated");
        return QuoteTermResponse.from(term);
    }
}
