package com.company.scopery.modules.traceability.apiendpoint.http.request;

import com.company.scopery.modules.traceability.apiendpoint.domain.enums.ApiParamLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApiParamItemRequest(
        @NotBlank String name,
        @NotNull ApiParamLocation in,
        String type,
        Boolean required,
        String description,
        String example
) {}
