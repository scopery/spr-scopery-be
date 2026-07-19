package com.company.scopery.modules.quote.quoteline.application.action;

import com.company.scopery.modules.quote.quoteline.application.response.QuoteLineResponse;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
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
public class ReorderQuoteLinesAction {
    private final QuoteVersionRepository versions;
    private final QuoteLineRepository lines;
    private final QuoteAuthorizationService authorization;
    private final QuotePlatformPublisher publisher;

    public ReorderQuoteLinesAction(QuoteVersionRepository versions, QuoteLineRepository lines,
                                   QuoteAuthorizationService authorization, QuotePlatformPublisher publisher) {
        this.versions = versions;
        this.lines = lines;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public List<QuoteLineResponse> execute(UUID projectId, UUID quoteId, UUID versionId, List<UUID> lineIds) {
        authorization.requireLineUpdate(projectId);
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.isEditable()) throw QuoteExceptions.versionNotDraft(version.id());
        Map<UUID, QuoteLine> byId = lines.findByQuoteVersionId(versionId).stream()
                .collect(Collectors.toMap(QuoteLine::id, Function.identity()));
        List<QuoteLineResponse> result = new ArrayList<>();
        int order = 0;
        for (UUID id : lineIds) {
            QuoteLine line = byId.get(id);
            if (line == null) throw QuoteExceptions.lineNotFound(id);
            result.add(QuoteLineResponse.from(lines.save(line.withDisplayOrder(order++))));
        }
        publisher.enqueueVersion(version, "QUOTE_LINES_REORDERED");
        return result;
    }
}
