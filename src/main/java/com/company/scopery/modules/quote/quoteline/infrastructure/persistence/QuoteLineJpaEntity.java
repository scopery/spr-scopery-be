package com.company.scopery.modules.quote.quoteline.infrastructure.persistence;

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
import java.util.UUID;

@Entity
@Table(name = QuoteTableNames.LINE)
@Getter
@Setter
@NoArgsConstructor
public class QuoteLineJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "quote_version_id", nullable = false)
    private UUID quoteVersionId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "source_phase_finance_id")
    private UUID sourcePhaseFinanceId;
    @Column(name = "source_project_phase_id")
    private UUID sourceProjectPhaseId;
    @Column(name = "line_type", nullable = false)
    private String lineType;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column(nullable = false)
    private BigDecimal quantity;
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
    @Column(name = "client_visible", nullable = false)
    private Boolean clientVisible;
    @Column(name = "internal_note", columnDefinition = "text")
    private String internalNote;
    @Version
    private Integer version;
}
