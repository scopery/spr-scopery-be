package com.company.scopery.modules.aiaction.realtime.infrastructure.persistence;

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
@Table(name = AiActionTableNames.EXECUTION_EVENT)
public class AiActionExecutionEventJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "execution_id", nullable = false)
    private UUID executionId;

    @Column(name = "sequence", nullable = false)
    private long sequence;

    @Column(name = "execution_version", nullable = false)
    private int executionVersion;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "redis_published_at")
    private Instant redisPublishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
