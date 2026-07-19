package com.company.scopery.modules.trust.legalhold.infrastructure.mapper;
import com.company.scopery.modules.trust.legalhold.domain.model.LegalHold;
import com.company.scopery.modules.trust.legalhold.infrastructure.persistence.LegalHoldJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class LegalHoldPersistenceMapper {
    public LegalHoldJpaEntity toJpaEntity(LegalHold d) {
        LegalHoldJpaEntity e = new LegalHoldJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setHoldCode(d.holdCode()); e.setHoldType(d.holdType());
        e.setScopeType(d.scopeType()); e.setScopeId(d.scopeId()); e.setReason(d.reason()); e.setStatus(d.status());
        e.setReleasedAt(d.releasedAt()); e.setReleaseReason(d.releaseReason()); e.setVersion(d.version());
        e.setCreatedAt(d.createdAt()); return e;
    }
    public LegalHold toDomain(LegalHoldJpaEntity e) {
        return new LegalHold(e.getId(), e.getWorkspaceId(), e.getHoldCode(), e.getHoldType(), e.getScopeType(), e.getScopeId(),
                e.getReason(), e.getStatus(), e.getReleasedAt(), e.getReleaseReason(),
                e.getVersion()==null?0:e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
