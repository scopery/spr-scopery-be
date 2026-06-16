package com.company.scopery.modules.iam.role.infrastructure.persistence;

import com.company.scopery.modules.iam.role.domain.IamRole;
import com.company.scopery.modules.iam.role.domain.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.IamRoleStatus;
import com.company.scopery.modules.iam.role.infrastructure.mapper.IamRolePersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public boolean existsByCode(IamRoleCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public boolean existsByCodeAndWorkspaceId(IamRoleCode code, UUID workspaceId) {
        return springDataRepository.existsByCodeAndWorkspaceId(code.value(), workspaceId);
    }

    @Override
    public Page<IamRole> findAll(String keyword, UUID workspaceId, IamRoleScope roleScope,
                                  IamRoleSource roleSource, IamRoleStatus status,
                                  boolean includeDeleted, Pageable pageable) {
        Specification<IamRoleJpaEntity> spec = buildSpec(keyword, workspaceId, roleScope, roleSource, status, includeDeleted);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
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
