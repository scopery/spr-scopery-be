package com.company.scopery.modules.quote.quote.application.action;

import com.company.scopery.modules.quote.quote.application.command.UpdateQuoteCommand;
import com.company.scopery.modules.quote.quote.application.response.QuoteResponse;
import com.company.scopery.modules.quote.quote.domain.model.Quote;
import com.company.scopery.modules.quote.quote.domain.model.QuoteRepository;
import com.company.scopery.modules.quote.shared.activity.QuoteActivityLogger;
import com.company.scopery.modules.quote.shared.authorization.QuoteAuthorizationService;
import com.company.scopery.modules.quote.shared.constant.QuoteActivityActions;
import com.company.scopery.modules.quote.shared.constant.QuoteEntityTypes;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import com.company.scopery.modules.quote.shared.support.QuotePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateQuoteAction {

    private final QuoteRepository quotes;
    private final QuoteAuthorizationService authorization;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public UpdateQuoteAction(QuoteRepository quotes,
                             QuoteAuthorizationService authorization,
                             QuotePlatformPublisher publisher,
                             QuoteActivityLogger activityLogger) {
        this.quotes = quotes;
        this.authorization = authorization;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public QuoteResponse execute(UpdateQuoteCommand command) {
        authorization.requireUpdate(command.projectId());
        Quote quote = quotes.findByIdAndProjectId(command.quoteId(), command.projectId())
                .orElseThrow(() -> QuoteExceptions.quoteNotFound(command.quoteId()));
        if (quote.isArchived()) {
            throw QuoteExceptions.versionImmutable(quote.id());
        }
        quote = quotes.save(quote.update(
                command.title(), command.description(), command.clientName(),
                command.clientCompany(), command.clientEmail(),
                command.clientContactName(), command.clientReference()));
        publisher.enqueueQuote(quote, "QUOTE_UPDATED");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE, quote.id(),
                QuoteActivityActions.QUOTE_UPDATED, "Quote updated");
        return QuoteResponse.from(quote);
    }
}
