package com.company.scopery.modules.servicesupport.problem.application.response;
import com.company.scopery.modules.servicesupport.problem.domain.model.SupportProblemRecord;
import java.time.Instant; import java.util.UUID;
public record SupportProblemResponse(UUID id, UUID workspaceId, UUID projectId, String problemNumber, String title,
        String status, String rootCauseSummary, Instant resolvedAt, Instant closedAt) {
    public static SupportProblemResponse from(SupportProblemRecord d) {
        return new SupportProblemResponse(d.id(), d.workspaceId(), d.projectId(), d.problemNumber(), d.title(),
                d.status(), d.rootCauseSummary(), d.resolvedAt(), d.closedAt());
    }
}
