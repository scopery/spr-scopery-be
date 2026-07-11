package com.company.scopery.modules.workspace.orgteam.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface OrgTeamMemberRepository {

    OrgTeamMember save(OrgTeamMember member);

    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);

    Optional<OrgTeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId);

    void deleteByTeamIdAndUserId(UUID teamId, UUID userId);

    PageResult<OrgTeamMember> findAllByTeamId(UUID teamId, PageQuery pageQuery);

    List<OrgTeamMember> findAllByUserId(UUID userId);
}
