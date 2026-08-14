package com.company.scopery.modules.traceability.screenprocessitem.infrastructure.persistence;

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
@Table(name = TraceabilityTableNames.SCREEN_PROCESS_ITEM)
@Getter
@Setter
@NoArgsConstructor
public class RegistryScreenProcessItemJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "screen_id", nullable = false)
    private UUID screenId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "mode_id")
    private UUID modeId;

    @Column(name = "target_field_id")
    private UUID targetFieldId;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "source_table")
    private String sourceTable;

    @Column(name = "condition_note", columnDefinition = "text")
    private String conditionNote;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private String status;

    @Version
    private Integer version;
}
