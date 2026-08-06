package com.company.scopery.modules.specpack.block.application.command;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateBlockCommand(
        UUID projectId,
        UUID specPackId,
        String blockKey,
        UUID parentBlockId,
        String blockType,
        String title,
        String contentFormat,
        Map<String, Object> contentJson,
        List<Map<String, Object>> sourceRefsJson,
        int displayOrder
) {}
