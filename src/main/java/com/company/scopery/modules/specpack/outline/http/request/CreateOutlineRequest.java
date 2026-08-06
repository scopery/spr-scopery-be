package com.company.scopery.modules.specpack.outline.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record CreateOutlineRequest(
        @NotNull Map<String, Object> outlineJson
) {}
