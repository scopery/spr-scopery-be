package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import com.company.scopery.modules.airecommendation.domain.repository.AiSuggestionRepository;
import com.company.scopery.modules.airecommendation.infrastructure.mapper.AiSuggestionPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiSuggestionRepository implements AiSuggestionRepository {

    private final SpringDataAiSuggestionJpaRepository springDataRepository;
    private final AiSuggestionPersistenceMapper mapper;

    public JpaAiSuggestionRepository(
            SpringDataAiSuggestionJpaRepository springDataRepository,
            AiSuggestionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiSuggestion save(AiSuggestion suggestion) {
        AiSuggestionJpaEntity entity = mapper.toJpaEntity(suggestion);
        AiSuggestionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiSuggestion> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiSuggestion> findActiveByWorkspaceAndDedupKey(UUID workspaceId, String dedupKey) {
        return springDataRepository.findActiveByWorkspaceAndDedupKey(workspaceId, dedupKey)
                .map(mapper::toDomain);
    }

    @Override
    public Page<AiSuggestion> findByProjectWithFilters(
            UUID workspaceId, UUID projectId,
            List<SuggestionStatus> statuses,
            List<String> severities,
            String packCode,
            String suggestionType,
            String targetEntityType,
            boolean includeExpired,
            Pageable pageable) {
        Specification<AiSuggestionJpaEntity> spec = buildProjectFilterSpec(
                workspaceId, projectId, statuses, severities, packCode, suggestionType, targetEntityType, includeExpired);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<AiSuggestion> findByEntity(
            UUID workspaceId, String entityType, UUID entityId, UUID projectId, Pageable pageable) {
        return springDataRepository
                .findByWorkspaceAndTargetEntity(workspaceId, entityType, entityId, projectId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public List<AiSuggestion> findExpiredAndActive(OffsetDateTime before, int batchSize) {
        Pageable limit = PageRequest.of(0, batchSize);
        return springDataRepository
                .findExpiredAndActive(before.toInstant(), limit)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    private Specification<AiSuggestionJpaEntity> buildProjectFilterSpec(
            UUID workspaceId, UUID projectId,
            List<SuggestionStatus> statuses,
            List<String> severities,
            String packCode,
            String suggestionType,
            String targetEntityType,
            boolean includeExpired) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            predicates.add(cb.equal(root.get("projectId"), projectId));
            if (statuses != null && !statuses.isEmpty()) {
                List<String> statusNames = statuses.stream().map(SuggestionStatus::name).toList();
                predicates.add(root.get("status").in(statusNames));
            }
            if (severities != null && !severities.isEmpty()) {
                predicates.add(root.get("severity").in(severities));
            }
            if (packCode != null && !packCode.isBlank()) {
                predicates.add(cb.equal(root.get("packCode"), packCode));
            }
            if (suggestionType != null && !suggestionType.isBlank()) {
                predicates.add(cb.equal(root.get("suggestionType"), suggestionType));
            }
            if (targetEntityType != null && !targetEntityType.isBlank()) {
                predicates.add(cb.equal(root.get("targetEntityType"), targetEntityType));
            }
            if (!includeExpired) {
                predicates.add(cb.or(
                        cb.isNull(root.get("expiresAt")),
                        cb.greaterThan(root.get("expiresAt"), OffsetDateTime.now(ZoneOffset.UTC).toInstant())
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
