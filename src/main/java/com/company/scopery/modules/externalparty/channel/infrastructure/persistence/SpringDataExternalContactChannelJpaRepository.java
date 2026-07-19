package com.company.scopery.modules.externalparty.channel.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataExternalContactChannelJpaRepository extends JpaRepository<ExternalContactChannelJpaEntity, UUID> {
    List<ExternalContactChannelJpaEntity> findByWorkspaceIdAndExternalContactId(UUID workspaceId, UUID contactId);
    boolean existsByWorkspaceIdAndExternalContactIdAndChannelTypeAndPrimaryFlagTrue(UUID workspaceId, UUID contactId, String channelType);
}
