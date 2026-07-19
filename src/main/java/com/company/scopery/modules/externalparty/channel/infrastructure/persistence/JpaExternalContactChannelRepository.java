package com.company.scopery.modules.externalparty.channel.infrastructure.persistence;

import com.company.scopery.modules.externalparty.channel.domain.model.ExternalContactChannel;
import com.company.scopery.modules.externalparty.channel.domain.model.ExternalContactChannelRepository;
import com.company.scopery.modules.externalparty.channel.infrastructure.mapper.ExternalContactChannelPersistenceMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaExternalContactChannelRepository implements ExternalContactChannelRepository {
    private final SpringDataExternalContactChannelJpaRepository spring;
    private final ExternalContactChannelPersistenceMapper mapper;

    public JpaExternalContactChannelRepository(SpringDataExternalContactChannelJpaRepository spring, ExternalContactChannelPersistenceMapper mapper) {
        this.spring = spring; this.mapper = mapper;
    }

    @Override public ExternalContactChannel save(ExternalContactChannel c) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(c)));
    }
    @Override public Optional<ExternalContactChannel> findById(UUID id) { return spring.findById(id).map(mapper::toDomain); }
    @Override public List<ExternalContactChannel> findByContactId(UUID workspaceId, UUID contactId) {
        return spring.findByWorkspaceIdAndExternalContactId(workspaceId, contactId).stream().map(mapper::toDomain).toList();
    }
    @Override public boolean existsPrimaryByContactIdAndChannelType(UUID workspaceId, UUID contactId, String channelType) {
        return spring.existsByWorkspaceIdAndExternalContactIdAndChannelTypeAndPrimaryFlagTrue(workspaceId, contactId, channelType);
    }
}
