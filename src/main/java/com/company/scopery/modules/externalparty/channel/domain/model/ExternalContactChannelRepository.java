package com.company.scopery.modules.externalparty.channel.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExternalContactChannelRepository {
    ExternalContactChannel save(ExternalContactChannel channel);
    Optional<ExternalContactChannel> findById(UUID id);
    List<ExternalContactChannel> findByContactId(UUID workspaceId, UUID contactId);
    boolean existsPrimaryByContactIdAndChannelType(UUID workspaceId, UUID contactId, String channelType);
}
