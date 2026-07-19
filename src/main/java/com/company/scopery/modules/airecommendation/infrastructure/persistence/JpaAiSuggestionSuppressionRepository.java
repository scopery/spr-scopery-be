package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionSuppression;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionSuppressionRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.AiSuggestionSuppressionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiSuggestionSuppressionRepository implements AiSuggestionSuppressionRepository {

    private final SpringDataAiSuggestionSuppressionJpaRepository springDataRepository;
    private final AiSuggestionSuppressionPersistenceMapper mapper;

    public JpaAiSuggestionSuppressionRepository(
            SpringDataAiSuggestionSuppressionJpaRepository springDataRepository,
            AiSuggestionSuppressionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiSuggestionSuppression save(AiSuggestionSuppression suppression) {
        AiSuggestionSuppressionJpaEntity entity = mapper.toJpaEntity(suppression);
        AiSuggestionSuppressionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiSuggestionSuppression> findActiveByKey(
            UUID workspaceId, UUID projectId, UUID actorId, String suppressionKey) {
        return springDataRepository.findActiveByKey(workspaceId, projectId, actorId, suppressionKey)
                .map(mapper::toDomain);
    }

    @Override
    public boolean hasActiveSuppressionFor(
            UUID workspaceId, UUID projectId, UUID actorId,
            String suggestionType, String targetEntityType, UUID targetEntityId) {
        return springDataRepository.hasActiveSuppressionFor(
                workspaceId, projectId, actorId, suggestionType, targetEntityType, targetEntityId);
    }

    @Override
    public void deactivateExpired(OffsetDateTime before) {
        springDataRepository.deactivateExpiredBefore(before.toInstant());
    }
}
