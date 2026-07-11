package com.company.scopery.modules.aiagent.aimodel.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelType;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.valueobject.AiModelCode;
import com.company.scopery.modules.aiagent.aimodel.infrastructure.mapper.AiModelPersistenceMapper;
import com.company.scopery.modules.aiagent.aimodel.infrastructure.persistence.entity.AiModelJpaEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAiModelRepository implements AiModelRepository {

    private final SpringDataAiModelJpaRepository springDataRepository;
    private final AiModelPersistenceMapper mapper;

    public JpaAiModelRepository(SpringDataAiModelJpaRepository springDataRepository,
                                 AiModelPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiModel save(AiModel model) {
        AiModelJpaEntity entity = mapper.toJpaEntity(model);
        AiModelJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiModel> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProviderIdAndCode(UUID providerId, AiModelCode code) {
        return springDataRepository.existsByProviderIdAndCode(providerId, code.value());
    }

    @Override
    public PageResult<AiModel> findAll(UUID providerId, String keyword, AiModelStatus status,
                                        AiModelType type, PageQuery pageQuery) {
        Specification<AiModelJpaEntity> spec = buildSearchSpec(providerId, keyword, status, type);
        Page<AiModel> page = springDataRepository.findAll(spec, toPageable(pageQuery)).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<AiModelJpaEntity> buildSearchSpec(UUID providerId, String keyword,
                                                              AiModelStatus status, AiModelType type) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (providerId != null) {
                predicates.add(cb.equal(root.get("providerId"), providerId));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern),
                        cb.like(cb.lower(root.get("providerModelId")), pattern)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
