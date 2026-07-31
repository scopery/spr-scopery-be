package com.company.scopery.modules.quality.nfrspecification.application.response;
import com.company.scopery.modules.quality.nfrspecification.domain.model.NfrTarget;
import java.time.Instant; import java.util.List; import java.util.UUID;
public record NfrTargetResponse(
        UUID id, UUID requirementId,
        String targetType, UUID targetId, String targetLabel,
        int displayOrder, Instant createdAt) {
    public static NfrTargetResponse from(NfrTarget e) {
        return new NfrTargetResponse(e.id(), e.requirementId(),
                e.targetType().name(), e.targetId(), e.targetLabel(),
                e.displayOrder(), e.createdAt());
    }
    public record ListResponse(UUID requirementId, List<NfrTargetResponse> targets) {}
}
