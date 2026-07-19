package com.company.scopery.modules.externalparty.preference.infrastructure.mapper;

import com.company.scopery.modules.externalparty.preference.domain.model.CommunicationPreference;
import com.company.scopery.modules.externalparty.preference.infrastructure.persistence.CommunicationPreferenceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CommunicationPreferencePersistenceMapper {

    public CommunicationPreference toDomain(CommunicationPreferenceJpaEntity e) {
        return new CommunicationPreference(e.getId(), e.getWorkspaceId(),
                e.getExternalOrganizationId(), e.getExternalContactId(),
                e.getPreferredChannelType(), e.getPreferredLanguage(),
                e.isDoNotContact(), e.getNotes(),
                e.getVersion() != null ? e.getVersion() : 0,
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public CommunicationPreferenceJpaEntity toJpaEntity(CommunicationPreference d) {
        var e = new CommunicationPreferenceJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setExternalOrganizationId(d.externalOrganizationId());
        e.setExternalContactId(d.externalContactId());
        e.setPreferredChannelType(d.preferredChannelType());
        e.setPreferredLanguage(d.preferredLanguage());
        e.setDoNotContact(d.doNotContact());
        e.setNotes(d.notes());
        e.setCreatedAt(d.createdAt());
        e.setVersion(d.version());
        return e;
    }
}
