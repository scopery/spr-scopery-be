package com.company.scopery.modules.notification.emailrule.infrastructure.persistence;

import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRuleRepository;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRuleSearchCriteria;
import com.company.scopery.modules.notification.emailrule.infrastructure.mapper.EmailRulePersistenceMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEmailRuleRepository implements EmailRuleRepository {

    private final SpringDataEmailRuleJpaRepository springRepo;
    private final EmailRulePersistenceMapper mapper;

    public JpaEmailRuleRepository(SpringDataEmailRuleJpaRepository springRepo,
                                   EmailRulePersistenceMapper mapper) {
        this.springRepo = springRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public EmailRule save(EmailRule rule) {
        return mapper.toDomain(springRepo.saveAndFlush(mapper.toJpaEntity(rule)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailRule> findById(UUID id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailRule> findByCode(String code) {
        return springRepo.findByCode(code).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return springRepo.existsByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailRule> findAll(EmailRuleSearchCriteria criteria) {
        String scopeStr = criteria.scope() != null ? criteria.scope().name() : null;
        String statusStr = criteria.status() != null ? criteria.status().name() : null;
        List<EmailRuleJpaEntity> results = springRepo.search(
                criteria.keyword(), scopeStr, statusStr,
                criteria.workspaceId(), criteria.eventDefinitionId(), criteria.templateId());
        int from = criteria.page() * criteria.size();
        int to = Math.min(from + criteria.size(), results.size());
        if (from >= results.size()) return List.of();
        return results.subList(from, to).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAll(EmailRuleSearchCriteria criteria) {
        String scopeStr = criteria.scope() != null ? criteria.scope().name() : null;
        String statusStr = criteria.status() != null ? criteria.status().name() : null;
        return springRepo.countSearch(
                criteria.keyword(), scopeStr, statusStr,
                criteria.workspaceId(), criteria.eventDefinitionId(), criteria.templateId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailRule> findActiveSystemRulesForEvent(UUID eventDefinitionId) {
        return springRepo.findActiveSystemRulesForEvent(eventDefinitionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailRule> findActiveWorkspaceRulesForEvent(UUID eventDefinitionId, UUID workspaceId) {
        return springRepo.findActiveWorkspaceRulesForEvent(eventDefinitionId, workspaceId)
                .stream().map(mapper::toDomain).toList();
    }
}
