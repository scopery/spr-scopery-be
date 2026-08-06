package com.company.scopery.modules.specpack.block.domain.model;

import com.company.scopery.modules.specpack.block.domain.enums.ChangeSource;
import com.company.scopery.modules.specpack.block.domain.enums.ContentFormat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SpecPackBlockRevision(
        UUID id,
        UUID specPackBlockId,
        int revisionNumber,
        String title,
        ContentFormat contentFormat,
        Map<String, Object> contentJson,
        List<Map<String, Object>> sourceRefsJson,
        ChangeSource changeSource,
        String changeComment,
        String createdBy,
        Instant createdAt
) {
    public static SpecPackBlockRevision create(UUID specPackBlockId, int revisionNumber, String title,
                                               ContentFormat contentFormat, Map<String, Object> contentJson,
                                               List<Map<String, Object>> sourceRefsJson,
                                               ChangeSource changeSource, String changeComment) {
        return new SpecPackBlockRevision(
                UUID.randomUUID(), specPackBlockId, revisionNumber, title,
                contentFormat, contentJson != null ? contentJson : Map.of(),
                sourceRefsJson, changeSource, changeComment, null, Instant.now()
        );
    }
}
