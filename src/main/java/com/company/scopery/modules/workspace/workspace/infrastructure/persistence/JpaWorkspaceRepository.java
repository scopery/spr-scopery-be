package com.company.scopery.modules.workspace.workspace.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.infrastructure.mapper.WorkspacePersistenceMapper;
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
public class JpaWorkspaceRepository implements WorkspaceRepository {

    private final SpringDataWorkspaceJpaRepository springDataRepository;
    private final WorkspacePersistenceMapper mapper;

    public JpaWorkspaceRepository(SpringDataWorkspaceJpaRepository springDataRepository,
                                   WorkspacePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Workspace save(Workspace workspace) {
        WorkspaceJpaEntity entity = mapper.toJpaEntity(workspace);
        WorkspaceJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Workspace> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Workspace> findByCode(WorkspaceCode code) {
        return springDataRepository.findByCode(code.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrganizationIdAndCode(UUID organizationId, WorkspaceCode code) {
        return springDataRepository.existsByOrganizationIdAndCode(organizationId, code.value());
    }

    @Override
    public List<Workspace> findActiveByMemberId(UUID userId) {
        return springDataRepository.findActiveByMemberId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Workspace> findAllActiveByOrganizationId(UUID organizationId) {
        return springDataRepository.findAllByOrganizationIdAndStatus(organizationId, WorkspaceStatus.ACTIVE.name())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public PageResult<Workspace> findAll(UUID organizationId, UUID ownerUserId, String keyword, WorkspaceStatus status,
                                          PageQuery pageQuery) {
        Specification<WorkspaceJpaEntity> spec = buildSearchSpec(organizationId, ownerUserId, keyword, status);
        Pageable pageable = toPageable(pageQuery);
        Page<Workspace> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<WorkspaceJpaEntity> buildSearchSpec(UUID organizationId, UUID ownerUserId,
                                                               String keyword, WorkspaceStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (organizationId != null) {
                predicates.add(cb.equal(root.get("organizationId"), organizationId));
            }
            if (ownerUserId != null) {
                predicates.add(cb.equal(root.get("ownerUserId"), ownerUserId));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
