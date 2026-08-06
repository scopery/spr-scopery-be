package com.company.scopery.modules.specpack.block.application.command;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UpdateBlockCommand(
        UUID projectId,
        UUID specPackId,
        UUID blockId,
        UUID parentBlockId,
        String title,
        String contentFormat,
        Map<String, Object> contentJson,
        List<Map<String, Object>> sourceRefsJson,
        int expectedRevisionNumber
) {}
