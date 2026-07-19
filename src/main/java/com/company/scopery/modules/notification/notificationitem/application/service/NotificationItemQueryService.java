package com.company.scopery.modules.notification.notificationitem.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.notification.notificationitem.application.response.NotificationItemResponse;
import com.company.scopery.modules.notification.notificationitem.application.response.UnreadCountResponse;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationItemQueryService {

    private final NotificationItemRepository repository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public NotificationItemQueryService(NotificationItemRepository repository,
                                         CurrentUserAuthorizationService currentUserAuthorizationService) {
        this.repository = repository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationItemResponse> listMine(int page, int size, boolean includeDismissed) {
        UUID userId = currentUserAuthorizationService.resolveCurrentUser().id();
        List<NotificationItem> items = repository.findByRecipientUserId(userId, includeDismissed, page, size);
        long total = repository.countByRecipientUserId(userId, includeDismissed);
        List<NotificationItemResponse> responses = items.stream().map(NotificationItemResponse::from).toList();
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PageResponse<>(responses, page, size, total, totalPages, page == 0, page >= totalPages - 1);
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse unreadCount() {
        UUID userId = currentUserAuthorizationService.resolveCurrentUser().id();
        return new UnreadCountResponse(repository.countUnreadByRecipientUserId(userId));
    }

    @Transactional(readOnly = true)
    public NotificationItemResponse getMine(UUID id) {
        UUID userId = currentUserAuthorizationService.resolveCurrentUser().id();
        NotificationItem item = repository.findById(id)
                .orElseThrow(() -> NotificationExceptions.notificationItemNotFound(id));
        if (!item.recipientUserId().equals(userId)) {
            throw NotificationExceptions.notificationItemNotFound(id);
        }
        return NotificationItemResponse.from(item);
    }
}
