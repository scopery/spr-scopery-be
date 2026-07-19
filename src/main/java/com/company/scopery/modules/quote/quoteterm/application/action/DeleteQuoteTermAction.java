package com.company.scopery.modules.quote.quoteterm.application.action;

import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTerm;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTermRepository;
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
public class DeleteQuoteTermAction {
    private final QuoteVersionRepository versions;
    private final QuoteTermRepository terms;
    private final QuoteAuthorizationService authorization;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public DeleteQuoteTermAction(QuoteVersionRepository versions, QuoteTermRepository terms,
                                 QuoteAuthorizationService authorization, QuotePlatformPublisher publisher,
                                 QuoteActivityLogger activityLogger) {
        this.versions = versions;
        this.terms = terms;
        this.authorization = authorization;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID projectId, UUID quoteId, UUID versionId, UUID termId) {
        authorization.requireTermDelete(projectId);
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.isEditable()) throw QuoteExceptions.versionNotDraft(version.id());
        QuoteTerm term = terms.findByIdAndVersionId(termId, versionId)
                .orElseThrow(() -> QuoteExceptions.termNotFound(termId));
        terms.delete(term);
        publisher.enqueueVersion(version, "QUOTE_TERM_DELETED");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_TERM, termId,
                QuoteActivityActions.QUOTE_TERM_DELETED, "Quote term deleted");
    }
}
