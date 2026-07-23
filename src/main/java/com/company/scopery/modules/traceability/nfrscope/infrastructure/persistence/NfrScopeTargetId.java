package com.company.scopery.modules.traceability.nfrscope.infrastructure.persistence;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class NfrScopeTargetId implements Serializable {

    private UUID nfrId;
    private UUID targetId;

    public NfrScopeTargetId() {}

    public NfrScopeTargetId(UUID nfrId, UUID targetId) {
        this.nfrId = nfrId;
        this.targetId = targetId;
    }

    public UUID getNfrId() { return nfrId; }
    public UUID getTargetId() { return targetId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NfrScopeTargetId that)) return false;
        return Objects.equals(nfrId, that.nfrId) && Objects.equals(targetId, that.targetId);
    }

    @Override
    public int hashCode() { return Objects.hash(nfrId, targetId); }
}
