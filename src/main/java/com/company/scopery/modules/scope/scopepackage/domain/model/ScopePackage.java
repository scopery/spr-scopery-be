package com.company.scopery.modules.scope.scopepackage.domain.model;
import com.company.scopery.modules.scope.scopepackage.domain.enums.ScopePackageStatus;
import java.time.Instant; import java.util.UUID;
public record ScopePackage(UUID id, UUID projectId, UUID workspaceId, UUID sourceQuoteVersionId, UUID sourceBaselineId,
        String code, String name, String description, ScopePackageStatus status, boolean currentFlag,
        Instant approvedAt, UUID approvedBy, Instant archivedAt, UUID archivedBy, String traceId,
        int version, Instant createdAt, Instant updatedAt) {
    public static ScopePackage create(UUID projectId, UUID workspaceId, String code, String name, String description, String traceId) {
        Instant now = Instant.now();
        return new ScopePackage(UUID.randomUUID(), projectId, workspaceId, null, null, code, name, description,
                ScopePackageStatus.DRAFT, false, null, null, null, null, traceId, 0, now, now);
    }
    public static ScopePackage importFromQuote(UUID projectId, UUID workspaceId, UUID quoteVersionId,
                                               String code, String name, String description, String traceId) {
        Instant now = Instant.now();
        return new ScopePackage(UUID.randomUUID(), projectId, workspaceId, quoteVersionId, null, code, name, description,
                ScopePackageStatus.DRAFT, false, null, null, null, null, traceId, 0, now, now);
    }
    public ScopePackage approve(UUID actorId) {
        if (status == ScopePackageStatus.ARCHIVED) throw new IllegalStateException("Cannot approve archived package");
        return copy(ScopePackageStatus.APPROVED, true, Instant.now(), actorId, archivedAt, archivedBy);
    }
    public ScopePackage markCurrent() {
        if (status != ScopePackageStatus.APPROVED) throw new IllegalStateException("Only approved packages can be marked current");
        return copy(ScopePackageStatus.CURRENT, true, approvedAt, approvedBy, archivedAt, archivedBy);
    }
    public ScopePackage archive(UUID actorId) {
        return copy(ScopePackageStatus.ARCHIVED, false, approvedAt, approvedBy, Instant.now(), actorId);
    }
    public boolean isEditable() { return status == ScopePackageStatus.DRAFT || status == ScopePackageStatus.READY; }
    private ScopePackage copy(ScopePackageStatus status, boolean currentFlag, Instant approvedAt, UUID approvedBy,
                              Instant archivedAt, UUID archivedBy) {
        return new ScopePackage(id, projectId, workspaceId, sourceQuoteVersionId, sourceBaselineId, code, name, description,
                status, currentFlag, approvedAt, approvedBy, archivedAt, archivedBy, traceId, version, createdAt, Instant.now());
    }
}
