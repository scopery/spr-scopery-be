package com.company.scopery.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableJpaEntity implements Persistable<Object> {

    @Setter
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * Drives Spring Data's persist-vs-merge decision.
     * createdAt is null before the first INSERT (set by @CreatedDate on flush).
     * This ensures new entities always use persist() regardless of @Version value.
     */
    @Override
    @Transient
    public boolean isNew() {
        return createdAt == null;
    }

    /**
     * Default implementation for Persistable contract.
     * Entities with a standard {@code @Id private UUID id} field override this via Lombok's @Getter.
     * Entities using non-standard @Id field names (e.g. userId, workspaceId) inherit this null default —
     * Spring Data uses the JPA metamodel (not this method) for actual persistence identity, so it is safe.
     */
    @Override
    @Transient
    public Object getId() {
        return null;
    }
}
