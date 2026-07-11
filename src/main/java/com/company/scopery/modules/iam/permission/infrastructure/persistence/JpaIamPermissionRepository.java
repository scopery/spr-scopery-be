package com.company.scopery.modules.iam.permission.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionAssignableSubjectType;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionCategory;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.permission.domain.enums.IamDataAccessPolicy;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionRiskLevel;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;
import com.company.scopery.modules.iam.permission.domain.enums.IamResourceScopeLevel;
import com.company.scopery.modules.iam.permission.infrastructure.mapper.IamPermissionPersistenceMapper;
import jakarta.persistence.criteria.Path;
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
public class JpaIamPermissionRepository implements IamPermissionRepository {

    private final SpringDataIamPermissionJpaRepository springDataRepository;
    private final IamPermissionPersistenceMapper mapper;

    public JpaIamPermissionRepository(SpringDataIamPermissionJpaRepository springDataRepository,
                                      IamPermissionPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamPermission save(IamPermission permission) {
        IamPermissionJpaEntity entity = mapper.toJpaEntity(permission);
        IamPermissionJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<IamPermission> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<IamPermission> findByCode(IamPermissionCode code) {
        return springDataRepository.findByCode(code.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(IamPermissionCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public PageResult<IamPermission> findAll(String keyword, String moduleCode,
                                       IamResourceScopeLevel resourceScopeLevel,
                                       IamDataAccessPolicy dataAccessPolicy,
                                       IamPermissionCategory permissionCategory,
                                       IamPermissionRiskLevel riskLevel,
                                       IamPermissionAssignableSubjectType assignableSubjectType,
                                       IamPermissionStatus status,
                                       PageQuery pageQuery) {
        Specification<IamPermissionJpaEntity> spec = buildSpec(
                keyword, moduleCode, resourceScopeLevel, dataAccessPolicy,
                permissionCategory, riskLevel, assignableSubjectType, status);
        Pageable pageable = toPageable(pageQuery);
        Page<IamPermission> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<IamPermissionJpaEntity> buildSpec(String keyword, String moduleCode,
                                                            IamResourceScopeLevel resourceScopeLevel,
                                                            IamDataAccessPolicy dataAccessPolicy,
                                                            IamPermissionCategory permissionCategory,
                                                            IamPermissionRiskLevel riskLevel,
                                                            IamPermissionAssignableSubjectType assignableSubjectType,
                                                            IamPermissionStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), like),
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }
            if (moduleCode != null && !moduleCode.isBlank()) {
                predicates.add(cb.equal(root.get("moduleCode"), moduleCode.trim().toUpperCase()));
            }
            if (resourceScopeLevel != null) {
                predicates.add(cb.equal(root.get("resourceScopeLevel"), resourceScopeLevel.name()));
            }
            if (dataAccessPolicy != null) {
                predicates.add(cb.equal(root.get("dataAccessPolicy"), dataAccessPolicy.name()));
            }
            if (permissionCategory != null) {
                predicates.add(cb.equal(root.get("permissionCategory"), permissionCategory.name()));
            }
            if (riskLevel != null) {
                predicates.add(cb.equal(root.get("riskLevel"), riskLevel.name()));
            }
            if (assignableSubjectType != null) {
                Path<String> field = root.get("assignableSubjectTypes");
                String token = assignableSubjectType.name();
                predicates.add(cb.or(
                        cb.equal(field, token),
                        cb.like(field, token + ",%"),
                        cb.like(field, "%," + token + ",%"),
                        cb.like(field, "%," + token)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
