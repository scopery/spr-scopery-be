package com.company.scopery.modules.aiaction.tool.infrastructure.persistence;

import com.company.scopery.modules.aiaction.tool.domain.model.AiActionPolicyDefinition;
import com.company.scopery.modules.aiaction.tool.infrastructure.mapper.AiActionPolicyDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionPolicyDefinitionRepository {

    private final SpringDataAiActionPolicyDefinitionJpaRepository springDataRepository;
    private final AiActionPolicyDefinitionPersistenceMapper mapper;

    public JpaAiActionPolicyDefinitionRepository(SpringDataAiActionPolicyDefinitionJpaRepository springDataRepository,
                                                  AiActionPolicyDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    public AiActionPolicyDefinition save(AiActionPolicyDefinition policyDefinition) {
        AiActionPolicyDefinitionJpaEntity entity = mapper.toJpaEntity(policyDefinition);
        AiActionPolicyDefinitionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    public Optional<AiActionPolicyDefinition> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    public Optional<AiActionPolicyDefinition> findByPolicyCodeAndVersion(String policyCode, int policyVersion) {
        return springDataRepository.findByPolicyCodeAndPolicyVersion(policyCode, policyVersion)
                .map(mapper::toDomain);
    }

    public Optional<AiActionPolicyDefinition> findActiveByPolicyCode(String policyCode) {
        return springDataRepository.findByPolicyCodeAndStatus(policyCode, "ACTIVE")
                .map(mapper::toDomain);
    }
}
