package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.model.AiSuggestedQuestion;
import com.company.scopery.modules.aiassistant.domain.model.AiSuggestedQuestionRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiSuggestedQuestionPersistenceMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaAiSuggestedQuestionRepository implements AiSuggestedQuestionRepository {

    private final SpringDataAiSuggestedQuestionJpaRepository springDataRepository;
    private final AiSuggestedQuestionPersistenceMapper mapper;

    public JpaAiSuggestedQuestionRepository(SpringDataAiSuggestedQuestionJpaRepository springDataRepository,
                                            AiSuggestedQuestionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public List<AiSuggestedQuestion> findByPageCodeAndEntityTypeAndLocaleAndStatus(
            String pageCode, String entityType, String locale, String status) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("pageCode"), pageCode),
                        entityType != null ? cb.equal(root.get("entityType"), entityType) : cb.isNull(root.get("entityType")),
                        cb.equal(root.get("locale"), locale),
                        cb.equal(root.get("status"), status)
                ),
                Sort.by(Sort.Direction.ASC, "displayOrder")
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<AiSuggestedQuestion> findByPageCodeAndLocaleAndStatus(
            String pageCode, String locale, String status) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("pageCode"), pageCode),
                        cb.equal(root.get("locale"), locale),
                        cb.equal(root.get("status"), status)
                ),
                Sort.by(Sort.Direction.ASC, "displayOrder")
        ).stream().map(mapper::toDomain).toList();
    }
}
