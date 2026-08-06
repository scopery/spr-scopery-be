package com.company.scopery.modules.specpack.block.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateBlockRequest(
        @NotBlank String blockKey,
        @NotBlank String blockType,
        String title,
        @NotNull String contentFormat,
        Map<String, Object> contentJson,
        List<Map<String, Object>> sourceRefsJson,
        UUID parentBlockId,
        int displayOrder
) {}
