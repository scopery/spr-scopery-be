package com.company.scopery.modules.quality.nfrspecification.domain.model;
import com.company.scopery.modules.quality.nfrspecification.domain.enums.NfrTargetType;
import java.time.Instant; import java.util.UUID;
public record NfrTarget(
        UUID id, UUID requirementId,
        NfrTargetType targetType,
        UUID targetId, String targetLabel,
        int displayOrder, Instant createdAt) {

    public static NfrTarget create(UUID requirementId, NfrTargetType targetType,
            UUID targetId, String targetLabel, int displayOrder) {
        return new NfrTarget(UUID.randomUUID(), requirementId, targetType,
                targetId, targetLabel, displayOrder, Instant.now());
    }
}
