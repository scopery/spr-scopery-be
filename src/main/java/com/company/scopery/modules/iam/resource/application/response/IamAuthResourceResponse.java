package com.company.scopery.modules.iam.resource.application.response;

import com.company.scopery.modules.iam.resource.domain.IamAuthResource;

import java.time.Instant;
import java.util.UUID;

public record IamAuthResourceResponse(
        UUID id,
        String code,
        String resourceType,
        String name,
        String description,
        UUID refId,
        UUID ownerUserId,
        UUID workspaceId,
        String visibility,
        UUID parentResourceId,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static IamAuthResourceResponse from(IamAuthResource r) {
        return new IamAuthResourceResponse(
                r.id(), r.code().value(), r.resourceType().name(),
                r.name(), r.description(),
                r.refId(), r.ownerUserId(), r.workspaceId(),
                r.visibility() != null ? r.visibility().name() : null,
                r.parentResourceId(),
                r.status().name(),
                r.createdAt(), r.updatedAt());
    }
}
