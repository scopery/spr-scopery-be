package com.company.scopery.modules.scope.mapping.http.request;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateWbsMappingRequest(@NotNull UUID wbsNodeId, String mappingType) {}
