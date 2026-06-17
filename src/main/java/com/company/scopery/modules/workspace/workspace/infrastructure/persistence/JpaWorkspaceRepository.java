package com.company.scopery.modules.workspace.workspace.infrastructure.persistence;

import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.infrastructure.mapper.WorkspacePersistenceMapper;
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
    public Page<Workspace> findAll(UUID organizationId, UUID ownerUserId, String keyword, WorkspaceStatus status,
                                    Pageable pageable) {
        Specification<WorkspaceJpaEntity> spec = buildSearchSpec(organizationId, ownerUserId, keyword, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
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
