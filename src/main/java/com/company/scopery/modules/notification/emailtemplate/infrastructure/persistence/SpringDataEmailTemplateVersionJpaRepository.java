package com.company.scopery.modules.notification.emailtemplate.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataEmailTemplateVersionJpaRepository extends JpaRepository<EmailTemplateVersionJpaEntity, UUID> {

    List<EmailTemplateVersionJpaEntity> findByTemplateIdOrderByVersionNumberDesc(UUID templateId);

    @Query("SELECT COUNT(v) FROM EmailTemplateVersionJpaEntity v WHERE v.templateId = :templateId")
    int countByTemplateId(@Param("templateId") UUID templateId);
}
