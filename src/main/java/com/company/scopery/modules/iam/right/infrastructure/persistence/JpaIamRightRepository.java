package com.company.scopery.modules.iam.right.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.enums.IamRightStatus;
import com.company.scopery.modules.iam.right.infrastructure.mapper.IamRightPersistenceMapper;
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
public class JpaIamRightRepository implements IamRightRepository {

    private final SpringDataIamRightJpaRepository springDataRepository;
    private final IamRightPersistenceMapper mapper;

    public JpaIamRightRepository(SpringDataIamRightJpaRepository springDataRepository,
                                   IamRightPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamRight save(IamRight right) {
        IamRightJpaEntity entity = mapper.toJpaEntity(right);
        IamRightJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<IamRight> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<IamRight> findByCode(IamRightCode code) {
        return springDataRepository.findByCode(code.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(IamRightCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public PageResult<IamRight> findAll(String keyword, String module, IamRightStatus status, PageQuery pageQuery) {
        Specification<IamRightJpaEntity> spec = buildSpec(keyword, module, status);
        Pageable pageable = toPageable(pageQuery);
        Page<IamRight> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<IamRightJpaEntity> buildSpec(String keyword, String module, IamRightStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), like),
                        cb.like(cb.lower(root.get("name")), like)
                ));
            }
            if (module != null && !module.isBlank()) {
                predicates.add(cb.equal(root.get("module"), module.toUpperCase()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
