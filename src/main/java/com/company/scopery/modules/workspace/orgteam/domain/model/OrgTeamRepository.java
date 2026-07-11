package com.company.scopery.modules.workspace.orgteam.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamStatus;
import com.company.scopery.modules.workspace.orgteam.domain.valueobject.OrgTeamCode;

import java.util.Optional;
import java.util.UUID;

public interface OrgTeamRepository {

    OrgTeam save(OrgTeam team);

    Optional<OrgTeam> findById(UUID id);

    boolean existsByOrganizationIdAndCode(UUID organizationId, OrgTeamCode code);

    PageResult<OrgTeam> findAll(UUID organizationId, String keyword, OrgTeamStatus status, PageQuery pageQuery);
}
