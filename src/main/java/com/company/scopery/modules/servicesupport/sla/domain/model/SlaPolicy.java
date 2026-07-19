package com.company.scopery.modules.servicesupport.sla.domain.model;

import java.time.Instant;
import java.util.UUID;

/** Operational SLA tracking policy — not a legal enforcement instrument. */
public record SlaPolicy(
        UUID id,
        UUID workspaceId,
        String policyCode,
        String name,
        Integer firstResponseMinutes,
        Integer resolveMinutes,
        boolean businessHoursOnly,
        String status,
        int version,
        Instant createdAt) {

    public static SlaPolicy create(
            UUID workspaceId, String code, String name, Integer firstResponse, Integer resolve) {
        return new SlaPolicy(
                UUID.randomUUID(),
                workspaceId,
                code,
                name,
                firstResponse,
                resolve,
                true,
                "ACTIVE",
                0,
                Instant.now());
    }
}
