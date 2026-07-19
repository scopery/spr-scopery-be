package com.company.scopery.modules.quote.quoteversion.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
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
public class ArchiveQuoteVersionAction {
    private final QuoteVersionRepository versions;
    private final QuoteAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final QuotePlatformPublisher publisher;
    private final QuoteActivityLogger activityLogger;

    public ArchiveQuoteVersionAction(QuoteVersionRepository versions,
                                     QuoteAuthorizationService authorization,
                                     CurrentUserAuthorizationService currentUser,
                                     QuotePlatformPublisher publisher, QuoteActivityLogger activityLogger) {
        this.versions = versions;
        this.authorization = authorization;
        this.currentUser = currentUser;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public QuoteVersionResponse execute(UUID projectId, UUID quoteId, UUID versionId) {
        authorization.requireVersionArchive(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        QuoteVersion version = versions.findByIdAndQuoteId(versionId, quoteId)
                .orElseThrow(() -> QuoteExceptions.versionNotFound(versionId));
        if (!version.projectId().equals(projectId)) {
            throw QuoteExceptions.versionNotFound(versionId);
        }
        version = versions.save(version.archive(actorId));
        publisher.enqueueVersion(version, "QUOTE_VERSION_ARCHIVED");
        activityLogger.logSuccess(QuoteEntityTypes.QUOTE_VERSION, version.id(),
                QuoteActivityActions.QUOTE_VERSION_ARCHIVED, "Quote version archived");
        return QuoteVersionResponse.from(version);
    }
}
