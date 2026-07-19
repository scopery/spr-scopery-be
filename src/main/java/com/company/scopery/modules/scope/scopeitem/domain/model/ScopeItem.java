package com.company.scopery.modules.scope.scopeitem.domain.model;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemStatus;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemType;
import java.time.Instant; import java.util.UUID;
public record ScopeItem(UUID id, UUID scopePackageId, UUID projectId, UUID workspaceId, UUID sourceQuoteLineId,
        UUID sourceChangeRequestId, UUID parentScopeItemId, ScopeItemType type, String code, String title,
        String description, boolean inScope, boolean outOfScope, String priority, boolean acceptanceRequired,
        ScopeItemStatus status, Integer sortOrder, Instant archivedAt, UUID archivedBy, int version,
        Instant createdAt, Instant updatedAt) {
    public static ScopeItem create(UUID scopePackageId, UUID projectId, UUID workspaceId, ScopeItemType type,
                                   String code, String title, String description, boolean inScope, boolean outOfScope,
                                   String priority, boolean acceptanceRequired, Integer sortOrder) {
        if (inScope && outOfScope) throw new IllegalArgumentException("Cannot be both in-scope and out-of-scope");
        Instant now = Instant.now();
        return new ScopeItem(UUID.randomUUID(), scopePackageId, projectId, workspaceId, null, null, null, type, code,
                title, description, inScope, outOfScope, priority, acceptanceRequired, ScopeItemStatus.DRAFT, sortOrder,
                null, null, 0, now, now);
    }
    public static ScopeItem fromQuoteLine(UUID scopePackageId, UUID projectId, UUID workspaceId, UUID quoteLineId,
                                          String title, String description, int sortOrder) {
        Instant now = Instant.now();
        return new ScopeItem(UUID.randomUUID(), scopePackageId, projectId, workspaceId, quoteLineId, null, null,
                ScopeItemType.FEATURE, null, title, description, true, false, null, true,
                ScopeItemStatus.DRAFT, sortOrder, null, null, 0, now, now);
    }
    public ScopeItem update(String title, String description, boolean inScope, boolean outOfScope, String priority,
                            boolean acceptanceRequired, Integer sortOrder) {
        if (inScope && outOfScope) throw new IllegalArgumentException("Cannot be both in-scope and out-of-scope");
        return new ScopeItem(id, scopePackageId, projectId, workspaceId, sourceQuoteLineId, sourceChangeRequestId,
                parentScopeItemId, type, code, title, description, inScope, outOfScope, priority, acceptanceRequired,
                status, sortOrder, archivedAt, archivedBy, version, createdAt, Instant.now());
    }
    public ScopeItem archive(UUID actorId) {
        return new ScopeItem(id, scopePackageId, projectId, workspaceId, sourceQuoteLineId, sourceChangeRequestId,
                parentScopeItemId, type, code, title, description, inScope, outOfScope, priority, acceptanceRequired,
                ScopeItemStatus.ARCHIVED, sortOrder, Instant.now(), actorId, version, createdAt, Instant.now());
    }
}
