package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationPolicy;
import com.company.scopery.modules.airecommendation.domain.repository.AiRecommendationPolicyRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.AiRecommendationPolicyPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiRecommendationPolicyRepository implements AiRecommendationPolicyRepository {

    private final SpringDataAiRecommendationPolicyJpaRepository springDataRepository;
    private final AiRecommendationPolicyPersistenceMapper mapper;

    public JpaAiRecommendationPolicyRepository(
            SpringDataAiRecommendationPolicyJpaRepository springDataRepository,
            AiRecommendationPolicyPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiRecommendationPolicy save(AiRecommendationPolicy policy) {
        AiRecommendationPolicyJpaEntity entity = mapper.toJpaEntity(policy);
        AiRecommendationPolicyJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiRecommendationPolicy> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiRecommendationPolicy> findByCode(String code) {
        return springDataRepository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return springDataRepository.existsByCode(code);
    }
}
