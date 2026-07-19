package com.company.scopery.modules.integrationhub.importjob.domain.model;
import java.time.Instant; import java.util.UUID;
public record ImportJob(UUID id, UUID workspaceId, String jobMode, String sourceFormat, String targetObjectType, String status,
        long totalRows, long validRows, long invalidRows, long createdRows, int version, Instant createdAt, Instant updatedAt) {
    public static ImportJob create(UUID workspaceId, String mode, String format, String targetType) {
        Instant now = Instant.now();
        return new ImportJob(UUID.randomUUID(), workspaceId, mode, format, targetType, "CREATED", 0,0,0,0, 0, now, now);
    }
    public ImportJob markValidated(long total, long valid, long invalid) {
        return new ImportJob(id, workspaceId, jobMode, sourceFormat, targetObjectType, "VALIDATED", total, valid, invalid, createdRows, version, createdAt, Instant.now());
    }
    public ImportJob markDryRunCompleted() {
        if (!"VALIDATED".equals(status) && !"CREATED".equals(status)) throw new IllegalStateException("invalid");
        return new ImportJob(id, workspaceId, jobMode, sourceFormat, targetObjectType, "DRY_RUN_COMPLETED", totalRows, validRows, invalidRows, createdRows, version, createdAt, Instant.now());
    }
    public ImportJob execute(long created) {
        if (!"DRY_RUN_COMPLETED".equals(status) && !"VALIDATED".equals(status)) throw new IllegalStateException("dry-run required");
        return new ImportJob(id, workspaceId, jobMode, sourceFormat, targetObjectType, "COMPLETED", totalRows, validRows, invalidRows, created, version, createdAt, Instant.now());
    }
    public ImportJob cancel() {
        if ("COMPLETED".equals(status) || "CANCELLED".equals(status)) throw new IllegalStateException("cannot cancel from status: "+status);
        return new ImportJob(id, workspaceId, jobMode, sourceFormat, targetObjectType, "CANCELLED", totalRows, validRows, invalidRows, createdRows, version, createdAt, Instant.now());
    }
}
