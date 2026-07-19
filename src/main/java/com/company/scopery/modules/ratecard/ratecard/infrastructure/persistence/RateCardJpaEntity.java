package com.company.scopery.modules.ratecard.ratecard.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.ratecard.shared.constant.RateCardTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = RateCardTableNames.RATE_CARD, indexes = {
        @Index(name = "idx_rate_card_scope", columnList = "scope"),
        @Index(name = "idx_rate_card_org", columnList = "organization_id"),
        @Index(name = "idx_rate_card_workspace", columnList = "workspace_id"),
        @Index(name = "idx_rate_card_status", columnList = "status"),
        @Index(name = "idx_rate_card_code", columnList = "code")
})
public class RateCardJpaEntity extends AuditableJpaEntity {
    @Id @Column(name = "id", nullable = false, updatable = false) private UUID id;
    @Column(name = "code", nullable = false, length = 100) private String code;
    @Column(name = "name", nullable = false, length = 255) private String name;
    @Column(name = "description", columnDefinition = "TEXT") private String description;
    @Column(name = "scope", nullable = false, length = 50) private String scope;
    @Column(name = "organization_id") private UUID organizationId;
    @Column(name = "workspace_id") private UUID workspaceId;
    @Column(name = "client_id") private UUID clientId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "default_currency_code", nullable = false, length = 10) private String defaultCurrencyCode;
    @Column(name = "is_default", nullable = false) private boolean isDefault;
    @Column(name = "status", nullable = false, length = 50) private String status;
    @Column(name = "current_version_id") private UUID currentVersionId;
    @Column(name = "built_in", nullable = false) private boolean builtIn;
    @Column(name = "archived_at") private Instant archivedAt;
    @Column(name = "archived_by") private UUID archivedBy;
    @Version @Column(name = "version", nullable = false) private Integer version;
}
