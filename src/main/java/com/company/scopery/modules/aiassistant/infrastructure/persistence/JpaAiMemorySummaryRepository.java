package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.enums.MemorySummaryStatus;
import com.company.scopery.modules.aiassistant.domain.model.AiMemorySummary;
import com.company.scopery.modules.aiassistant.domain.model.AiMemorySummaryRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiMemorySummaryPersistenceMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiMemorySummaryRepository implements AiMemorySummaryRepository {

    private final SpringDataAiMemorySummaryJpaRepository springDataRepository;
    private final AiMemorySummaryPersistenceMapper mapper;

    public JpaAiMemorySummaryRepository(SpringDataAiMemorySummaryJpaRepository springDataRepository,
                                        AiMemorySummaryPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiMemorySummary save(AiMemorySummary summary) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(summary)));
    }

    @Override
    public Optional<AiMemorySummary> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiMemorySummary> findByConversationIdAndStatus(UUID conversationId, MemorySummaryStatus status) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("conversationId"), conversationId),
                        cb.equal(root.get("status"), status.name())
                )
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public List<AiMemorySummary> findByConversationIdOrderByVersionDesc(UUID conversationId) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("conversationId"), conversationId),
                Sort.by(Sort.Direction.DESC, "summaryVersion")
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public int countByConversationId(UUID conversationId) {
        return (int) springDataRepository.count(
                (root, query, cb) -> cb.equal(root.get("conversationId"), conversationId)
        );
    }
}
