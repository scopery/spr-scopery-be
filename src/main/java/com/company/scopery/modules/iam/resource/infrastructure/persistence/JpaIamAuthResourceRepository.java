package com.company.scopery.modules.iam.resource.infrastructure.persistence;

import com.company.scopery.modules.iam.resource.domain.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.IamResourceType;
import com.company.scopery.modules.iam.resource.infrastructure.mapper.IamAuthResourcePersistenceMapper;
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
public class JpaIamAuthResourceRepository implements IamAuthResourceRepository {

    private final SpringDataIamAuthResourceJpaRepository springDataRepository;
    private final IamAuthResourcePersistenceMapper mapper;

    public JpaIamAuthResourceRepository(SpringDataIamAuthResourceJpaRepository springDataRepository,
                                         IamAuthResourcePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public IamAuthResource save(IamAuthResource resource) {
        IamAuthResourceJpaEntity entity = mapper.toJpaEntity(resource);
        IamAuthResourceJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<IamAuthResource> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<IamAuthResource> findByRefIdAndResourceType(UUID refId, IamResourceType resourceType) {
        return springDataRepository.findByRefIdAndResourceType(refId, resourceType.name())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByCodeAndResourceType(IamResourceCode code, IamResourceType resourceType) {
        return springDataRepository.existsByCodeAndResourceType(code.value(), resourceType.name());
    }

    @Override
    public Page<IamAuthResource> findAll(String keyword, IamResourceType resourceType,
                                          IamResourceStatus status, Pageable pageable) {
        Specification<IamAuthResourceJpaEntity> spec = buildSpec(keyword, resourceType, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<IamAuthResourceJpaEntity> buildSpec(String keyword,
                                                               IamResourceType resourceType,
                                                               IamResourceStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), like),
                        cb.like(cb.lower(root.get("name")), like)
                ));
            }
            if (resourceType != null) {
                predicates.add(cb.equal(root.get("resourceType"), resourceType.name()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
