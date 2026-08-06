package com.company.scopery.modules.specpack.block.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UpdateBlockRequest(
        String title,
        String contentFormat,
        Map<String, Object> contentJson,
        List<Map<String, Object>> sourceRefsJson,
        UUID parentBlockId,
        @NotNull Integer expectedRevisionNumber
) {}
