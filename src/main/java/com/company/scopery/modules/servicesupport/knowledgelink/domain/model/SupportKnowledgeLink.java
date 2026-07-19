package com.company.scopery.modules.servicesupport.knowledgelink.domain.model;
import java.time.Instant; import java.util.UUID;
public record SupportKnowledgeLink(UUID id, UUID workspaceId, UUID supportCaseId, UUID problemId, UUID incidentId,
        UUID documentId, UUID documentVersionId, String linkType, boolean clientVisible,
        int version, Instant createdAt, Instant updatedAt) {
    public static SupportKnowledgeLink create(UUID workspaceId, UUID supportCaseId, UUID documentId, String linkType, boolean clientVisible) {
        Instant now = Instant.now();
        return new SupportKnowledgeLink(UUID.randomUUID(), workspaceId, supportCaseId, null, null,
                documentId, null, linkType, clientVisible, 0, now, now);
    }
}
