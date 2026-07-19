package com.company.scopery.modules.trust.consent.infrastructure.mapper;
import com.company.scopery.modules.trust.consent.domain.model.ConsentRecord;
import com.company.scopery.modules.trust.consent.domain.model.ContactSuppression;
import com.company.scopery.modules.trust.consent.infrastructure.persistence.ConsentRecordJpaEntity;
import com.company.scopery.modules.trust.consent.infrastructure.persistence.ContactSuppressionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ConsentPersistenceMapper {
    public ConsentRecordJpaEntity toJpaEntity(ConsentRecord d) {
        ConsentRecordJpaEntity e = new ConsentRecordJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setConsentType(d.consentType());
        e.setStatus(d.status()); e.setGivenAt(d.givenAt()); e.setWithdrawnAt(d.withdrawnAt());
        e.setCreatedAt(d.createdAt()); return e;
    }
    public ConsentRecord toDomain(ConsentRecordJpaEntity e) {
        return new ConsentRecord(e.getId(), e.getWorkspaceId(), e.getConsentType(),
                e.getStatus(), e.getGivenAt(), e.getWithdrawnAt(), e.getCreatedAt());
    }
    public ContactSuppressionJpaEntity toJpaEntity(ContactSuppression d) {
        ContactSuppressionJpaEntity e = new ContactSuppressionJpaEntity();
        e.setId(d.id()); e.setWorkspaceId(d.workspaceId()); e.setExternalContactId(d.externalContactId());
        e.setPortalAccountId(d.portalAccountId()); e.setSuppressionType(d.suppressionType());
        e.setReason(d.reason()); e.setStatus(d.status()); e.setReleasedAt(d.releasedAt());
        e.setReleaseReason(d.releaseReason()); e.setVersion(d.version()); e.setCreatedAt(d.createdAt());
        return e;
    }
    public ContactSuppression toDomain(ContactSuppressionJpaEntity e) {
        return new ContactSuppression(e.getId(), e.getWorkspaceId(), e.getExternalContactId(),
                e.getPortalAccountId(), e.getSuppressionType(), e.getReason(), e.getStatus(),
                e.getReleasedAt(), e.getReleaseReason(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
