package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.model.AiMessageCitation;
import com.company.scopery.modules.aiassistant.domain.model.AiMessageCitationRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiMessageCitationPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaAiMessageCitationRepository implements AiMessageCitationRepository {

    private final SpringDataAiMessageCitationJpaRepository springDataRepository;
    private final AiMessageCitationPersistenceMapper mapper;

    public JpaAiMessageCitationRepository(SpringDataAiMessageCitationJpaRepository springDataRepository,
                                          AiMessageCitationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiMessageCitation save(AiMessageCitation citation) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(citation)));
    }

    @Override
    public List<AiMessageCitation> saveAll(List<AiMessageCitation> citations) {
        List<AiMessageCitationJpaEntity> entities = citations.stream().map(mapper::toJpaEntity).toList();
        return springDataRepository.saveAllAndFlush(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AiMessageCitation> findByMessageIdOrderByOrdinal(UUID messageId) {
        return springDataRepository.findAll(
                (root, query, cb) -> {
                    query.orderBy(cb.asc(root.get("ordinal")));
                    return cb.equal(root.get("messageId"), messageId);
                }
        ).stream().map(mapper::toDomain).toList();
    }
}
