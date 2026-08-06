package com.company.scopery.modules.specpack.version.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpecPackVersion {

    private UUID id;
    private UUID specPackId;
    private int versionNumber;
    private List<Map<String, Object>> snapshotJson;
    private Map<String, Object> outlineJson;
    private int blockCount;
    private int assetCount;
    private String changeReason;
    private String createdBy;
    private Instant createdAt;

    private SpecPackVersion() {}

    public static SpecPackVersion create(UUID specPackId, int versionNumber,
                                         List<Map<String, Object>> snapshotJson,
                                         Map<String, Object> outlineJson,
                                         int blockCount, int assetCount,
                                         String changeReason) {
        SpecPackVersion v = new SpecPackVersion();
        v.id = UUID.randomUUID();
        v.specPackId = specPackId;
        v.versionNumber = versionNumber;
        v.snapshotJson = snapshotJson != null ? snapshotJson : List.of();
        v.outlineJson = outlineJson;
        v.blockCount = blockCount;
        v.assetCount = assetCount;
        v.changeReason = changeReason;
        v.createdAt = Instant.now();
        return v;
    }

    public static SpecPackVersion reconstitute(UUID id, UUID specPackId, int versionNumber,
                                               List<Map<String, Object>> snapshotJson,
                                               Map<String, Object> outlineJson,
                                               int blockCount, int assetCount,
                                               String changeReason, String createdBy, Instant createdAt) {
        SpecPackVersion v = new SpecPackVersion();
        v.id = id;
        v.specPackId = specPackId;
        v.versionNumber = versionNumber;
        v.snapshotJson = snapshotJson != null ? snapshotJson : List.of();
        v.outlineJson = outlineJson;
        v.blockCount = blockCount;
        v.assetCount = assetCount;
        v.changeReason = changeReason;
        v.createdBy = createdBy;
        v.createdAt = createdAt;
        return v;
    }

    public UUID id()                                    { return id; }
    public UUID specPackId()                            { return specPackId; }
    public int versionNumber()                          { return versionNumber; }
    public List<Map<String, Object>> snapshotJson()    { return snapshotJson; }
    public Map<String, Object> outlineJson()            { return outlineJson; }
    public int blockCount()                             { return blockCount; }
    public int assetCount()                             { return assetCount; }
    public String changeReason()                        { return changeReason; }
    public String createdBy()                           { return createdBy; }
    public Instant createdAt()                          { return createdAt; }
}
