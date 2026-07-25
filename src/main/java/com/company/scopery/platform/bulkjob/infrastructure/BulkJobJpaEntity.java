package com.company.scopery.platform.bulkjob.infrastructure;

import com.company.scopery.common.audit.AuditableJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_bulk_job")
@Getter
@Setter
public class BulkJobJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "job_type", nullable = false, length = 100)
    private String jobType;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "actor_username")
    private String actorUsername;

    @Column(name = "total_items", nullable = false)
    private int totalItems;

    @Column(name = "succeeded_items", nullable = false)
    private int succeededItems;

    @Column(name = "failed_items", nullable = false)
    private int failedItems;

    @Column(name = "payload_json", nullable = false, columnDefinition = "text")
    private String payloadJson;

    @Column(name = "result_summary", columnDefinition = "text")
    private String resultSummary;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "leased_by", length = 100)
    private String leasedBy;

    @Column(name = "leased_at")
    private Instant leasedAt;

    @Column(name = "lease_until")
    private Instant leaseUntil;

    @Override
    public Object getId() {
        return id;
    }
}
