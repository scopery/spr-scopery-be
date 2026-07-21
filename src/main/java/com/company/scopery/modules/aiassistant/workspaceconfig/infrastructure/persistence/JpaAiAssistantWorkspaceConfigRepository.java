package com.company.scopery.modules.aiassistant.workspaceconfig.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.workspaceconfig.domain.model.AiAssistantWorkspaceConfig;
import com.company.scopery.modules.aiassistant.workspaceconfig.domain.model.AiAssistantWorkspaceConfigRepository;
import com.company.scopery.modules.aiassistant.workspaceconfig.infrastructure.mapper.AiAssistantWorkspaceConfigPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiAssistantWorkspaceConfigRepository implements AiAssistantWorkspaceConfigRepository {

    private final SpringDataAiAssistantWorkspaceConfigJpaRepository springDataRepository;
    private final AiAssistantWorkspaceConfigPersistenceMapper mapper;

    public JpaAiAssistantWorkspaceConfigRepository(
            SpringDataAiAssistantWorkspaceConfigJpaRepository springDataRepository,
            AiAssistantWorkspaceConfigPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<AiAssistantWorkspaceConfig> findByWorkspaceId(UUID workspaceId) {
        return springDataRepository.findByWorkspaceId(workspaceId).map(mapper::toDomain);
    }

    @Override
    public AiAssistantWorkspaceConfig save(AiAssistantWorkspaceConfig config) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(config)));
    }
}
