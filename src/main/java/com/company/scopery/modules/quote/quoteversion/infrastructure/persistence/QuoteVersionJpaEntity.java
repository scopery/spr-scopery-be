package com.company.scopery.modules.quote.quoteversion.infrastructure.persistence;

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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = QuoteTableNames.VERSION)
@Getter
@Setter
@NoArgsConstructor
public class QuoteVersionJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "quote_id", nullable = false)
    private UUID quoteId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "finance_scenario_id", nullable = false)
    private UUID financeScenarioId;
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;
    @Column(nullable = false)
    private String status;
    @Column(name = "title_snapshot", nullable = false)
    private String titleSnapshot;
    @Column(name = "client_snapshot_json", columnDefinition = "jsonb")
    private String clientSnapshotJson;
    @Column(name = "finance_snapshot_json", nullable = false, columnDefinition = "jsonb")
    private String financeSnapshotJson;
    @Column(name = "formula_version", nullable = false)
    private String formulaVersion;
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    @Column(name = "target_margin_percent")
    private BigDecimal targetMarginPercent;
    @Column(name = "pricing_method", nullable = false)
    private String pricingMethod;
    @Column(name = "cost_base_method", nullable = false)
    private String costBaseMethod;
    @Column(name = "current_flag", nullable = false)
    private Boolean currentFlag;
    @Column(name = "discount_method", nullable = false)
    private String discountMethod;
    @Column(name = "discount_percent")
    private BigDecimal discountPercent;
    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;
    @Column(name = "discount_reason", columnDefinition = "text")
    private String discountReason;
    @Column(name = "valid_until")
    private LocalDate validUntil;
    @Column(name = "proposal_title")
    private String proposalTitle;
    @Column(name = "proposal_notes", columnDefinition = "text")
    private String proposalNotes;
    @Column(name = "submitted_at")
    private Instant submittedAt;
    @Column(name = "submitted_by")
    private UUID submittedBy;
    @Column(name = "approved_at")
    private Instant approvedAt;
    @Column(name = "approved_by")
    private UUID approvedBy;
    @Column(name = "rejected_at")
    private Instant rejectedAt;
    @Column(name = "rejected_by")
    private UUID rejectedBy;
    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;
    @Column(name = "sent_at")
    private Instant sentAt;
    @Column(name = "sent_by")
    private UUID sentBy;
    @Column(name = "accepted_at")
    private Instant acceptedAt;
    @Column(name = "accepted_by")
    private UUID acceptedBy;
    @Column(name = "archived_at")
    private Instant archivedAt;
    @Column(name = "archived_by")
    private UUID archivedBy;
    @Version
    private Integer version;
}
