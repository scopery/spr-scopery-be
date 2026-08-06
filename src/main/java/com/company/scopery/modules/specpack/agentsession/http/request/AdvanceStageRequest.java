package com.company.scopery.modules.specpack.agentsession.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record AdvanceStageRequest(
        @NotBlank String stageCode,
        Map<String, Object> result
) {}
