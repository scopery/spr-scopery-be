package com.company.scopery.modules.workspace.orgteam.infrastructure.persistence;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMember;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.orgteam.infrastructure.mapper.OrgTeamMemberPersistenceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOrgTeamMemberRepository implements OrgTeamMemberRepository {

    private final SpringDataOrgTeamMemberJpaRepository springDataRepository;
    private final OrgTeamMemberPersistenceMapper mapper;

    public JpaOrgTeamMemberRepository(SpringDataOrgTeamMemberJpaRepository springDataRepository,
                                       OrgTeamMemberPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public OrgTeamMember save(OrgTeamMember member) {
        OrgTeamMemberJpaEntity entity = mapper.toJpaEntity(member);
        return mapper.toDomain(springDataRepository.saveAndFlush(entity));
    }

    @Override
    public boolean existsByTeamIdAndUserId(UUID teamId, UUID userId) {
        return springDataRepository.existsByIdTeamIdAndIdUserId(teamId, userId);
    }

    @Override
    public Optional<OrgTeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId) {
        return springDataRepository.findByIdTeamIdAndIdUserId(teamId, userId).map(mapper::toDomain);
    }

    @Override
    public void deleteByTeamIdAndUserId(UUID teamId, UUID userId) {
        springDataRepository.deleteByIdTeamIdAndIdUserId(teamId, userId);
    }

    @Override
    public PageResult<OrgTeamMember> findAllByTeamId(UUID teamId, PageQuery pageQuery) {
        List<OrgTeamMember> all = springDataRepository.findAllByIdTeamId(teamId)
                .stream().map(mapper::toDomain).toList();
        Pageable pageable = toPageable(pageQuery);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<OrgTeamMember> page = start >= all.size() ? List.of() : all.subList(start, end);
        Page<OrgTeamMember> springPage = new PageImpl<>(page, pageable, all.size());
        return PageResult.fromSpringPage(springPage);
    }

    private Pageable toPageable(PageQuery pageQuery) {
        Sort sort = pageQuery.sortBy() != null
                ? Sort.by(pageQuery.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, pageQuery.sortBy())
                : Sort.unsorted();
        return PageRequest.of(pageQuery.page(), pageQuery.size(), sort);
    }

    @Override
    public List<OrgTeamMember> findAllByUserId(UUID userId) {
        return springDataRepository.findAllByIdUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
