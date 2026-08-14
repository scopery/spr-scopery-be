package com.company.scopery.modules.traceability.screenspecdoc.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.SCREEN_SPEC_DOCUMENT)
@Getter
@Setter
@NoArgsConstructor
public class RegistryScreenSpecDocumentJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "document_code", nullable = false, length = 100)
    private String documentCode;

    @Column(name = "document_name", nullable = false, length = 500)
    private String documentName;

    @Column(name = "project_name", length = 255)
    private String projectName;

    @Column(name = "system_name", length = 255)
    private String systemName;

    @Column(name = "phase_name", length = 255)
    private String phaseName;

    @Column(name = "language", nullable = false, length = 10)
    private String language;

    @Column(name = "overview", columnDefinition = "TEXT")
    private String overview;

    @Column(name = "figma_url", length = 1000)
    private String figmaUrl;

    @Column(nullable = false, length = 50)
    private String status;

    @Version
    private Integer version;
}
