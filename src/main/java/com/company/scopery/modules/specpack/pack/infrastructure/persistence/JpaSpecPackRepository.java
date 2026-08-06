package com.company.scopery.modules.specpack.pack.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackStatus;
import com.company.scopery.modules.specpack.pack.domain.enums.SpecPackType;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPack;
import com.company.scopery.modules.specpack.pack.domain.model.SpecPackRepository;
import com.company.scopery.modules.specpack.pack.infrastructure.mapper.SpecPackPersistenceMapper;
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
public class JpaSpecPackRepository implements SpecPackRepository {

    private final SpringDataSpecPackJpaRepository springDataRepository;
    private final SpecPackPersistenceMapper mapper;

    public JpaSpecPackRepository(SpringDataSpecPackJpaRepository springDataRepository, SpecPackPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public SpecPack save(SpecPack specPack) {
        SpecPackJpaEntity entity = mapper.toJpaEntity(specPack);
        SpecPackJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SpecPack> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<SpecPack> findAllByProject(UUID projectId, String keyword, SpecPackType packType, SpecPackStatus status, PageQuery pageQuery) {
        Pageable pageable = toPageable(pageQuery);
        Specification<SpecPackJpaEntity> spec = buildSpec(projectId, keyword, packType, status);
        Page<SpecPackJpaEntity> page = springDataRepository.findAll(spec, pageable);
        return PageResult.fromSpringPage(page).map(mapper::toDomain);
    }

    private Specification<SpecPackJpaEntity> buildSpec(UUID projectId, String keyword, SpecPackType packType, SpecPackStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("projectId"), projectId));
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.trim().toLowerCase() + "%"));
            }
            if (packType != null) {
                predicates.add(cb.equal(root.get("packType"), packType.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }
}
