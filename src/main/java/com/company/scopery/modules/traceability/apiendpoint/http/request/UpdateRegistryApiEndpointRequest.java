package com.company.scopery.modules.traceability.apiendpoint.http.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
public record UpdateRegistryApiEndpointRequest(
        @NotBlank String method, @NotBlank String pathPattern, @NotBlank String name,
        String description,
        @Valid List<ApiParamItemRequest> requestParams,
        String responseSchemaJson
) {}
