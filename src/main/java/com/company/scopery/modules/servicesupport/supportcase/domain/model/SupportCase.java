package com.company.scopery.modules.servicesupport.supportcase.domain.model;
import java.time.Instant; import java.util.UUID;
public record SupportCase(UUID id, UUID workspaceId, UUID projectId, String caseNumber, String requestTypeCode, String source,
        String priority, String status, String title, String description, UUID ownerUserId, boolean portalVisible,
        int version, Instant createdAt, Instant updatedAt) {
    public static SupportCase create(UUID workspaceId, UUID projectId, String type, String priority, String title, String source, boolean portalVisible) {
        Instant now = Instant.now();
        String num = "SC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new SupportCase(UUID.randomUUID(), workspaceId, projectId, num, type, source, priority, "NEW", title, null, null, portalVisible, 0, now, now);
    }
    public SupportCase triage(UUID ownerUserId) {
        if (!"NEW".equals(status)) throw new IllegalStateException("Only NEW can triage");
        return new SupportCase(id, workspaceId, projectId, caseNumber, requestTypeCode, source, priority, "TRIAGED", title, description, ownerUserId, portalVisible, version, createdAt, Instant.now());
    }
    public SupportCase startProgress() {
        if (!"TRIAGED".equals(status) && !"WAITING_FOR_CLIENT".equals(status) && !"WAITING_INTERNAL".equals(status)) throw new IllegalStateException("invalid");
        return withStatus("IN_PROGRESS");
    }
    public SupportCase resolve() {
        if (!"IN_PROGRESS".equals(status) && !"WAITING_FOR_CLIENT".equals(status)) throw new IllegalStateException("invalid");
        return withStatus("RESOLVED");
    }
    public SupportCase close() {
        if (!"RESOLVED".equals(status)) throw new IllegalStateException("Must resolve before close");
        return withStatus("CLOSED");
    }
    private SupportCase withStatus(String s) {
        return new SupportCase(id, workspaceId, projectId, caseNumber, requestTypeCode, source, priority, s, title, description, ownerUserId, portalVisible, version, createdAt, Instant.now());
    }
}
