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

import java.util.ArrayList;
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
