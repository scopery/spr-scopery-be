package com.company.scopery.modules.iam.roleassignment.infrastructure.persistence;

import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.RoleAssigneeType;
import com.company.scopery.modules.iam.roleassignment.infrastructure.mapper.IamRoleAssignmentPersistenceMapper;
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
public class JpaIamRoleAssignmentRepository implements IamRoleAssignmentRepository {

    private final SpringDataIamRoleAssignmentJpaRepository springDataRepository;
    private final IamRoleAssignmentPersistenceMapper mapper;

    public JpaIamRoleAssignmentRepository(SpringDataIamRoleAssignmentJpaRepository springDataRepository,
                                           IamRoleAssignmentPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamRoleAssignment save(IamRoleAssignment assignment) {
        IamRoleAssignmentJpaEntity entity = mapper.toJpaEntity(assignment);
        IamRoleAssignmentJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<IamRoleAssignment> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsActiveAssignment(RoleAssigneeType assigneeType, UUID assigneeId,
                                           UUID roleId, UUID workspaceId) {
        return springDataRepository.existsActiveAssignment(
                assigneeType.name(), assigneeId, roleId, workspaceId);
    }

    @Override
    public List<IamRoleAssignment> findActiveByAssigneeId(UUID assigneeId) {
        return springDataRepository.findActiveByAssigneeId(assigneeId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Page<IamRoleAssignment> findAll(UUID roleId, UUID assigneeId,
                                            RoleAssigneeType assigneeType,
                                            IamRoleAssignmentStatus status,
                                            UUID workspaceId, Pageable pageable) {
        Specification<IamRoleAssignmentJpaEntity> spec = buildSpec(roleId, assigneeId, assigneeType, status, workspaceId);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<IamRoleAssignmentJpaEntity> buildSpec(UUID roleId, UUID assigneeId,
                                                                  RoleAssigneeType assigneeType,
                                                                  IamRoleAssignmentStatus status,
                                                                  UUID workspaceId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (roleId != null) {
                predicates.add(cb.equal(root.get("roleId"), roleId));
            }
            if (assigneeId != null) {
                predicates.add(cb.equal(root.get("assigneeId"), assigneeId));
            }
            if (assigneeType != null) {
                predicates.add(cb.equal(root.get("assigneeType"), assigneeType.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (workspaceId != null) {
                predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
