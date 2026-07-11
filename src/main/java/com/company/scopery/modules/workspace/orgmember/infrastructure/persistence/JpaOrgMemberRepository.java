package com.company.scopery.modules.workspace.orgmember.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMemberStatus;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.orgmember.infrastructure.mapper.OrgMemberPersistenceMapper;
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
import java.util.stream.Collectors;

@Repository
public class JpaOrgMemberRepository implements OrgMemberRepository {

    private final SpringDataOrgMemberJpaRepository springDataRepository;
    private final OrgMemberPersistenceMapper mapper;

    public JpaOrgMemberRepository(SpringDataOrgMemberJpaRepository springDataRepository,
                                   OrgMemberPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public OrgMember save(OrgMember member) {
        OrgMemberJpaEntity entity = mapper.toJpaEntity(member);
        OrgMemberJpaEntity saved = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<OrgMember> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return springDataRepository.existsByOrganizationIdAndUserId(organizationId, userId);
    }

    @Override
    public Optional<OrgMember> findByOrganizationIdAndUserId(UUID organizationId, UUID userId) {
        return springDataRepository.findByOrganizationIdAndUserId(organizationId, userId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean isActiveMember(UUID organizationId, UUID userId) {
        return springDataRepository.existsByOrganizationIdAndUserIdAndStatus(
                organizationId, userId, OrgMemberStatus.ACTIVE.name());
    }

    @Override
    public List<OrgMember> findAllByUserId(UUID userId) {
        return springDataRepository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<OrgMember> findAll(UUID organizationId, UUID userId, OrgMemberStatus status,
                                          PageQuery pageQuery) {
        Specification<OrgMemberJpaEntity> spec = buildSpec(organizationId, userId, status);
        Pageable pageable = toPageable(pageQuery);
        Page<OrgMember> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    private Specification<OrgMemberJpaEntity> buildSpec(UUID organizationId, UUID userId,
                                                         OrgMemberStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("organizationId"), organizationId));
            if (userId != null) predicates.add(cb.equal(root.get("userId"), userId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status.name()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
