package com.company.scopery.modules.externalparty.channel.domain.model;

import com.company.scopery.modules.externalparty.channel.domain.enums.ChannelType;
import java.time.Instant;
import java.util.UUID;

public record ExternalContactChannel(
        UUID id, UUID workspaceId, UUID externalContactId,
        ChannelType channelType, String channelValue,
        boolean primaryFlag,
        int version, Instant createdAt, Instant updatedAt) {

    public static ExternalContactChannel create(UUID workspaceId, UUID contactId,
            ChannelType channelType, String channelValue, boolean primaryFlag) {
        Instant now = Instant.now();
        return new ExternalContactChannel(UUID.randomUUID(), workspaceId, contactId,
                channelType, channelValue, primaryFlag, 0, now, now);
    }
}
