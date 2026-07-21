package com.company.scopery.modules.aiaction.request.infrastructure.persistence;

import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequest;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequestRepository;
import com.company.scopery.modules.aiaction.request.infrastructure.mapper.AiActionRequestPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiActionRequestRepository implements AiActionRequestRepository {

    private final SpringDataAiActionRequestJpaRepository springDataRepository;
    private final AiActionRequestPersistenceMapper mapper;

    public JpaAiActionRequestRepository(SpringDataAiActionRequestJpaRepository springDataRepository,
                                         AiActionRequestPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiActionRequest save(AiActionRequest request) {
        AiActionRequestJpaEntity entity = mapper.toJpaEntity(request);
        AiActionRequestJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiActionRequest> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiActionRequest> findByWorkspaceAndUserAndIdempotencyKey(UUID workspaceId, UUID userId, String idempotencyKey) {
        return springDataRepository
                .findByWorkspaceIdAndInitiatedByUserIdAndIdempotencyKey(workspaceId, userId, idempotencyKey)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByWorkspaceAndUserAndIdempotencyKeyAndDifferentHash(UUID workspaceId, UUID userId, String idempotencyKey, String requestHash) {
        return springDataRepository
                .existsByWorkspaceIdAndInitiatedByUserIdAndIdempotencyKeyAndRequestHashNot(
                        workspaceId, userId, idempotencyKey, requestHash);
    }
}
