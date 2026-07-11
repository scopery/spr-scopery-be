package com.company.scopery.modules.iam.role.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.role.domain.model.IamRole;
import com.company.scopery.modules.iam.role.domain.valueobject.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleStatus;
import com.company.scopery.modules.iam.role.infrastructure.mapper.IamRolePersistenceMapper;
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
public class JpaIamRoleRepository implements IamRoleRepository {

    private final SpringDataIamRoleJpaRepository springDataRepository;
    private final IamRolePersistenceMapper mapper;

    public JpaIamRoleRepository(SpringDataIamRoleJpaRepository springDataRepository,
                                  IamRolePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamRole save(IamRole role) {
        IamRoleJpaEntity entity = mapper.toJpaEntity(role);
        IamRoleJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<IamRole> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<IamRole> findByCode(IamRoleCode code) {
        return springDataRepository.findByCode(code.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(IamRoleCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public boolean existsByCodeAndWorkspaceId(IamRoleCode code, UUID workspaceId) {
        return springDataRepository.existsByCodeAndWorkspaceId(code.value(), workspaceId);
    }

    @Override
    public PageResult<IamRole> findAll(String keyword, UUID workspaceId, IamRoleScope roleScope,
                                  IamRoleSource roleSource, IamRoleStatus status,
                                  boolean includeDeleted, PageQuery pageQuery) {
        Specification<IamRoleJpaEntity> spec = buildSpec(keyword, workspaceId, roleScope, roleSource, status, includeDeleted);
        Pageable pageable = toPageable(pageQuery);
        Page<IamRole> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<IamRoleJpaEntity> buildSpec(String keyword, UUID workspaceId,
                                                       IamRoleScope roleScope, IamRoleSource roleSource,
                                                       IamRoleStatus status, boolean includeDeleted) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), like),
                        cb.like(cb.lower(root.get("name")), like)
                ));
            }
            if (workspaceId != null) {
                predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            }
            if (roleScope != null) {
                predicates.add(cb.equal(root.get("roleScope"), roleScope.name()));
            }
            if (roleSource != null) {
                predicates.add(cb.equal(root.get("roleSource"), roleSource.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (!includeDeleted) {
                predicates.add(cb.isNull(root.get("deletedAt")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
