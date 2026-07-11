package com.company.scopery.modules.workspace.orgteam.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamStatus;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.orgteam.domain.valueobject.OrgTeamCode;
import com.company.scopery.modules.workspace.orgteam.infrastructure.mapper.OrgTeamPersistenceMapper;
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
public class JpaOrgTeamRepository implements OrgTeamRepository {

    private final SpringDataOrgTeamJpaRepository springDataRepository;
    private final OrgTeamPersistenceMapper mapper;

    public JpaOrgTeamRepository(SpringDataOrgTeamJpaRepository springDataRepository,
                                 OrgTeamPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public OrgTeam save(OrgTeam team) {
        OrgTeamJpaEntity entity = mapper.toJpaEntity(team);
        return mapper.toDomain(springDataRepository.saveAndFlush(entity));
    }

    @Override
    public Optional<OrgTeam> findById(UUID id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrganizationIdAndCode(UUID organizationId, OrgTeamCode code) {
        return springDataRepository.existsByOrganizationIdAndCode(organizationId, code.value());
    }

    @Override
    public PageResult<OrgTeam> findAll(UUID organizationId, String keyword, OrgTeamStatus status, PageQuery pageQuery) {
        Specification<OrgTeamJpaEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("organizationId"), organizationId));
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status.name()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = toPageable(pageQuery);
        Page<OrgTeam> page = springDataRepository.findAll(spec, pageable).map(mapper::toDomain);
        return PageResult.fromSpringPage(page);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }
}
