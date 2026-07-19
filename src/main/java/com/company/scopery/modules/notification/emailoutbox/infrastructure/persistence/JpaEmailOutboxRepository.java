package com.company.scopery.modules.notification.emailoutbox.infrastructure.persistence;

import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutboxSearchCriteria;
import com.company.scopery.modules.notification.emailoutbox.infrastructure.mapper.EmailOutboxPersistenceMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEmailOutboxRepository implements EmailOutboxRepository {

    private final SpringDataEmailOutboxJpaRepository springRepo;
    private final EmailOutboxPersistenceMapper mapper;

    public JpaEmailOutboxRepository(SpringDataEmailOutboxJpaRepository springRepo,
                                     EmailOutboxPersistenceMapper mapper) {
        this.springRepo = springRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public EmailOutbox save(EmailOutbox outbox) {
        return mapper.toDomain(springRepo.saveAndFlush(mapper.toJpaEntity(outbox)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailOutbox> findById(UUID id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailOutbox> findAll(EmailOutboxSearchCriteria criteria) {
        String statusStr = criteria.status() != null ? criteria.status().name() : null;
        String providerStr = criteria.providerType() != null ? criteria.providerType().name() : null;
        List<EmailOutboxJpaEntity> results = springRepo.search(criteria.deliveryId(), statusStr, providerStr);
        int from = criteria.page() * criteria.size();
        int to = Math.min(from + criteria.size(), results.size());
        if (from >= results.size()) return List.of();
        return results.subList(from, to).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAll(EmailOutboxSearchCriteria criteria) {
        String statusStr = criteria.status() != null ? criteria.status().name() : null;
        String providerStr = criteria.providerType() != null ? criteria.providerType().name() : null;
        return springRepo.countSearch(criteria.deliveryId(), statusStr, providerStr);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailOutbox> findPendingBatch(Instant beforeScheduledAt, int limit) {
        return springRepo.findPendingBatch(beforeScheduledAt, PageRequest.of(0, limit))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public int claimForProcessing(UUID id) {
        return springRepo.claimForProcessing(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDedupKey(String dedupKey) {
        return springRepo.existsByDedupKey(dedupKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailOutbox> findByDedupKey(String dedupKey) {
        return springRepo.findByDedupKey(dedupKey).map(mapper::toDomain);
    }
}
