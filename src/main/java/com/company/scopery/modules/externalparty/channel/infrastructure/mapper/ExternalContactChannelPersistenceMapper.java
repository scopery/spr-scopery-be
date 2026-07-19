package com.company.scopery.modules.externalparty.channel.infrastructure.mapper;

import com.company.scopery.modules.externalparty.channel.domain.enums.ChannelType;
import com.company.scopery.modules.externalparty.channel.domain.model.ExternalContactChannel;
import com.company.scopery.modules.externalparty.channel.infrastructure.persistence.ExternalContactChannelJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ExternalContactChannelPersistenceMapper {

    public ExternalContactChannel toDomain(ExternalContactChannelJpaEntity e) {
        return new ExternalContactChannel(e.getId(), e.getWorkspaceId(), e.getExternalContactId(),
                e.getChannelType() != null ? ChannelType.valueOf(e.getChannelType()) : null,
                e.getChannelValue(), e.isPrimaryFlag(),
                e.getVersion() != null ? e.getVersion() : 0,
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public ExternalContactChannelJpaEntity toJpaEntity(ExternalContactChannel d) {
        var e = new ExternalContactChannelJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setExternalContactId(d.externalContactId());
        e.setChannelType(d.channelType() != null ? d.channelType().name() : null);
        e.setChannelValue(d.channelValue());
        e.setPrimaryFlag(d.primaryFlag());
        e.setCreatedAt(d.createdAt());
        e.setVersion(d.version());
        return e;
    }
}
