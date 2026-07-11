package com.company.scopery.modules.workspace.orgmember.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMemberStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface OrgMemberRepository {

    OrgMember save(OrgMember member);

    Optional<OrgMember> findById(UUID id);

    boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    Optional<OrgMember> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);

    boolean isActiveMember(UUID organizationId, UUID userId);

    List<OrgMember> findAllByUserId(UUID userId);

    PageResult<OrgMember> findAll(UUID organizationId, UUID userId, OrgMemberStatus status, PageQuery pageQuery);
}
