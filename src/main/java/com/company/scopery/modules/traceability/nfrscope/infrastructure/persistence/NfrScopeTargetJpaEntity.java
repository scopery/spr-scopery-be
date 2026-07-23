package com.company.scopery.modules.traceability.nfrscope.infrastructure.persistence;

import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = TraceabilityTableNames.NFR_SCOPE_TARGET)
public class NfrScopeTargetJpaEntity {

    @EmbeddedId
    private NfrScopeTargetId id;

    @Column(name = "target_type", nullable = false, length = 30)
    private String targetType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    public NfrScopeTargetJpaEntity() {}

    public NfrScopeTargetId getId() { return id; }
    public void setId(NfrScopeTargetId id) { this.id = id; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
