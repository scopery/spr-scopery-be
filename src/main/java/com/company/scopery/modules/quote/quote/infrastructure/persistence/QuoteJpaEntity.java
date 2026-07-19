package com.company.scopery.modules.quote.quote.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.quote.shared.constant.QuoteTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = QuoteTableNames.QUOTE)
@Getter
@Setter
@NoArgsConstructor
public class QuoteJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;
    @Column(name = "source_finance_scenario_id", nullable = false)
    private UUID sourceFinanceScenarioId;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "text")
    private String description;
    @Column(name = "client_name")
    private String clientName;
    @Column(name = "client_company")
    private String clientCompany;
    @Column(name = "client_email")
    private String clientEmail;
    @Column(name = "client_contact_name")
    private String clientContactName;
    @Column(name = "client_reference")
    private String clientReference;
    @Column(name = "external_party_id")
    private UUID externalPartyId;
    @Column(nullable = false)
    private String status;
    @Column(name = "current_version_id")
    private UUID currentVersionId;
    @Column(name = "archived_at")
    private Instant archivedAt;
    @Column(name = "archived_by")
    private UUID archivedBy;
    @Version
    private Integer version;
}
