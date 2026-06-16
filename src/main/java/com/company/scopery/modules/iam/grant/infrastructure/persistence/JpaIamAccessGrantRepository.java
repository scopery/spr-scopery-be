package com.company.scopery.modules.iam.grant.infrastructure.persistence;

import com.company.scopery.modules.iam.grant.domain.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.IamSubjectType;
import com.company.scopery.modules.iam.grant.infrastructure.mapper.IamAccessGrantPersistenceMapper;
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
    public Page<IamAccessGrant> findAll(UUID subjectId, UUID resourceId,
                                         IamAccessGrantStatus status, Pageable pageable) {
        Specification<IamAccessGrantJpaEntity> spec = buildSpec(subjectId, resourceId, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    public List<IamAccessGrant> findActiveBySubjectsAndResource(List<IamSubjectType> subjectTypes,
                                                                  List<UUID> subjectIds,
                                                                  UUID resourceId) {
        List<String> typeNames = subjectTypes.stream().map(IamSubjectType::name).toList();
        return springDataRepository.findActiveBySubjectsAndResource(typeNames, subjectIds, resourceId)
                .stream().map(mapper::toDomain).toList();
    }

    private Specification<IamAccessGrantJpaEntity> buildSpec(UUID subjectId, UUID resourceId,
                                                               IamAccessGrantStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (subjectId != null) {
                predicates.add(cb.equal(root.get("subjectId"), subjectId));
            }
            if (resourceId != null) {
                predicates.add(cb.equal(root.get("resourceId"), resourceId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
