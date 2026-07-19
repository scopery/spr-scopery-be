package com.company.scopery.modules.quote.quote.application.service;

import com.company.scopery.modules.quote.quote.application.response.QuoteResponse;
import com.company.scopery.modules.quote.quote.domain.model.QuoteRepository;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class QuoteQueryService {

    private final QuoteRepository quotes;
    private final QuoteAuthorizationService authorization;

    public QuoteQueryService(QuoteRepository quotes, QuoteAuthorizationService authorization) {
        this.quotes = quotes;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> list(UUID projectId) {
        authorization.requireView(projectId);
        return quotes.findByProjectId(projectId).stream().map(QuoteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public QuoteResponse get(UUID projectId, UUID quoteId) {
        authorization.requireView(projectId);
        return quotes.findByIdAndProjectId(quoteId, projectId)
                .map(QuoteResponse::from)
                .orElseThrow(() -> QuoteExceptions.quoteNotFound(quoteId));
    }
}
