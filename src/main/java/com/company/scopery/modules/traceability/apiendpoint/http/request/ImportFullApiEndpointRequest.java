package com.company.scopery.modules.traceability.apiendpoint.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record ImportFullApiEndpointRequest(
        @NotNull @Size(min = 1, max = 200) List<@Valid ApiEndpointImportItem> items) {

    public record ApiEndpointImportItem(
            @NotNull UUID projectId,
            @NotBlank String method,
            @NotBlank @Size(max = 500) String pathPattern,
            @NotBlank @Size(max = 255) String name,
            String description,
            List<@Valid ParamItem> requestParams,
            String responseSchemaJson) {

        public record ParamItem(
                @NotBlank String name,
                @NotBlank String in,
                String type,
                Boolean required,
                String description,
                String example) {}
    }
}
