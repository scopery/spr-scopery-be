package com.company.scopery.modules.notification.emailoutbox.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailOutboxRepository {

    EmailOutbox save(EmailOutbox outbox);

    Optional<EmailOutbox> findById(UUID id);

    List<EmailOutbox> findAll(EmailOutboxSearchCriteria criteria);

    long countAll(EmailOutboxSearchCriteria criteria);

    List<EmailOutbox> findPendingBatch(Instant beforeScheduledAt, int limit);
}
