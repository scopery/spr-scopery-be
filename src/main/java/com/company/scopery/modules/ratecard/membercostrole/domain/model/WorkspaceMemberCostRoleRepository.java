package com.company.scopery.modules.ratecard.membercostrole.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.ratecard.membercostrole.domain.enums.MemberCostRoleStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberCostRoleRepository {
    WorkspaceMemberCostRoleAssignment save(WorkspaceMemberCostRoleAssignment assignment);
    Optional<WorkspaceMemberCostRoleAssignment> findById(UUID id);
    List<WorkspaceMemberCostRoleAssignment> findActiveDefaultsByMember(UUID workspaceMemberId);
    PageResult<WorkspaceMemberCostRoleAssignment> search(UUID workspaceId, UUID workspaceMemberId, UUID userId,
                                                         UUID costRoleId, MemberCostRoleStatus status,
                                                         LocalDate effectiveDate, PageQuery pageQuery);
}
