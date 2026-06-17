package com.company.scopery.modules.notification.emaildelivery.infrastructure.persistence;

import com.company.scopery.modules.notification.emaildelivery.domain.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryRepository;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliverySearchCriteria;
import com.company.scopery.modules.notification.emaildelivery.infrastructure.mapper.EmailDeliveryPersistenceMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEmailDeliveryRepository implements EmailDeliveryRepository {

    private final SpringDataEmailDeliveryJpaRepository springRepo;
    private final EmailDeliveryPersistenceMapper mapper;

    public JpaEmailDeliveryRepository(SpringDataEmailDeliveryJpaRepository springRepo,
                                       EmailDeliveryPersistenceMapper mapper) {
        this.springRepo = springRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public EmailDelivery save(EmailDelivery delivery) {
        return mapper.toDomain(springRepo.saveAndFlush(mapper.toJpaEntity(delivery)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailDelivery> findById(UUID id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailDelivery> findAll(EmailDeliverySearchCriteria criteria) {
        String statusStr = criteria.status() != null ? criteria.status().name() : null;
        List<EmailDeliveryJpaEntity> results = springRepo.search(
                criteria.ruleId(), criteria.templateId(),
                criteria.eventDefinitionId(), criteria.workspaceId(), statusStr);
        int from = criteria.page() * criteria.size();
        int to = Math.min(from + criteria.size(), results.size());
        if (from >= results.size()) return List.of();
        return results.subList(from, to).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAll(EmailDeliverySearchCriteria criteria) {
        String statusStr = criteria.status() != null ? criteria.status().name() : null;
        return springRepo.countSearch(
                criteria.ruleId(), criteria.templateId(),
                criteria.eventDefinitionId(), criteria.workspaceId(), statusStr);
    }
}
