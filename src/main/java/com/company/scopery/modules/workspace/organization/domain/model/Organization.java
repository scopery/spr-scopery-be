package com.company.scopery.modules.workspace.organization.domain.model;

import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.modules.workspace.organization.domain.valueobject.OrganizationCode;

import java.time.Instant;
import java.util.UUID;

public record Organization(
        UUID id,
        OrganizationCode code,
        String name,
        String description,
        UUID ownerUserId,
        OrganizationStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static Organization create(String name, OrganizationCode code, String description, UUID ownerUserId) {
        Instant now = Instant.now();
        return new Organization(UUID.randomUUID(), code, name, description, ownerUserId,
                OrganizationStatus.ACTIVE, 0, now, now);
    }

    public Organization update(String name, String description) {
        return new Organization(id, code, name, description, ownerUserId, status, version, createdAt, Instant.now());
    }

    public Organization activate() {
        return new Organization(id, code, name, description, ownerUserId,
                OrganizationStatus.ACTIVE, version, createdAt, Instant.now());
    }

    public Organization archive() {
        return new Organization(id, code, name, description, ownerUserId,
                OrganizationStatus.ARCHIVED, version, createdAt, Instant.now());
    }
}
