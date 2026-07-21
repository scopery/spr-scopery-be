package com.company.scopery.modules.notification.emailtemplate.infrastructure.persistence;

import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateSearchCriteria;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateVersion;
import com.company.scopery.modules.notification.emailtemplate.infrastructure.mapper.EmailTemplatePersistenceMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEmailTemplateRepository implements EmailTemplateRepository {

    private final SpringDataEmailTemplateJpaRepository templateRepo;
    private final SpringDataEmailTemplateVersionJpaRepository versionRepo;
    private final EmailTemplatePersistenceMapper mapper;

    public JpaEmailTemplateRepository(SpringDataEmailTemplateJpaRepository templateRepo,
                                       SpringDataEmailTemplateVersionJpaRepository versionRepo,
                                       EmailTemplatePersistenceMapper mapper) {
        this.templateRepo = templateRepo;
        this.versionRepo = versionRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public EmailTemplate save(EmailTemplate template) {
        EmailTemplateJpaEntity entity = mapper.toJpaEntity(template);
        return mapper.toDomain(templateRepo.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailTemplate> findById(UUID id) {
        return templateRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailTemplate> findByCode(String code) {
        return templateRepo.findByCode(code).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return templateRepo.existsByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplate> findAll(EmailTemplateSearchCriteria criteria) {
        String scopeStr = criteria.scope() != null ? criteria.scope().name() : null;
        String statusStr = criteria.status() != null ? criteria.status().name() : null;
        List<EmailTemplateJpaEntity> page = templateRepo.search(
                criteria.keyword() != null ? criteria.keyword() : "", scopeStr, statusStr, criteria.workspaceId(), criteria.eventDefinitionId());
        int from = criteria.page() * criteria.size();
        int to = Math.min(from + criteria.size(), page.size());
        if (from >= page.size()) return List.of();
        return page.subList(from, to).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAll(EmailTemplateSearchCriteria criteria) {
        String scopeStr = criteria.scope() != null ? criteria.scope().name() : null;
        String statusStr = criteria.status() != null ? criteria.status().name() : null;
        return templateRepo.countSearch(
                criteria.keyword() != null ? criteria.keyword() : "", scopeStr, statusStr, criteria.workspaceId(), criteria.eventDefinitionId());
    }

    @Override
    @Transactional
    public EmailTemplateVersion saveVersion(EmailTemplateVersion version) {
        EmailTemplateVersionJpaEntity entity = mapper.toVersionJpaEntity(version);
        return mapper.toVersionDomain(versionRepo.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmailTemplateVersion> findVersionById(UUID versionId) {
        return versionRepo.findById(versionId).map(mapper::toVersionDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailTemplateVersion> findVersionsByTemplateId(UUID templateId) {
        return versionRepo.findByTemplateIdOrderByVersionNumberDesc(templateId)
                .stream().map(mapper::toVersionDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int countVersionsByTemplateId(UUID templateId) {
        return versionRepo.countByTemplateId(templateId);
    }
}
