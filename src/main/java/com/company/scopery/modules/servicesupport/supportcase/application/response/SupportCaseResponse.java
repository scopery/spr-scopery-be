package com.company.scopery.modules.servicesupport.supportcase.application.response;
import com.company.scopery.modules.servicesupport.supportcase.domain.model.SupportCase;
import java.time.Instant; import java.util.UUID;
public record SupportCaseResponse(UUID id, UUID workspaceId, UUID projectId, String caseNumber, String requestTypeCode,
        String source, String priority, String status, String title, UUID ownerUserId,
        boolean portalVisible, Instant createdAt) {
    public static SupportCaseResponse from(SupportCase c) {
        return new SupportCaseResponse(c.id(), c.workspaceId(), c.projectId(), c.caseNumber(), c.requestTypeCode(),
                c.source(), c.priority(), c.status(), c.title(), c.ownerUserId(), c.portalVisible(), c.createdAt());
    }
}
