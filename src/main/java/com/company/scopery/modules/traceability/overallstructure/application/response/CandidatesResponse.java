package com.company.scopery.modules.traceability.overallstructure.application.response;

import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.screen.domain.model.RegistryScreen;

import java.util.List;
import java.util.UUID;

public record CandidatesResponse(UUID applicationId,
                                  List<ScreenCandidate> screens,
                                  List<ApiCandidate> apiEndpoints,
                                  List<ComponentCandidate> components) {

    public record ScreenCandidate(UUID id, String code, String name, String routePath) {
        public static ScreenCandidate from(RegistryScreen s) {
            return new ScreenCandidate(s.id(), s.code(), s.name(), s.routePath());
        }
    }

    public record ApiCandidate(UUID id, String method, String pathPattern, String name) {
        public static ApiCandidate from(RegistryApiEndpoint e) {
            return new ApiCandidate(e.id(), e.method(), e.pathPattern(), e.name());
        }
    }

    public record ComponentCandidate(UUID id, String code, String name, String componentType) {
        public static ComponentCandidate from(RegistryAppComponent c) {
            return new ComponentCandidate(c.id(), c.code(), c.name(), c.componentType());
        }
    }
}
