package com.company.scopery.modules.notification.emailtemplate.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailTemplateRepository {

    EmailTemplate save(EmailTemplate template);

    Optional<EmailTemplate> findById(UUID id);

    Optional<EmailTemplate> findByCode(String code);

    boolean existsByCode(String code);

    List<EmailTemplate> findAll(EmailTemplateSearchCriteria criteria);

    long countAll(EmailTemplateSearchCriteria criteria);

    // Version operations

    EmailTemplateVersion saveVersion(EmailTemplateVersion version);

    Optional<EmailTemplateVersion> findVersionById(UUID versionId);

    List<EmailTemplateVersion> findVersionsByTemplateId(UUID templateId);

    int countVersionsByTemplateId(UUID templateId);
}
