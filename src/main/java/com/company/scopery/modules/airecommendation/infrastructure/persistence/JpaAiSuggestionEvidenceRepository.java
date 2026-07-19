package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionEvidence;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionEvidenceRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.AiSuggestionEvidencePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaAiSuggestionEvidenceRepository implements AiSuggestionEvidenceRepository {

    private final SpringDataAiSuggestionEvidenceJpaRepository springDataRepository;
    private final AiSuggestionEvidencePersistenceMapper mapper;

    public JpaAiSuggestionEvidenceRepository(
            SpringDataAiSuggestionEvidenceJpaRepository springDataRepository,
            AiSuggestionEvidencePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiSuggestionEvidence save(AiSuggestionEvidence evidence) {
        AiSuggestionEvidenceJpaEntity entity = mapper.toJpaEntity(evidence);
        AiSuggestionEvidenceJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<AiSuggestionEvidence> saveAll(List<AiSuggestionEvidence> evidences) {
        List<AiSuggestionEvidenceJpaEntity> entities = evidences.stream().map(mapper::toJpaEntity).toList();
        List<AiSuggestionEvidenceJpaEntity> saved = springDataRepository.saveAllAndFlush(entities);
        return saved.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AiSuggestionEvidence> findBySuggestionId(UUID suggestionId) {
        return springDataRepository.findBySuggestionIdOrderByOrdinal(suggestionId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsBySuggestionAndSourceKey(UUID suggestionId, String sourceType, UUID sourceRefId, UUID knowledgeChunkId) {
        return springDataRepository.existsBySuggestionAndSourceKey(suggestionId, sourceType, sourceRefId, knowledgeChunkId);
    }
}
