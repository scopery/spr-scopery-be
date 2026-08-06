package com.company.scopery.modules.specpack.block.domain.model;

import com.company.scopery.modules.specpack.block.domain.enums.BlockStatus;
import com.company.scopery.modules.specpack.block.domain.enums.BlockType;
import com.company.scopery.modules.specpack.block.domain.enums.ChangeSource;
import com.company.scopery.modules.specpack.block.domain.enums.ContentFormat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpecPackBlock {

    private UUID id;
    private UUID specPackId;
    private String blockKey;
    private UUID parentBlockId;
    private BlockType blockType;
    private String title;
    private ContentFormat contentFormat;
    private Map<String, Object> contentJson;
    private List<Map<String, Object>> sourceRefsJson;
    private int displayOrder;
    private BlockStatus status;
    private int currentRevisionNumber;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    private Instant deletedAt;

    private SpecPackBlock() {}

    public static SpecPackBlock create(UUID specPackId, String blockKey, UUID parentBlockId,
                                       BlockType blockType, String title, ContentFormat contentFormat,
                                       Map<String, Object> contentJson, List<Map<String, Object>> sourceRefsJson,
                                       int displayOrder) {
        if (blockKey == null || blockKey.isBlank()) throw new IllegalArgumentException("blockKey is required");
        if (blockKey.contains(" ")) throw new IllegalArgumentException("blockKey must not contain spaces");
        SpecPackBlock b = new SpecPackBlock();
        b.id = UUID.randomUUID();
        b.specPackId = specPackId;
        b.blockKey = blockKey;
        b.parentBlockId = parentBlockId;
        b.blockType = blockType;
        b.title = title;
        b.contentFormat = contentFormat;
        b.contentJson = contentJson != null ? contentJson : Map.of();
        b.sourceRefsJson = sourceRefsJson;
        b.displayOrder = displayOrder;
        b.status = BlockStatus.DRAFT;
        b.currentRevisionNumber = 1;
        b.createdAt = Instant.now();
        b.updatedAt = b.createdAt;
        return b;
    }

    public static SpecPackBlock reconstitute(UUID id, UUID specPackId, String blockKey, UUID parentBlockId,
                                              BlockType blockType, String title, ContentFormat contentFormat,
                                              Map<String, Object> contentJson, List<Map<String, Object>> sourceRefsJson,
                                              int displayOrder, BlockStatus status, int currentRevisionNumber,
                                              String createdBy, Instant createdAt, String updatedBy, Instant updatedAt,
                                              Instant deletedAt) {
        SpecPackBlock b = new SpecPackBlock();
        b.id = id;
        b.specPackId = specPackId;
        b.blockKey = blockKey;
        b.parentBlockId = parentBlockId;
        b.blockType = blockType;
        b.title = title;
        b.contentFormat = contentFormat;
        b.contentJson = contentJson != null ? contentJson : Map.of();
        b.sourceRefsJson = sourceRefsJson;
        b.displayOrder = displayOrder;
        b.status = status;
        b.currentRevisionNumber = currentRevisionNumber;
        b.createdBy = createdBy;
        b.createdAt = createdAt;
        b.updatedBy = updatedBy;
        b.updatedAt = updatedAt;
        b.deletedAt = deletedAt;
        return b;
    }

    public void update(UUID parentBlockId, String title, ContentFormat contentFormat,
                       Map<String, Object> contentJson, List<Map<String, Object>> sourceRefsJson,
                       int expectedRevisionNumber) {
        if (deletedAt != null) throw new IllegalStateException("Block has been deleted");
        if (currentRevisionNumber != expectedRevisionNumber) {
            throw new IllegalStateException("Stale revision: expected " + expectedRevisionNumber + " but found " + currentRevisionNumber);
        }
        this.parentBlockId = parentBlockId;
        this.title = title;
        this.contentFormat = contentFormat;
        this.contentJson = contentJson != null ? contentJson : Map.of();
        this.sourceRefsJson = sourceRefsJson;
        this.currentRevisionNumber = currentRevisionNumber + 1;
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        if (deletedAt != null) throw new IllegalStateException("Block is already deleted");
        this.deletedAt = Instant.now();
        this.updatedAt = this.deletedAt;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
        this.updatedAt = Instant.now();
    }

    public void restoreRevision(SpecPackBlockRevision revision) {
        this.contentFormat = revision.contentFormat();
        this.contentJson = revision.contentJson();
        this.sourceRefsJson = revision.sourceRefsJson();
        this.title = revision.title();
        this.currentRevisionNumber = currentRevisionNumber + 1;
        this.updatedAt = Instant.now();
    }

    public UUID id()                             { return id; }
    public UUID specPackId()                     { return specPackId; }
    public String blockKey()                     { return blockKey; }
    public UUID parentBlockId()                  { return parentBlockId; }
    public BlockType blockType()                 { return blockType; }
    public String title()                        { return title; }
    public ContentFormat contentFormat()         { return contentFormat; }
    public Map<String, Object> contentJson()     { return contentJson; }
    public List<Map<String, Object>> sourceRefsJson() { return sourceRefsJson; }
    public int displayOrder()                    { return displayOrder; }
    public BlockStatus status()                  { return status; }
    public int currentRevisionNumber()           { return currentRevisionNumber; }
    public String createdBy()                    { return createdBy; }
    public Instant createdAt()                   { return createdAt; }
    public String updatedBy()                    { return updatedBy; }
    public Instant updatedAt()                   { return updatedAt; }
    public Instant deletedAt()                   { return deletedAt; }
    public boolean isDeleted()                   { return deletedAt != null; }
}
