package com.company.scopery.modules.notification.emailtemplate.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.notification.shared.NotificationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = NotificationTableNames.EMAIL_TEMPLATE_VERSION)
@Getter
@Setter
@NoArgsConstructor
public class EmailTemplateVersionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "template_id", nullable = false)
    private UUID templateId;

    @Column(name = "version_number", nullable = false)
    private int versionNumber;

    @Column(name = "subject_template", nullable = false, columnDefinition = "TEXT")
    private String subjectTemplate;

    @Column(name = "html_body_template", nullable = false, columnDefinition = "TEXT")
    private String htmlBodyTemplate;

    @Column(name = "text_body_template", columnDefinition = "TEXT")
    private String textBodyTemplate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
