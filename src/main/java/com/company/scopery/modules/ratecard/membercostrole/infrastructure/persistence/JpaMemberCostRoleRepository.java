package com.company.scopery.modules.ratecard.membercostrole.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.membercostrole.domain.enums.MemberCostRoleStatus;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleAssignment;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleRepository;
import com.company.scopery.modules.ratecard.membercostrole.infrastructure.mapper.MemberCostRolePersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaMemberCostRoleRepository implements WorkspaceMemberCostRoleRepository {
    private final SpringDataMemberCostRoleJpaRepository springDataRepository;
    private final MemberCostRolePersistenceMapper mapper;

    public JpaMemberCostRoleRepository(SpringDataMemberCostRoleJpaRepository springDataRepository,
                                       MemberCostRolePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository; this.mapper = mapper;
    }

    @Override public WorkspaceMemberCostRoleAssignment save(WorkspaceMemberCostRoleAssignment assignment) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(assignment)));
    }
    @Override public Optional<WorkspaceMemberCostRoleAssignment> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }
    @Override public List<WorkspaceMemberCostRoleAssignment> findActiveDefaultsByMember(UUID workspaceMemberId) {
        return springDataRepository.findAllByWorkspaceMemberIdAndIsDefaultTrueAndStatus(
                        workspaceMemberId, MemberCostRoleStatus.ACTIVE.name())
                .stream().map(mapper::toDomain).toList();
    }
    @Override public PageResult<WorkspaceMemberCostRoleAssignment> search(
            UUID workspaceId, UUID workspaceMemberId, UUID userId, UUID costRoleId,
            MemberCostRoleStatus status, LocalDate effectiveDate, PageQuery pageQuery) {
        Specification<MemberCostRoleJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (workspaceId != null) predicates.add(cb.equal(root.get("workspaceId"), workspaceId));
            if (workspaceMemberId != null) predicates.add(cb.equal(root.get("workspaceMemberId"), workspaceMemberId));
            if (userId != null) predicates.add(cb.equal(root.get("userId"), userId));
            if (costRoleId != null) predicates.add(cb.equal(root.get("costRoleId"), costRoleId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status.name()));
            if (effectiveDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("effectiveFrom"), effectiveDate));
                predicates.add(cb.or(cb.isNull(root.get("effectiveTo")),
                        cb.greaterThanOrEqualTo(root.get("effectiveTo"), effectiveDate)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return PageResult.fromSpringPage(springDataRepository.findAll(spec,
                PageRequest.of(pageQuery.page(), pageQuery.size(), Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(mapper::toDomain));
    }
}
