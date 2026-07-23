package com.company.scopery.modules.traceability.overallstructure.application.response;

import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;

import java.util.UUID;

public record ApiRef(UUID id, String method, String pathPattern, String name) {
    public static ApiRef from(RegistryApiEndpoint e) {
        return new ApiRef(e.id(), e.method(), e.pathPattern(), e.name());
    }
}
