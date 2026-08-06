package com.company.scopery.modules.specpack.pack.domain.model;

import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackStatus;
import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackType;

import java.time.Instant;
import java.util.UUID;

public class SpecPack {

    private UUID id;
    private UUID projectId;
    private SpecPackType packType;
    private String name;
    private String description;
    private SpecPackStatus status;
    private UUID currentVersionId;
    private UUID sourcePackId;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    private Instant archivedAt;

    private SpecPack() {}

    public static SpecPack create(UUID projectId, SpecPackType packType, String name, String description, UUID sourcePackId) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        SpecPack p = new SpecPack();
        p.id = UUID.randomUUID();
        p.projectId = projectId;
        p.packType = packType;
        p.name = name.trim();
        p.description = description;
        p.status = SpecPackStatus.DRAFT;
        p.currentVersionId = null;
        p.sourcePackId = sourcePackId;
        p.createdAt = Instant.now();
        p.updatedAt = p.createdAt;
        return p;
    }

    public static SpecPack reconstitute(UUID id, UUID projectId, SpecPackType packType, String name, String description,
                                        SpecPackStatus status, UUID currentVersionId, UUID sourcePackId,
                                        String createdBy, Instant createdAt, String updatedBy, Instant updatedAt,
                                        Instant archivedAt) {
        SpecPack p = new SpecPack();
        p.id = id;
        p.projectId = projectId;
        p.packType = packType;
        p.name = name;
        p.description = description;
        p.status = status;
        p.currentVersionId = currentVersionId;
        p.sourcePackId = sourcePackId;
        p.createdBy = createdBy;
        p.createdAt = createdAt;
        p.updatedBy = updatedBy;
        p.updatedAt = updatedAt;
        p.archivedAt = archivedAt;
        return p;
    }

    public void update(String name, String description) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (status == SpecPackStatus.ARCHIVED) throw new IllegalStateException("Archived pack cannot be updated");
        this.name = name.trim();
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void archive() {
        if (status == SpecPackStatus.ARCHIVED) throw new IllegalStateException("Pack is already archived");
        this.status = SpecPackStatus.ARCHIVED;
        this.archivedAt = Instant.now();
        this.updatedAt = this.archivedAt;
    }

    public void restore() {
        if (status != SpecPackStatus.ARCHIVED) throw new IllegalStateException("Pack is not archived");
        this.status = SpecPackStatus.DRAFT;
        this.archivedAt = null;
        this.updatedAt = Instant.now();
    }

    public void setCurrentVersionId(UUID versionId) {
        this.currentVersionId = versionId;
        this.updatedAt = Instant.now();
    }

    public UUID id()              { return id; }
    public UUID projectId()       { return projectId; }
    public SpecPackType packType() { return packType; }
    public String name()          { return name; }
    public String description()   { return description; }
    public SpecPackStatus status() { return status; }
    public UUID currentVersionId() { return currentVersionId; }
    public UUID sourcePackId()    { return sourcePackId; }
    public String createdBy()     { return createdBy; }
    public Instant createdAt()    { return createdAt; }
    public String updatedBy()     { return updatedBy; }
    public Instant updatedAt()    { return updatedAt; }
    public Instant archivedAt()   { return archivedAt; }
}
