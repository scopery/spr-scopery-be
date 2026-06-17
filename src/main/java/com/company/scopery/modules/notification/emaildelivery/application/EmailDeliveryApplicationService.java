package com.company.scopery.modules.notification.emaildelivery.application;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.notification.emaildelivery.application.query.SearchEmailDeliveriesQuery;
import com.company.scopery.modules.notification.emaildelivery.application.response.EmailDeliveryResponse;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryRepository;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliverySearchCriteria;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryStatus;
import com.company.scopery.modules.notification.shared.NotificationEnumParser;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmailDeliveryApplicationService {

    private final EmailDeliveryRepository deliveryRepository;

    public EmailDeliveryApplicationService(EmailDeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional(readOnly = true)
    public EmailDeliveryResponse getDelivery(UUID id) {
        return EmailDeliveryResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmailDeliveryResponse> searchDeliveries(SearchEmailDeliveriesQuery query) {
        EmailDeliveryStatus status = query.status() != null
                ? NotificationEnumParser.parseDeliveryStatus(query.status()) : null;

        EmailDeliverySearchCriteria criteria = new EmailDeliverySearchCriteria(
                query.ruleId(), query.templateId(), query.eventDefinitionId(),
                query.workspaceId(), status, query.page(), query.size());

        List<EmailDeliveryResponse> items = deliveryRepository.findAll(criteria)
                .stream().map(EmailDeliveryResponse::from).toList();
        long total = deliveryRepository.countAll(criteria);
        int totalPages = query.size() == 0 ? 1 : (int) Math.ceil((double) total / query.size());
        return new PageResponse<>(items, query.page(), query.size(), total, totalPages,
                query.page() == 0, query.page() >= totalPages - 1);
    }

    private EmailDelivery findOrThrow(UUID id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailDeliveryNotFound(id));
    }
}
