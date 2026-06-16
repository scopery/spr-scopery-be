package com.company.scopery.modules.workspace.organization.domain;

import java.time.Instant;
import java.util.UUID;

public record Organization(
        UUID id,
        OrganizationCode code,
        String name,
        String description,
        UUID ownerUserId,
        OrganizationStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static Organization create(String name, OrganizationCode code, String description, UUID ownerUserId) {
        Instant now = Instant.now();
        return new Organization(UUID.randomUUID(), code, name, description, ownerUserId,
                OrganizationStatus.ACTIVE, now, now);
    }

    public Organization update(String name, String description) {
        return new Organization(id, code, name, description, ownerUserId, status, createdAt, Instant.now());
    }

    public Organization activate() {
        return new Organization(id, code, name, description, ownerUserId,
                OrganizationStatus.ACTIVE, createdAt, Instant.now());
    }

    public Organization archive() {
        return new Organization(id, code, name, description, ownerUserId,
                OrganizationStatus.ARCHIVED, createdAt, Instant.now());
    }
}
