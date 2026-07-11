package com.company.scopery.modules.iam.resource.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.infrastructure.mapper.IamAuthResourcePersistenceMapper;
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
    public Optional<IamAuthResource> findByCodeAndResourceType(IamResourceCode code, IamResourceType resourceType) {
        return springDataRepository.findByCodeAndResourceType(code.value(), resourceType.name())
                .map(mapper::toDomain);
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
    public PageResult<IamAuthResource> findAll(String keyword, IamResourceType resourceType,
                                          IamResourceStatus status, PageQuery pageQuery) {
        Specification<IamAuthResourceJpaEntity> spec = buildSpec(keyword, resourceType, status);
        Pageable pageable = toPageable(pageQuery);
        Page<IamAuthResource> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    @Override
    public List<IamAuthResource> findAllByResourceTypeAndStatus(IamResourceType resourceType,
                                                                 IamResourceStatus status) {
        return springDataRepository.findAllByResourceTypeAndStatus(resourceType.name(), status.name())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<IamAuthResource> findAllByOrganizationId(UUID organizationId) {
        return springDataRepository.findAllByOrganizationId(organizationId).stream().map(mapper::toDomain).toList();
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
