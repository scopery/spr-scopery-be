package com.company.scopery.modules.specpack.block.application.response;

import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record BlockResponse(
        UUID id,
        UUID specPackId,
        String blockKey,
        UUID parentBlockId,
        String blockType,
        String title,
        String contentFormat,
        Map<String, Object> contentJson,
        List<Map<String, Object>> sourceRefsJson,
        int displayOrder,
        String status,
        int currentRevisionNumber,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt,
        Instant deletedAt
) {
    public static BlockResponse from(SpecPackBlock block) {
        return new BlockResponse(
                block.id(),
                block.specPackId(),
                block.blockKey(),
                block.parentBlockId(),
                block.blockType().name(),
                block.title(),
                block.contentFormat().name(),
                block.contentJson(),
                block.sourceRefsJson(),
                block.displayOrder(),
                block.status().name(),
                block.currentRevisionNumber(),
                block.createdBy(),
                block.createdAt(),
                block.updatedBy(),
                block.updatedAt(),
                block.deletedAt()
        );
    }
}
