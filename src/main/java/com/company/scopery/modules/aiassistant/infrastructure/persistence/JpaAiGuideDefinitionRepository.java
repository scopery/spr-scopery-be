package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinition;
import com.company.scopery.modules.aiassistant.domain.model.AiGuideDefinitionRepository;
import com.company.scopery.modules.aiassistant.infrastructure.mapper.AiGuideDefinitionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiGuideDefinitionRepository implements AiGuideDefinitionRepository {

    private final SpringDataAiGuideDefinitionJpaRepository springDataRepository;
    private final AiGuideDefinitionPersistenceMapper mapper;

    public JpaAiGuideDefinitionRepository(SpringDataAiGuideDefinitionJpaRepository springDataRepository,
                                          AiGuideDefinitionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<AiGuideDefinition> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiGuideDefinition> findByPageCodeAndFieldCodeAndActionCodeAndLocaleAndStatus(
            String pageCode, String fieldCode, String actionCode, String locale, String status) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("pageCode"), pageCode),
                        fieldCode != null ? cb.equal(root.get("fieldCode"), fieldCode) : cb.isNull(root.get("fieldCode")),
                        actionCode != null ? cb.equal(root.get("actionCode"), actionCode) : cb.isNull(root.get("actionCode")),
                        cb.equal(root.get("locale"), locale),
                        cb.equal(root.get("status"), status)
                )
        ).stream().findFirst().map(mapper::toDomain);
    }

    @Override
    public List<AiGuideDefinition> findByPageCodeAndLocaleAndStatus(String pageCode, String locale, String status) {
        return springDataRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("pageCode"), pageCode),
                        cb.equal(root.get("locale"), locale),
                        cb.equal(root.get("status"), status)
                )
        ).stream().map(mapper::toDomain).toList();
    }
}
