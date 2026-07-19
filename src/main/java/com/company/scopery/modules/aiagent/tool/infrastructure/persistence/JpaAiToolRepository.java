package com.company.scopery.modules.aiagent.tool.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolStatus;
import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolRepository;
import com.company.scopery.modules.aiagent.tool.domain.valueobject.AiToolCode;
import com.company.scopery.modules.aiagent.tool.infrastructure.mapper.AiToolPersistenceMapper;
import com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity.AiToolJpaEntity;
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
public class JpaAiToolRepository implements AiToolRepository {

    private final SpringDataAiToolJpaRepository springDataRepository;
    private final AiToolPersistenceMapper mapper;

    public JpaAiToolRepository(SpringDataAiToolJpaRepository springDataRepository,
                               AiToolPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public AiTool save(AiTool tool) {
        AiToolJpaEntity saved = springDataRepository.saveAndFlush(mapper.toJpaEntity(tool));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AiTool> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<AiTool> findByCode(AiToolCode code) {
        return springDataRepository.findByCode(code.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(AiToolCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public PageResult<AiTool> findAll(String category, AiToolStatus status, String codeOrName, PageQuery pageQuery) {
        Specification<AiToolJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category.trim()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (codeOrName != null && !codeOrName.isBlank()) {
                String pattern = "%" + codeOrName.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), pattern),
                        cb.like(cb.lower(root.get("name")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
        Page<AiTool> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }
}
