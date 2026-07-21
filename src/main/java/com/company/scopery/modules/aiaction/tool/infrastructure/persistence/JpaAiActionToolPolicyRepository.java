package com.company.scopery.modules.aiaction.tool.infrastructure.persistence;

import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionInvocationScope;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionToolPolicyStatus;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicy;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicyRepository;
import com.company.scopery.modules.aiaction.tool.infrastructure.mapper.AiActionToolPolicyPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAiActionToolPolicyRepository implements AiActionToolPolicyRepository {

    private final SpringDataAiActionToolPolicyJpaRepository springDataRepository;
    private final AiActionToolPolicyPersistenceMapper mapper;

    public JpaAiActionToolPolicyRepository(SpringDataAiActionToolPolicyJpaRepository springDataRepository,
                                            AiActionToolPolicyPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionToolPolicy save(AiActionToolPolicy policy) {
        AiActionToolPolicyJpaEntity entity = mapper.toJpaEntity(policy);
        AiActionToolPolicyJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiActionToolPolicy> findByToolCodeAndToolVersion(String toolCode, String toolVersion) {
        return springDataRepository.findByToolCodeAndToolVersion(toolCode, toolVersion)
                .map(mapper::toDomain);
    }

    @Override
    public List<AiActionToolPolicy> findByStatus(AiActionToolPolicyStatus status) {
        return springDataRepository.findByStatus(status.name())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AiActionToolPolicy> findByInvocationScopeAndStatus(AiActionInvocationScope scope,
                                                                    AiActionToolPolicyStatus status) {
        return springDataRepository.findByInvocationScopeAndStatus(scope.name(), status.name())
                .stream().map(mapper::toDomain).toList();
    }
}
