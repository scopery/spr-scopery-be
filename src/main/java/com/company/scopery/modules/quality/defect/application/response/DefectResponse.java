package com.company.scopery.modules.quality.defect.application.response;
import com.company.scopery.modules.quality.defect.domain.model.Defect;
import java.time.Instant; import java.util.UUID;
public record DefectResponse(UUID id, UUID projectId, String code, String title, String category, String severity,
        String priority, String status, UUID assignedToUserId, Instant createdAt) {
    public static DefectResponse from(Defect d) {
        return new DefectResponse(d.id(), d.projectId(), d.code(), d.title(), d.category().name(), d.severity().name(),
                d.priority().name(), d.status().name(), d.assignedToUserId(), d.createdAt());
    }
}
