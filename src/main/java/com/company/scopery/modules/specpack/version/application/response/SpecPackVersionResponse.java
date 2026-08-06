package com.company.scopery.modules.specpack.version.application.response;

import com.company.scopery.modules.specpack.version.domain.model.SpecPackVersion;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SpecPackVersionResponse(
        UUID id,
        UUID specPackId,
        int versionNumber,
        List<Map<String, Object>> snapshotJson,
        Map<String, Object> outlineJson,
        int blockCount,
        int assetCount,
        String changeReason,
        String createdBy,
        Instant createdAt
) {
    public static SpecPackVersionResponse from(SpecPackVersion version) {
        return new SpecPackVersionResponse(
                version.id(),
                version.specPackId(),
                version.versionNumber(),
                version.snapshotJson(),
                version.outlineJson(),
                version.blockCount(),
                version.assetCount(),
                version.changeReason(),
                version.createdBy(),
                version.createdAt()
        );
    }
}
