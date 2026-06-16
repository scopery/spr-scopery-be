package com.company.scopery.common.audit;

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
@Table(
    name = "app_activity_log",
    indexes = {
        @Index(name = "idx_activity_log_module_code",  columnList = "module_code"),
        @Index(name = "idx_activity_log_entity_type",  columnList = "entity_type"),
        @Index(name = "idx_activity_log_entity_id",    columnList = "entity_id"),
        @Index(name = "idx_activity_log_action",       columnList = "action"),
        @Index(name = "idx_activity_log_created_at",   columnList = "created_at"),
        @Index(name = "idx_activity_log_trace_id",     columnList = "trace_id")
    }
)
public class ActivityLogJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "module_code", nullable = false, length = 100)
    private String moduleCode;

    @Column(name = "entity_type", length = 100)
    private String entityType;

    @Column(name = "entity_id", length = 100)
    private String entityId;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "actor_id", length = 100)
    private String actorId;

    @Column(name = "actor_name", length = 255)
    private String actorName;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }
}
