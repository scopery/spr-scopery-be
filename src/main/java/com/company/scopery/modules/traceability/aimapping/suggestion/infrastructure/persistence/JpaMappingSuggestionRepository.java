package com.company.scopery.modules.traceability.aimapping.suggestion.infrastructure.persistence;

import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionReviewStatus;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestion;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestionRepository;
import com.company.scopery.modules.traceability.aimapping.suggestion.infrastructure.mapper.MappingSuggestionPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaMappingSuggestionRepository implements MappingSuggestionRepository {

    private final SpringDataMappingSuggestionJpaRepository springDataRepository;
    private final MappingSuggestionPersistenceMapper mapper;

    public JpaMappingSuggestionRepository(SpringDataMappingSuggestionJpaRepository springDataRepository,
                                          MappingSuggestionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public MappingSuggestion save(MappingSuggestion suggestion) {
        MappingSuggestionJpaEntity entity = mapper.toJpaEntity(suggestion);
        MappingSuggestionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void saveAll(List<MappingSuggestion> suggestions) {
        List<MappingSuggestionJpaEntity> entities = suggestions.stream()
                .map(mapper::toJpaEntity)
                .toList();
        springDataRepository.saveAllAndFlush(entities);
    }

    @Override
    public Optional<MappingSuggestion> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<MappingSuggestion> findAllById(List<UUID> ids) {
        return springDataRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<MappingSuggestion> findAcceptedByRunId(UUID runId) {
        return springDataRepository
                .findByRunIdAndReviewStatus(runId, SuggestionReviewStatus.ACCEPTED.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Page<MappingSuggestion> findByRunIdAndFilters(UUID runId,
                                                         String relationType,
                                                         String reviewStatus,
                                                         String confidenceBand,
                                                         String decision,
                                                         Boolean hasWarning,
                                                         UUID sourceId,
                                                         UUID targetId,
                                                         Pageable pageable) {
        Specification<MappingSuggestionJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("runId"), runId));
            if (relationType != null) predicates.add(cb.equal(root.get("relationType"), relationType));
            if (reviewStatus != null) predicates.add(cb.equal(root.get("reviewStatus"), reviewStatus));
            if (confidenceBand != null) predicates.add(cb.equal(root.get("confidenceBand"), confidenceBand));
            if (decision != null) predicates.add(cb.equal(root.get("decision"), decision));
            if (hasWarning != null) {
                if (Boolean.TRUE.equals(hasWarning)) {
                    predicates.add(cb.and(
                            cb.isNotNull(root.get("warningsJson")),
                            cb.notEqual(root.get("warningsJson"), "[]"),
                            cb.notEqual(root.get("warningsJson"), "")
                    ));
                } else {
                    predicates.add(cb.or(
                            cb.isNull(root.get("warningsJson")),
                            cb.equal(root.get("warningsJson"), "[]"),
                            cb.equal(root.get("warningsJson"), "")
                    ));
                }
            }
            if (sourceId != null) predicates.add(cb.equal(root.get("sourceId"), sourceId));
            if (targetId != null) predicates.add(cb.equal(root.get("targetId"), targetId));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public void updateReviewStatus(UUID id, SuggestionReviewStatus reviewStatus,
                                   UUID reviewedBy, Instant reviewedAt) {
        springDataRepository.updateReviewStatus(id, reviewStatus.name(), reviewedBy, reviewedAt);
    }

    @Override
    public void bulkUpdateReviewStatus(List<UUID> ids, SuggestionReviewStatus reviewStatus,
                                       UUID reviewedBy, Instant reviewedAt) {
        springDataRepository.bulkUpdateReviewStatus(ids, reviewStatus.name(), reviewedBy, reviewedAt);
    }
}
