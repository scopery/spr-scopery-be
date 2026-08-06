package com.company.scopery.modules.specpack.block.application.response;

import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRevision;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record BlockRevisionResponse(
        UUID id,
        UUID specPackBlockId,
        int revisionNumber,
        String title,
        String contentFormat,
        Map<String, Object> contentJson,
        List<Map<String, Object>> sourceRefsJson,
        String changeSource,
        String changeComment,
        String createdBy,
        Instant createdAt
) {
    public static BlockRevisionResponse from(SpecPackBlockRevision revision) {
        return new BlockRevisionResponse(
                revision.id(),
                revision.specPackBlockId(),
                revision.revisionNumber(),
                revision.title(),
                revision.contentFormat().name(),
                revision.contentJson(),
                revision.sourceRefsJson(),
                revision.changeSource().name(),
                revision.changeComment(),
                revision.createdBy(),
                revision.createdAt()
        );
    }
}
