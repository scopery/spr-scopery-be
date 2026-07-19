package com.company.scopery.modules.resourcecapacity.projectallocation.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.ProjectResourceAllocationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectResourceAllocationRepository {

    ProjectResourceAllocation save(ProjectResourceAllocation allocation);

    Optional<ProjectResourceAllocation> findById(UUID id);

    List<ProjectResourceAllocation> findActiveByUserId(UUID userId);

    List<ProjectResourceAllocation> findActiveByUserIdAndDateRange(UUID userId, LocalDate from, LocalDate to);

    List<ProjectResourceAllocation> findActiveByProjectId(UUID projectId);

    PageResult<ProjectResourceAllocation> search(UUID workspaceId, UUID projectId, UUID workspaceMemberId,
                                                 UUID userId, ProjectResourceAllocationStatus status,
                                                 PageQuery pageQuery);
}
