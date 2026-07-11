package com.company.scopery.modules.workspace.member.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.infrastructure.mapper.WorkspaceMemberPersistenceMapper;
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
public class JpaWorkspaceMemberRepository implements WorkspaceMemberRepository {

    private final SpringDataWorkspaceMemberJpaRepository springDataRepository;
    private final WorkspaceMemberPersistenceMapper mapper;

    public JpaWorkspaceMemberRepository(SpringDataWorkspaceMemberJpaRepository springDataRepository,
                                         WorkspaceMemberPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkspaceMember save(WorkspaceMember member) {
        WorkspaceMemberJpaEntity entity = mapper.toJpaEntity(member);
        WorkspaceMemberJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<WorkspaceMember> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        return springDataRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }

    @Override
    public Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        return springDataRepository.findByWorkspaceIdAndUserId(workspaceId, userId).map(mapper::toDomain);
    }

    @Override
    public boolean isActiveMember(UUID workspaceId, UUID userId) {
        return springDataRepository.existsByWorkspaceIdAndUserIdAndStatus(
                workspaceId, userId, WorkspaceMemberStatus.ACTIVE.name());
    }

    @Override
    public PageResult<WorkspaceMember> findAll(UUID workspaceId, UUID userId, WorkspaceMemberStatus status,
                                                PageQuery pageQuery) {
        Specification<WorkspaceMemberJpaEntity> spec = buildSearchSpec(workspaceId, userId, status);
        Pageable pageable = toPageable(pageQuery);
        Page<WorkspaceMember> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<WorkspaceMemberJpaEntity> buildSearchSpec(UUID workspaceId, UUID userId,
                                                                      WorkspaceMemberStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
