package com.company.scopery.modules.traceability.aimapping.suggestion.infrastructure.mapper;

import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.ConfidenceBand;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionDecision;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionReviewStatus;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestion;
import com.company.scopery.modules.traceability.aimapping.suggestion.infrastructure.persistence.MappingSuggestionJpaEntity;
import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;
import org.springframework.stereotype.Component;

@Component
public class MappingSuggestionPersistenceMapper {

    public MappingSuggestion toDomain(MappingSuggestionJpaEntity entity) {
        return new MappingSuggestion(
                entity.getId(),
                entity.getRunId(),
                SummaryEntityType.valueOf(entity.getSourceType()),
                entity.getSourceId(),
                entity.getSourceVersion(),
                entity.getSourceSummaryHash(),
                entity.getTargetType() != null ? SummaryEntityType.valueOf(entity.getTargetType()) : null,
                entity.getTargetId(),
                entity.getTargetVersion(),
                entity.getTargetSummaryHash(),
                MappingRelationType.valueOf(entity.getRelationType()),
                entity.getRank(),
                entity.getRetrievalScore(),
                entity.getAiScore(),
                entity.getFinalScore(),
                entity.getSecondBestScore(),
                entity.getScoreMargin(),
                entity.getConfidenceBand() != null ? ConfidenceBand.valueOf(entity.getConfidenceBand()) : null,
                SuggestionDecision.valueOf(entity.getDecision()),
                entity.getReasonCodesJson(),
                entity.getEvidenceJson(),
                entity.getWarningsJson(),
                entity.getCoverageJson(),
                SuggestionReviewStatus.valueOf(entity.getReviewStatus()),
                entity.getReviewedBy(),
                entity.getReviewedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public MappingSuggestionJpaEntity toJpaEntity(MappingSuggestion domain) {
        MappingSuggestionJpaEntity entity = new MappingSuggestionJpaEntity();
        entity.setId(domain.id());
        entity.setRunId(domain.runId());
        entity.setSourceType(domain.sourceType().name());
        entity.setSourceId(domain.sourceId());
        entity.setSourceVersion(domain.sourceVersion());
        entity.setSourceSummaryHash(domain.sourceSummaryHash());
        entity.setTargetType(domain.targetType() != null ? domain.targetType().name() : null);
        entity.setTargetId(domain.targetId());
        entity.setTargetVersion(domain.targetVersion());
        entity.setTargetSummaryHash(domain.targetSummaryHash());
        entity.setRelationType(domain.relationType().name());
        entity.setRank(domain.rank());
        entity.setRetrievalScore(domain.retrievalScore());
        entity.setAiScore(domain.aiScore());
        entity.setFinalScore(domain.finalScore());
        entity.setSecondBestScore(domain.secondBestScore());
        entity.setScoreMargin(domain.scoreMargin());
        entity.setConfidenceBand(domain.confidenceBand() != null ? domain.confidenceBand().name() : null);
        entity.setDecision(domain.decision().name());
        entity.setReasonCodesJson(domain.reasonCodesJson());
        entity.setEvidenceJson(domain.evidenceJson());
        entity.setWarningsJson(domain.warningsJson());
        entity.setCoverageJson(domain.coverageJson());
        entity.setReviewStatus(domain.reviewStatus().name());
        entity.setReviewedBy(domain.reviewedBy());
        entity.setReviewedAt(domain.reviewedAt());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}
