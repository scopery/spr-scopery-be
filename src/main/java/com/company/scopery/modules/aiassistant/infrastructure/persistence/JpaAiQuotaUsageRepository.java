package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.model.AiQuotaUsage;
import com.company.scopery.modules.aiassistant.domain.model.AiQuotaUsageRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiQuotaUsagePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiQuotaUsageRepository implements AiQuotaUsageRepository {

    private final SpringDataAiQuotaUsageJpaRepository springDataRepository;
    private final AiQuotaUsagePersistenceMapper mapper;

    public JpaAiQuotaUsageRepository(SpringDataAiQuotaUsageJpaRepository springDataRepository,
                                     AiQuotaUsagePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiQuotaUsage save(AiQuotaUsage usage) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(usage)));
    }

    @Override
    public Optional<AiQuotaUsage> findByWorkspaceIdAndActorIdAndUsageDate(UUID workspaceId, UUID actorId,
                                                                            LocalDate date) {
        return springDataRepository.findByWorkspaceIdAndActorIdAndUsageDate(workspaceId, actorId, date)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AiQuotaUsage> findByWorkspaceIdAndActorIdAndUsageDateForUpdate(UUID workspaceId, UUID actorId,
                                                                                    LocalDate date) {
        return springDataRepository.findByWorkspaceIdAndActorIdAndUsageDate(workspaceId, actorId, date)
                .map(mapper::toDomain);
    }
}
