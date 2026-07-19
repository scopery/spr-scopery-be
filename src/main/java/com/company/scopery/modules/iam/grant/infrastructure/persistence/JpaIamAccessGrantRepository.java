package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.infrastructure.mapper.IamAccessGrantPersistenceMapper;
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
import java.time.Instant;

@Repository
public class JpaIamAccessGrantRepository implements IamAccessGrantRepository {

    private final SpringDataIamAccessGrantJpaRepository springDataRepository;
    private final IamAccessGrantPersistenceMapper mapper;

    public JpaIamAccessGrantRepository(SpringDataIamAccessGrantJpaRepository springDataRepository,
                                        IamAccessGrantPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamAccessGrant save(IamAccessGrant grant) {
        IamAccessGrantJpaEntity entity = mapper.toJpaEntity(grant);
        IamAccessGrantJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<IamAccessGrant> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsBySubjectIdAndResourceId(UUID subjectId, UUID resourceId) {
        return springDataRepository.existsBySubjectIdAndResourceId(subjectId, resourceId);
    }

    @Override
    public PageResult<IamAccessGrant> findAll(UUID subjectId, UUID resourceId, UUID workspaceId,
                                         IamAccessGrantStatus status, PageQuery pageQuery) {
        Specification<IamAccessGrantJpaEntity> spec = buildSpec(subjectId, resourceId, workspaceId, status);
        Pageable pageable = toPageable(pageQuery);
        Page<IamAccessGrant> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    @Override
    public List<IamAccessGrant> findActiveBySubjectsAndResource(List<IamSubjectType> subjectTypes,
                                                                  List<UUID> subjectIds,
                                                                  UUID resourceId) {
        if (subjectTypes.size() != subjectIds.size()) {
            throw new IllegalArgumentException("Subject type/id collections must have the same size");
        }
        List<IamAccessGrant> result = new ArrayList<>();
        for (int i = 0; i < subjectTypes.size(); i++) {
            springDataRepository.findAllBySubjectTypeAndSubjectIdAndResourceIdAndStatus(
                            subjectTypes.get(i).name(), subjectIds.get(i), resourceId,
                            IamAccessGrantStatus.ACTIVE.name())
                    .stream().map(mapper::toDomain).forEach(result::add);
        }
        return result.stream().distinct().filter(grant -> grant.isEffectiveAt(Instant.now())).toList();
    }

    @Override
    public List<IamAccessGrant> findActiveByResource(UUID resourceId) {
        return springDataRepository.findAllByResourceIdAndStatus(resourceId, IamAccessGrantStatus.ACTIVE.name())
                .stream().map(mapper::toDomain).filter(grant -> grant.isEffectiveAt(Instant.now())).toList();
    }

    @Override
    public boolean hasActiveGlobalResourceGrantForSubjects(List<UUID> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) return false;
        return springDataRepository.existsActiveGlobalGrantForSubjects(subjectIds);
    }

    private Specification<IamAccessGrantJpaEntity> buildSpec(UUID subjectId, UUID resourceId,
                                                               UUID workspaceId,
                                                               IamAccessGrantStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (subjectId != null) {
                predicates.add(cb.equal(root.get("subjectId"), subjectId));
            }
            if (resourceId != null) {
                predicates.add(cb.equal(root.get("resourceId"), resourceId));
            }
            if (workspaceId != null) {
                predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
