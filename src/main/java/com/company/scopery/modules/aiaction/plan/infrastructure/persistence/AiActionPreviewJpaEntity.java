package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import com.company.scopery.modules.aiaction.shared.constant.AiActionTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = AiActionTableNames.PREVIEW)
public class AiActionPreviewJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "plan_id", nullable = false, unique = true)
    private UUID planId;

    @Column(name = "preview_hash", length = 200)
    private String previewHash;

    @Column(name = "masked_diff", columnDefinition = "TEXT")
    private String maskedDiffJson;

    @Column(name = "warnings", columnDefinition = "TEXT")
    private String warningsJson;

    @Column(name = "baseline_impact", length = 100)
    private String baselineImpact;

    @Column(name = "external_side_effect", nullable = false)
    private boolean externalSideEffect;

    @Column(name = "valid_until")
    private Instant validUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
