package com.company.scopery.modules.project.wbs.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import com.company.scopery.modules.project.wbs.infrastructure.mapper.WbsNodePersistenceMapper;
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
public class JpaWbsNodeRepository implements WbsNodeRepository {

    private final SpringDataWbsNodeJpaRepository springDataRepository;
    private final WbsNodePersistenceMapper mapper;

    public JpaWbsNodeRepository(SpringDataWbsNodeJpaRepository springDataRepository,
                                 WbsNodePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public WbsNode save(WbsNode node) {
        WbsNodeJpaEntity entity = mapper.toJpaEntity(node);
        WbsNodeJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<WbsNode> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByProjectIdAndCode(UUID projectId, String code) {
        return springDataRepository.existsByProjectIdAndCode(projectId, code);
    }

    @Override
    public boolean existsBySortOrderUnderParent(UUID projectId, UUID parentId, int sortOrder) {
        if (parentId == null) {
            return springDataRepository.existsByProjectIdAndParentIdIsNullAndSortOrder(projectId, sortOrder);
        }
        return springDataRepository.existsByParentIdAndSortOrder(parentId, sortOrder);
    }

    @Override
    public boolean hasActiveChildrenOrLinkedTasks(UUID nodeId) {
        return springDataRepository.hasActiveChildren(nodeId)
                || springDataRepository.hasLinkedActiveTasks(nodeId);
    }

    @Override
    public List<WbsNode> findAllDescendants(UUID nodeId) {
        return springDataRepository.findById(nodeId)
                .map(n -> springDataRepository.findAllDescendants(n.getPath())
                        .stream().map(mapper::toDomain).toList())
                .orElse(List.of());
    }

    @Override
    public PageResult<WbsNode> search(UUID projectId, UUID phaseId, UUID parentId, WbsNodeStatus status, PageQuery pageQuery) {
        Specification<WbsNodeJpaEntity> spec = buildSearchSpec(projectId, phaseId, parentId, status);
        Pageable pageable = toPageable(pageQuery);
        Page<WbsNode> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    @Override
    public List<WbsNode> findAllByProjectId(UUID projectId) {
        return springDataRepository.findAllByProjectId(projectId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<WbsNodeJpaEntity> buildSearchSpec(UUID projectId, UUID phaseId,
                                                              UUID parentId, WbsNodeStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("projectId"), projectId));
            if (phaseId != null) {
                predicates.add(cb.equal(root.get("projectPhaseId"), phaseId));
            }
            if (parentId != null) {
                predicates.add(cb.equal(root.get("parentId"), parentId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
