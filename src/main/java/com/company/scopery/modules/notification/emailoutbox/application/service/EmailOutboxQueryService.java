package com.company.scopery.modules.notification.emailoutbox.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailoutbox.application.query.SearchEmailOutboxQuery;
import com.company.scopery.modules.notification.emailoutbox.application.response.EmailOutboxResponse;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutboxSearchCriteria;
import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailOutboxStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailProviderType;
import com.company.scopery.modules.notification.shared.NotificationEnumParser;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmailOutboxQueryService {

    private final EmailOutboxRepository outboxRepository;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public EmailOutboxQueryService(EmailOutboxRepository outboxRepository,
                                    IamSystemAuthorizationService systemAuthorizationService) {
        this.outboxRepository = outboxRepository;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    @Transactional(readOnly = true)
    public EmailOutboxResponse getOutbox(UUID id) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_NOTIFICATION_VIEW_DELIVERY.legacyRightCode());
        return EmailOutboxResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmailOutboxResponse> searchOutbox(SearchEmailOutboxQuery query) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_NOTIFICATION_VIEW_DELIVERY.legacyRightCode());
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

    private EmailOutbox findOrThrow(UUID id) {
        return outboxRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailOutboxNotFound(id));
    }
}
