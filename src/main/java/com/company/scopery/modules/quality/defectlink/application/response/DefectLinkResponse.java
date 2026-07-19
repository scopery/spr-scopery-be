package com.company.scopery.modules.quality.defectlink.application.response;
import com.company.scopery.modules.quality.defectlink.domain.model.DefectLink;
import java.time.Instant; import java.util.UUID;
public record DefectLinkResponse(UUID id, UUID projectId, UUID defectId, String targetType, UUID targetId, String linkType, Instant createdAt) {
    public static DefectLinkResponse from(DefectLink e) {
        return new DefectLinkResponse(e.id(), e.projectId(), e.defectId(), e.targetType(), e.targetId(), e.linkType().name(), e.createdAt());
    }
}
