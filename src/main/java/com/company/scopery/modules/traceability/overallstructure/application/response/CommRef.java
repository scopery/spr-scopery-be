package com.company.scopery.modules.traceability.overallstructure.application.response;

import com.company.scopery.modules.traceability.commspec.domain.model.CommunicationSpecification;

import java.util.UUID;

public record CommRef(UUID id, String code, String name, String status, String triggerKey) {
    public static CommRef from(CommunicationSpecification c) {
        return new CommRef(
                c.id(),
                c.code(),
                c.name(),
                c.status() != null ? c.status().name() : null,
                c.triggerKey());
    }
}
