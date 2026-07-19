package com.company.scopery.modules.quote.quoteterm.infrastructure.persistence;

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

import java.util.UUID;

@Entity
@Table(name = QuoteTableNames.TERM)
@Getter
@Setter
@NoArgsConstructor
public class QuoteTermJpaEntity extends AuditableJpaEntity {
    @Id
    private UUID id;
    @Column(name = "quote_version_id", nullable = false)
    private UUID quoteVersionId;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "term_type", nullable = false)
    private String termType;
    private String title;
    @Column(nullable = false, columnDefinition = "text")
    private String content;
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;
    @Column(name = "client_visible", nullable = false)
    private Boolean clientVisible;
    @Version
    private Integer version;
}
