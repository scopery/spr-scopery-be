package com.company.scopery.modules.project.task.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.task.domain.enums.TaskStatus;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.task.infrastructure.mapper.TaskPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTaskRepository implements TaskRepository {

    private final SpringDataTaskJpaRepository springDataRepository;
    private final TaskPersistenceMapper mapper;

    public JpaTaskRepository(SpringDataTaskJpaRepository springDataRepository,
                              TaskPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Task save(Task task) {
        TaskJpaEntity entity = mapper.toJpaEntity(task);
        TaskJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProjectIdAndCode(UUID projectId, String code) {
        return springDataRepository.existsByProjectIdAndCode(projectId, code);
    }

    @Override
    public PageResult<Task> search(UUID projectId, UUID phaseId, UUID wbsNodeId,
                             TaskStatus status, TaskPriority priority,
                             String keyword, PageQuery pageQuery) {
        Specification<TaskJpaEntity> spec = buildSearchSpec(projectId, phaseId, wbsNodeId, status, priority, keyword);
        Pageable pageable = toPageable(pageQuery);
        Page<Task> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    @Override
    public List<Task> findAllByWbsNodeId(UUID wbsNodeId) {
        return springDataRepository.findAllByWbsNodeId(wbsNodeId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Task> findAllByProjectId(UUID projectId) {
        return springDataRepository.findAllByProjectId(projectId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Task> findDueSoonReminderCandidates(LocalDate dueDate, Collection<TaskStatus> excludedStatuses, int limit) {
        List<String> excluded = excludedStatuses.stream().map(Enum::name).toList();
        return springDataRepository.findDueSoonCandidates(dueDate, excluded, PageRequest.of(0, limit))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Task> findOverdueReminderCandidates(LocalDate beforeDate, Collection<TaskStatus> excludedStatuses, int limit) {
        List<String> excluded = excludedStatuses.stream().map(Enum::name).toList();
        return springDataRepository.findOverdueCandidates(beforeDate, excluded, PageRequest.of(0, limit))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public PageResult<Task> searchForUser(Collection<UUID> projectIds, UUID userId,
                                          List<String> statuses, boolean excludeTerminal,
                                          LocalDate dateFrom, LocalDate dateTo,
                                          boolean dueDateOnly, boolean includeUndated,
                                          PageQuery pageQuery) {
        if (projectIds.isEmpty()) return PageResult.fromSpringPage(org.springframework.data.domain.Page.empty());
        Specification<TaskJpaEntity> spec = buildUserSpec(projectIds, userId, statuses, excludeTerminal,
                dateFrom, dateTo, dueDateOnly, includeUndated);
        Pageable pageable = toMyWorkPageable(pageQuery);
        Page<Task> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    @Override
    public long countForUser(Collection<UUID> projectIds, UUID userId,
                             List<String> statuses, boolean excludeTerminal,
                             LocalDate dateFrom, LocalDate dateTo,
                             boolean dueDateOnly, boolean includeUndated) {
        if (projectIds.isEmpty()) return 0L;
        Specification<TaskJpaEntity> spec = buildUserSpec(projectIds, userId, statuses, excludeTerminal,
                dateFrom, dateTo, dueDateOnly, includeUndated);
        return springDataRepository.count(spec);
    }

    @Override
    public List<Object[]> findDailySummaryRows(UUID workspaceId, Collection<UUID> userIds, LocalDate date) {
        if (userIds.isEmpty()) return List.of();
        return springDataRepository.findDailySummaryRows(workspaceId, userIds, date);
    }

    @Override
    public long countUndatedForUser(Collection<UUID> projectIds, UUID userId, boolean excludeTerminal) {
        if (projectIds.isEmpty()) return 0L;
        Specification<TaskJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get("projectId").in(projectIds));
            predicates.add(cb.equal(root.get("inChargeUserId"), userId));
            predicates.add(cb.isNull(root.get("dueDate")));
            predicates.add(cb.isNull(root.get("plannedStartDate")));
            if (excludeTerminal) predicates.add(root.get("status").in(TERMINAL_STATUSES).not());
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return springDataRepository.count(spec);
    }

    private Pageable toMyWorkPageable(PageQuery pageQuery) {
        // nullsLast() is not supported by JPA Criteria API; PostgreSQL ASC already sorts NULLs last
        Sort sort = Sort.by(
                Sort.Order.asc("dueDate"),
                Sort.Order.desc("updatedAt")
        );
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private static final List<String> TERMINAL_STATUSES = List.of("DONE", "CANCELLED", "ARCHIVED");

    private Specification<TaskJpaEntity> buildUserSpec(Collection<UUID> projectIds, UUID userId,
                                                        List<String> statuses, boolean excludeTerminal,
                                                        LocalDate dateFrom, LocalDate dateTo,
                                                        boolean dueDateOnly, boolean includeUndated) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(root.get("projectId").in(projectIds));
            predicates.add(cb.equal(root.get("inChargeUserId"), userId));

            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }
            if (excludeTerminal) {
                predicates.add(root.get("status").in(TERMINAL_STATUSES).not());
            }

            if (!includeUndated) {
                if (dueDateOnly) {
                    // OVERDUE: dueDate IS NOT NULL AND dueDate <= dateTo
                    predicates.add(cb.isNotNull(root.get("dueDate")));
                    if (dateTo != null) {
                        predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), dateTo));
                    }
                } else if (dateFrom != null && dateTo != null) {
                    // Overlap: (dueDate in window) OR (startDate in window) OR (startDate..dueDate overlaps window)
                    Predicate dueDateInWindow = cb.and(
                            cb.isNotNull(root.get("dueDate")),
                            cb.between(root.get("dueDate"), dateFrom, dateTo));
                    Predicate startDateInWindow = cb.and(
                            cb.isNotNull(root.get("plannedStartDate")),
                            cb.between(root.get("plannedStartDate"), dateFrom, dateTo));
                    Predicate spanOverlaps = cb.and(
                            cb.isNotNull(root.get("plannedStartDate")),
                            cb.isNotNull(root.get("dueDate")),
                            cb.lessThanOrEqualTo(root.get("plannedStartDate"), dateTo),
                            cb.greaterThanOrEqualTo(root.get("dueDate"), dateFrom));
                    if (!excludeTerminal) {
                        // includeCompleted=true: also match tasks completed (UTC) within the window
                        Instant windowStart = dateFrom.atStartOfDay(ZoneOffset.UTC).toInstant();
                        Instant windowEnd = dateTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
                        Predicate completedInWindow = cb.and(
                                cb.isNotNull(root.<Instant>get("completedAt")),
                                cb.greaterThanOrEqualTo(root.<Instant>get("completedAt"), windowStart),
                                cb.lessThan(root.<Instant>get("completedAt"), windowEnd));
                        predicates.add(cb.or(dueDateInWindow, startDateInWindow, spanOverlaps, completedInWindow));
                    } else {
                        predicates.add(cb.or(dueDateInWindow, startDateInWindow, spanOverlaps));
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<TaskJpaEntity> buildSearchSpec(UUID projectId, UUID phaseId, UUID wbsNodeId,
                                                          TaskStatus status, TaskPriority priority,
                                                          String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("projectId"), projectId));
            if (phaseId != null) {
                predicates.add(cb.equal(root.get("projectPhaseId"), phaseId));
            }
            if (wbsNodeId != null) {
                predicates.add(cb.equal(root.get("wbsNodeId"), wbsNodeId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority.name()));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
