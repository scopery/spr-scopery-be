package com.company.scopery.modules.integrationhub.mapping.application.response;
import com.company.scopery.modules.integrationhub.mapping.domain.model.DataMappingProfile;
import java.time.Instant; import java.util.UUID;
public record DataMappingProfileResponse(UUID id, UUID workspaceId, UUID connectionId, String mappingCode, String name,
        String targetObjectType, String sourceFormat, String status, Instant createdAt, Instant updatedAt) {
    public static DataMappingProfileResponse from(DataMappingProfile p) {
        return new DataMappingProfileResponse(p.id(), p.workspaceId(), p.connectionId(), p.mappingCode(), p.name(),
                p.targetObjectType(), p.sourceFormat(), p.status(), p.createdAt(), p.updatedAt());
    }
}
