package com.company.scopery.modules.resourcecapacity.projectallocation.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.ProjectResourceAllocationStatus;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.projectallocation.infrastructure.mapper.ProjectResourceAllocationPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectResourceAllocationRepository implements ProjectResourceAllocationRepository {

    private final SpringDataProjectResourceAllocationJpaRepository springDataRepository;
    private final ProjectResourceAllocationPersistenceMapper mapper;

    public JpaProjectResourceAllocationRepository(SpringDataProjectResourceAllocationJpaRepository springDataRepository,
                                                  ProjectResourceAllocationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public ProjectResourceAllocation save(ProjectResourceAllocation allocation) {
        ProjectResourceAllocationJpaEntity entity = mapper.toJpaEntity(allocation);
        ProjectResourceAllocationJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ProjectResourceAllocation> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ProjectResourceAllocation> findActiveByUserId(UUID userId) {
        return springDataRepository
                .findByUserIdAndStatus(userId, ProjectResourceAllocationStatus.ACTIVE.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ProjectResourceAllocation> findActiveByUserIdAndDateRange(UUID userId, LocalDate from, LocalDate to) {
        return findActiveByUserId(userId).stream()
                .filter(allocation -> allocation.overlaps(from, to))
                .toList();
    }

    @Override
    public List<ProjectResourceAllocation> findActiveByProjectId(UUID projectId) {
        return springDataRepository
                .findByProjectIdAndStatus(projectId, ProjectResourceAllocationStatus.ACTIVE.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PageResult<ProjectResourceAllocation> search(UUID workspaceId, UUID projectId, UUID workspaceMemberId,
                                                        UUID userId, ProjectResourceAllocationStatus status,
                                                        PageQuery pageQuery) {
        Specification<ProjectResourceAllocationJpaEntity> spec =
                buildSearchSpec(workspaceId, projectId, workspaceMemberId, userId, status);
        Pageable pageable = toPageable(pageQuery);
        Page<ProjectResourceAllocation> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<ProjectResourceAllocationJpaEntity> buildSearchSpec(
            UUID workspaceId, UUID projectId, UUID workspaceMemberId, UUID userId,
            ProjectResourceAllocationStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            if (projectId != null) {
                predicates.add(cb.equal(root.get("projectId"), projectId));
            }
            if (workspaceMemberId != null) {
                predicates.add(cb.equal(root.get("workspaceMemberId"), workspaceMemberId));
            }
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
