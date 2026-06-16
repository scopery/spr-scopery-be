package com.company.scopery.modules.workspace.organization.infrastructure.persistence;

import com.company.scopery.modules.workspace.organization.domain.Organization;
import com.company.scopery.modules.workspace.organization.domain.OrganizationCode;
import com.company.scopery.modules.workspace.organization.domain.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.OrganizationStatus;
import com.company.scopery.modules.workspace.organization.infrastructure.mapper.OrganizationPersistenceMapper;
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
public class JpaOrganizationRepository implements OrganizationRepository {

    private final SpringDataOrganizationJpaRepository springDataRepository;
    private final OrganizationPersistenceMapper mapper;

    public JpaOrganizationRepository(SpringDataOrganizationJpaRepository springDataRepository,
                                      OrganizationPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Organization save(Organization organization) {
        OrganizationJpaEntity entity = mapper.toJpaEntity(organization);
        OrganizationJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Organization> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(OrganizationCode code) {
        return springDataRepository.existsByCode(code.value());
    }

    @Override
    public Page<Organization> findAll(String keyword, UUID ownerUserId, OrganizationStatus status, Pageable pageable) {
        Specification<OrganizationJpaEntity> spec = buildSearchSpec(keyword, ownerUserId, status);
        return springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    private Specification<OrganizationJpaEntity> buildSearchSpec(String keyword, UUID ownerUserId,
                                                                   OrganizationStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)
                ));
            }
            if (ownerUserId != null) {
                predicates.add(cb.equal(root.get("ownerUserId"), ownerUserId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
