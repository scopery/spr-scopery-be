package com.company.scopery.modules.quote.quoteterm.application.action;

import com.company.scopery.modules.quote.quoteterm.application.response.QuoteTermResponse;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTerm;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTermRepository;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersion;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import com.company.scopery.modules.quote.shared.support.QuotePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReorderQuoteTermsAction {
    private final QuoteVersionRepository versions;
    private final QuoteTermRepository terms;
    private final QuoteAuthorizationService authorization;
    private final QuotePlatformPublisher publisher;

    public ReorderQuoteTermsAction(QuoteVersionRepository versions, QuoteTermRepository terms,
                                   QuoteAuthorizationService authorization, QuotePlatformPublisher publisher) {
        this.versions = versions;
        this.terms = terms;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public List<QuoteTermResponse> execute(UUID projectId, UUID quoteId, UUID versionId, List<UUID> termIds) {
        authorization.requireTermUpdate(projectId);
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.isEditable()) throw QuoteExceptions.versionNotDraft(version.id());
        Map<UUID, QuoteTerm> byId = terms.findByQuoteVersionId(versionId).stream()
                .collect(Collectors.toMap(QuoteTerm::id, Function.identity()));
        List<QuoteTermResponse> result = new ArrayList<>();
        int order = 0;
        for (UUID id : termIds) {
            QuoteTerm term = byId.get(id);
            if (term == null) throw QuoteExceptions.termNotFound(id);
            result.add(QuoteTermResponse.from(terms.save(term.withDisplayOrder(order++))));
        }
        publisher.enqueueVersion(version, "QUOTE_TERMS_REORDERED");
        return result;
    }
}
