package com.company.scopery.modules.notification.emailoutbox.application;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.notification.emailoutbox.application.query.SearchEmailOutboxQuery;
import com.company.scopery.modules.notification.emailoutbox.application.response.EmailOutboxResponse;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutboxSearchCriteria;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutboxStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailProviderType;
import com.company.scopery.modules.notification.shared.NotificationEnumParser;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmailOutboxApplicationService {

    private final EmailOutboxRepository outboxRepository;
    private final EmailOutboxProcessor outboxProcessor;
    private final NotificationProperties properties;

    public EmailOutboxApplicationService(EmailOutboxRepository outboxRepository,
                                          EmailOutboxProcessor outboxProcessor,
                                          NotificationProperties properties) {
        this.outboxRepository = outboxRepository;
        this.outboxProcessor = outboxProcessor;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public EmailOutboxResponse getOutbox(UUID id) {
        return EmailOutboxResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmailOutboxResponse> searchOutbox(SearchEmailOutboxQuery query) {
        EmailOutboxStatus status = query.status() != null
                ? NotificationEnumParser.parseOutboxStatus(query.status()) : null;
        EmailProviderType providerType = query.providerType() != null
                ? NotificationEnumParser.parseProviderType(query.providerType()) : null;

        EmailOutboxSearchCriteria criteria = new EmailOutboxSearchCriteria(
                query.deliveryId(), status, providerType, query.page(), query.size());

        List<EmailOutboxResponse> items = outboxRepository.findAll(criteria)
                .stream().map(EmailOutboxResponse::from).toList();
        long total = outboxRepository.countAll(criteria);
        int totalPages = query.size() == 0 ? 1 : (int) Math.ceil((double) total / query.size());
        return new PageResponse<>(items, query.page(), query.size(), total, totalPages,
                query.page() == 0, query.page() >= totalPages - 1);
    }

    @Transactional
    public EmailOutboxResponse retryOutbox(UUID id) {
        EmailOutbox outbox = findOrThrow(id);
        int maxRetry = properties.getOutbox().getMaxRetry();

        if (outbox.status() != EmailOutboxStatus.FAILED) {
            throw NotificationExceptions.emailOutboxNotRetryable(id, outbox.status().name());
        }
        if (outbox.retryCount() >= maxRetry) {
            throw NotificationExceptions.emailOutboxMaxRetryReached(id, maxRetry);
        }

        outbox.scheduleRetry(0);
        outbox = outboxRepository.save(outbox);
        outboxProcessor.processOne(outbox);
        return EmailOutboxResponse.from(findOrThrow(id));
    }

    private EmailOutbox findOrThrow(UUID id) {
        return outboxRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailOutboxNotFound(id));
    }
}
