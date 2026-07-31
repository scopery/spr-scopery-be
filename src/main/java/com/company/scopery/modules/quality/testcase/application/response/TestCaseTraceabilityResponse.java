package com.company.scopery.modules.quality.testcase.application.response;
import java.util.List; import java.util.UUID;
public record TestCaseTraceabilityResponse(
        List<TraceLink> requirements,
        List<TraceLink> useCases,
        List<TraceLink> derivedFunctions,
        List<TraceLink> derivedScreens,
        List<TraceLink> derivedApis,
        List<TraceLink> derivedComponents) {
    public record TraceLink(UUID id, String objectType, String code, String name, boolean derived) {}
}
