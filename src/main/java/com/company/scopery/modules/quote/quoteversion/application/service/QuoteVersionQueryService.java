package com.company.scopery.modules.quote.quoteversion.application.service;

import com.company.scopery.modules.quote.quoteline.application.response.QuoteLineResponse;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLineRepository;
import com.company.scopery.modules.quote.quotesummary.application.response.QuoteSummaryResponse;
import com.company.scopery.modules.quote.quotesummary.domain.model.QuoteSummaryRepository;
import com.company.scopery.modules.quote.quoteterm.application.response.QuoteTermResponse;
import com.company.scopery.modules.quote.quoteterm.domain.model.QuoteTermRepository;
import com.company.scopery.modules.quote.quoteversion.application.response.QuoteVersionResponse;
import com.company.scopery.modules.quote.quoteversion.domain.model.QuoteVersionRepository;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class QuoteVersionQueryService {

    private final QuoteVersionRepository versions;
    private final QuoteLineRepository lines;
    private final QuoteTermRepository terms;
    private final QuoteSummaryRepository summaries;
    private final QuoteAuthorizationService authorization;

    public QuoteVersionQueryService(QuoteVersionRepository versions,
                                    QuoteLineRepository lines,
                                    QuoteTermRepository terms,
                                    QuoteSummaryRepository summaries,
                                    QuoteAuthorizationService authorization) {
        this.versions = versions;
        this.lines = lines;
        this.terms = terms;
        this.summaries = summaries;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<QuoteVersionResponse> list(UUID projectId, UUID quoteId) {
        authorization.requireView(projectId);
        return versions.findByQuoteId(quoteId).stream()
                .filter(v -> v.projectId().equals(projectId))
                .map(QuoteVersionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuoteVersionResponse get(UUID projectId, UUID quoteId, UUID versionId) {
        authorization.requireView(projectId);
        return versions.findByIdAndQuoteId(versionId, quoteId)
                .filter(v -> v.projectId().equals(projectId))
                .map(QuoteVersionResponse::from)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
    }

    @Transactional(readOnly = true)
    public List<QuoteLineResponse> listLines(UUID projectId, UUID quoteId, UUID versionId) {
        authorization.requireView(projectId);
        ensureVersion(projectId, quoteId, versionId);
        return lines.findByQuoteVersionId(versionId).stream().map(QuoteLineResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public QuoteLineResponse getLine(UUID projectId, UUID quoteId, UUID versionId, UUID lineId) {
        authorization.requireView(projectId);
        ensureVersion(projectId, quoteId, versionId);
        return lines.findByIdAndVersionId(lineId, versionId)
                .map(QuoteLineResponse::from)
                .orElseThrow(() -> QuoteExceptions.lineNotFound(lineId));
    }

    @Transactional(readOnly = true)
    public List<QuoteTermResponse> listTerms(UUID projectId, UUID quoteId, UUID versionId) {
        authorization.requireView(projectId);
        ensureVersion(projectId, quoteId, versionId);
        return terms.findByQuoteVersionId(versionId).stream().map(QuoteTermResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public QuoteSummaryResponse getSummary(UUID projectId, UUID quoteId, UUID versionId) {
        authorization.requireView(projectId);
        ensureVersion(projectId, quoteId, versionId);
        boolean margin = authorization.canViewMargin(projectId);
        return summaries.findByQuoteVersionId(versionId)
                .map(s -> QuoteSummaryResponse.from(s, margin))
                .orElseThrow(() -> QuoteExceptions.versionSummaryInvalid("Summary not found"));
    }

    private void ensureVersion(UUID projectId, UUID quoteId, UUID versionId) {
        versions.findByIdAndQuoteId(versionId, quoteId)
                .filter(v -> v.projectId().equals(projectId))
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
    }
}
