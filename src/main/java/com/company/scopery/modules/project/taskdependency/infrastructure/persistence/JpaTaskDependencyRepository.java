package com.company.scopery.modules.project.taskdependency.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyStatus;
import com.company.scopery.modules.project.taskdependency.domain.enums.TaskDependencyType;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependency;
import com.company.scopery.modules.project.taskdependency.domain.model.TaskDependencyRepository;
import com.company.scopery.modules.project.taskdependency.infrastructure.mapper.TaskDependencyPersistenceMapper;
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
public class JpaTaskDependencyRepository implements TaskDependencyRepository {

    private final SpringDataTaskDependencyJpaRepository springDataRepository;
    private final TaskDependencyPersistenceMapper mapper;

    public JpaTaskDependencyRepository(SpringDataTaskDependencyJpaRepository springDataRepository,
                                        TaskDependencyPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public TaskDependency save(TaskDependency dep) {
        TaskDependencyJpaEntity entity = mapper.toJpaEntity(dep);
        TaskDependencyJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TaskDependency> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public boolean existsByPredecessorAndSuccessorAndType(UUID predecessorId, UUID successorId, TaskDependencyType type) {
        return springDataRepository.existsByPredecessorTaskIdAndSuccessorTaskIdAndDependencyType(
                predecessorId, successorId, type.name());
    }

    @Override
    public List<TaskDependency> findActiveDependenciesFrom(UUID taskId) {
        return springDataRepository.findActiveDependenciesFrom(taskId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PageResult<TaskDependency> search(UUID projectId, UUID taskId, TaskDependencyStatus status, PageQuery pageQuery) {
        Specification<TaskDependencyJpaEntity> spec = buildSearchSpec(projectId, taskId, status);
        Pageable pageable = toPageable(pageQuery);
        Page<TaskDependency> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<TaskDependencyJpaEntity> buildSearchSpec(UUID projectId, UUID taskId, TaskDependencyStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("projectId"), projectId));
            if (taskId != null) {
                predicates.add(cb.or(
                        cb.equal(root.get("predecessorTaskId"), taskId),
                        cb.equal(root.get("successorTaskId"), taskId)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
