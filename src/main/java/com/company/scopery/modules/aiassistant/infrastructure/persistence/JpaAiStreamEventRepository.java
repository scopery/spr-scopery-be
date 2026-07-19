package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.model.AiStreamEvent;
import com.company.scopery.modules.aiassistant.domain.model.AiStreamEventRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiStreamEventPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class JpaAiStreamEventRepository implements AiStreamEventRepository {

    private final SpringDataAiStreamEventJpaRepository springDataRepository;
    private final AiStreamEventPersistenceMapper mapper;

    public JpaAiStreamEventRepository(SpringDataAiStreamEventJpaRepository springDataRepository,
                                      AiStreamEventPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiStreamEvent save(AiStreamEvent event) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(event)));
    }

    @Override
    public List<AiStreamEvent> findByMessageIdAndSequenceGreaterThanOrderBySequence(UUID messageId, long afterSequence) {
        return springDataRepository
                .findByMessageIdAndSequenceGreaterThanOrderBySequenceAsc(messageId, afterSequence)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public int deleteByExpiresAtBefore(Instant threshold) {
        List<AiStreamEventJpaEntity> expired = springDataRepository.findAll(
                (root, query, cb) -> cb.lessThan(root.get("expiresAt"), threshold)
        );
        springDataRepository.deleteAll(expired);
        return expired.size();
    }
}
