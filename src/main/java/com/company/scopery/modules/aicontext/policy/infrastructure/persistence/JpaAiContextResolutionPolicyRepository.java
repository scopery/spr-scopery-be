package com.company.scopery.modules.aicontext.policy.infrastructure.persistence;

import com.company.scopery.modules.aicontext.policy.domain.model.AiContextResolutionPolicy;
import com.company.scopery.modules.aicontext.policy.domain.model.AiContextResolutionPolicyRepository;
import com.company.scopery.modules.aicontext.policy.infrastructure.mapper.AiContextResolutionPolicyPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiContextResolutionPolicyRepository implements AiContextResolutionPolicyRepository {

    private final SpringDataAiContextResolutionPolicyJpaRepository springDataRepository;
    private final AiContextResolutionPolicyPersistenceMapper mapper;

    public JpaAiContextResolutionPolicyRepository(
            SpringDataAiContextResolutionPolicyJpaRepository springDataRepository,
            AiContextResolutionPolicyPersistenceMapper mapper
    ) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiContextResolutionPolicy save(AiContextResolutionPolicy policy) {
        AiContextResolutionPolicyJpaEntity entity = mapper.toJpaEntity(policy);
        // saveAndFlush ensures JPA auditing timestamps (createdAt, updatedAt) are populated
        // in the returned entity before mapping back to domain.
        AiContextResolutionPolicyJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiContextResolutionPolicy> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiContextResolutionPolicy> findByWorkspaceIdAndPolicyCode(UUID workspaceId, String policyCode) {
        return springDataRepository.findByWorkspaceIdAndPolicyCode(workspaceId, policyCode)
                .map(mapper::toDomain);
    }

    @Override
    public List<AiContextResolutionPolicy> findByWorkspaceId(UUID workspaceId) {
        return springDataRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
