package com.company.scopery.modules.aiagent.tool.infrastructure.persistence;

import com.company.scopery.modules.aiagent.tool.domain.model.AiAgentToolBinding;
import com.company.scopery.modules.aiagent.tool.domain.model.AiAgentToolBindingRepository;
import com.company.scopery.modules.aiagent.tool.infrastructure.mapper.AiAgentToolBindingPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiAgentToolBindingRepository implements AiAgentToolBindingRepository {

    private final SpringDataAiAgentToolBindingJpaRepository springDataRepository;
    private final AiAgentToolBindingPersistenceMapper mapper;

    public JpaAiAgentToolBindingRepository(SpringDataAiAgentToolBindingJpaRepository springDataRepository,
                                           AiAgentToolBindingPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiAgentToolBinding save(AiAgentToolBinding binding) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(binding)));
    }

    @Override
    public Optional<AiAgentToolBinding> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiAgentToolBinding> findByAgentIdAndToolId(UUID agentId, UUID toolId) {
        return springDataRepository.findByAgentIdAndToolId(agentId, toolId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByAgentIdAndToolId(UUID agentId, UUID toolId) {
        return springDataRepository.existsByAgentIdAndToolId(agentId, toolId);
    }

    @Override
    public List<AiAgentToolBinding> findByToolId(UUID toolId) {
        return springDataRepository.findByToolId(toolId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AiAgentToolBinding> findByAgentId(UUID agentId) {
        return springDataRepository.findByAgentId(agentId).stream().map(mapper::toDomain).toList();
    }
}
