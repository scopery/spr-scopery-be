package com.company.scopery.modules.notification.notificationitem.infrastructure.persistence;

import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItem;
import com.company.scopery.modules.notification.notificationitem.domain.model.NotificationItemRepository;
import com.company.scopery.modules.notification.notificationitem.infrastructure.mapper.NotificationItemPersistenceMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaNotificationItemRepository implements NotificationItemRepository {

    private final SpringDataNotificationItemJpaRepository springRepo;
    private final NotificationItemPersistenceMapper mapper;

    public JpaNotificationItemRepository(SpringDataNotificationItemJpaRepository springRepo,
                                          NotificationItemPersistenceMapper mapper) {
        this.springRepo = springRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public NotificationItem save(NotificationItem item) {
        return mapper.toDomain(springRepo.saveAndFlush(mapper.toJpaEntity(item)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationItem> findById(UUID id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRecipientUserIdAndDedupKey(UUID recipientUserId, String dedupKey) {
        return springRepo.existsByRecipientUserIdAndDedupKey(recipientUserId, dedupKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationItem> findByRecipientUserId(UUID recipientUserId, boolean includeDismissed,
                                                         int page, int size) {
        return springRepo.findByRecipient(recipientUserId, includeDismissed, PageRequest.of(page, size))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRecipientUserId(UUID recipientUserId, boolean includeDismissed) {
        return springRepo.countByRecipient(recipientUserId, includeDismissed);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByRecipientUserId(UUID recipientUserId) {
        return springRepo.countUnread(recipientUserId);
    }

    @Override
    @Transactional
    public int markAllRead(UUID recipientUserId) {
        return springRepo.markAllRead(recipientUserId);
    }
}
