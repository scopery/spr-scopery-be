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
@Table(name = AiActionTableNames.CONFIRMATION)
public class AiActionConfirmationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "plan_version", nullable = false)
    private int planVersion;

    @Column(name = "plan_hash", length = 200)
    private String planHash;

    @Column(name = "confirmed_by", nullable = false)
    private UUID confirmedByUserId;

    @Column(name = "decision", nullable = false, length = 20)
    private String decision;

    @Column(name = "channel", length = 50)
    private String channel;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "confirmation_hash", length = 200)
    private String confirmationHash;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
