package com.company.scopery.modules.quality.defectlink.domain.model;
import com.company.scopery.modules.quality.defectlink.domain.enums.DefectLinkType;
import java.time.Instant; import java.util.UUID;
public record DefectLink(UUID id, UUID projectId, UUID defectId, String targetType, UUID targetId, DefectLinkType linkType,
                       Instant archivedAt, UUID archivedBy, int version, Instant createdAt) {
    public static DefectLink create(UUID projectId, UUID defectId, String targetType, UUID targetId, DefectLinkType linkType) {
        return new DefectLink(UUID.randomUUID(), projectId, defectId, targetType, targetId, linkType, null, null, 0, Instant.now());
    }
    public DefectLink archive(UUID actorId) {
        return new DefectLink(id, projectId, defectId, targetType, targetId, linkType, Instant.now(), actorId, version, createdAt);
    }
}
