package com.company.scopery.modules.reporting.run.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateReportRunRequest(
        @NotBlank String reportCode,
        UUID projectId,
        Map<String, Object> filters,
        List<String> selectedFields
) {}
