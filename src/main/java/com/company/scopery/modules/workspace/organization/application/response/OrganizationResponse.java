package com.company.scopery.modules.workspace.organization.application.response;

import com.company.scopery.modules.workspace.organization.domain.model.Organization;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String code,
        String name,
        String description,
        UUID ownerUserId,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static OrganizationResponse from(Organization org) {
        return new OrganizationResponse(
                org.id(),
                org.code().value(),
                org.name(),
                org.description(),
                org.ownerUserId(),
                org.status().name(),
                org.version(),
                org.createdAt(),
                org.updatedAt());
    }
}
