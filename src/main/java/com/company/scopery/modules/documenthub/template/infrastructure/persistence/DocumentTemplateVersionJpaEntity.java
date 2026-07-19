package com.company.scopery.modules.documenthub.template.infrastructure.persistence;

import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.TEMPLATE_VERSION)
@Getter
@Setter
@NoArgsConstructor
public class DocumentTemplateVersionJpaEntity {
    @Id
    private UUID id;
    @Column(name = "template_id", nullable = false)
    private UUID templateId;
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;
    @Column(name = "body_template", columnDefinition = "text", nullable = false)
    private String bodyTemplate;
    @Column(name = "output_format", nullable = false)
    private String outputFormat;
    @Column(nullable = false)
    private String status;
    @Column(columnDefinition = "jsonb")
    private String ast;
}
