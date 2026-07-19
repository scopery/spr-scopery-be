package com.company.scopery.modules.resourcecapacity.projectallocation.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.query.SearchProjectResourceAllocationQuery;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.response.ProjectResourceAllocationResponse;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.ProjectResourceAllocationStatus;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacitySortFields;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.util.CapacityEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectResourceAllocationQueryService {

    private final ProjectResourceAllocationRepository allocationRepository;
    private final CapacityWorkspaceAuthorizationService authorizationService;

    public ProjectResourceAllocationQueryService(ProjectResourceAllocationRepository allocationRepository,
                                                 CapacityWorkspaceAuthorizationService authorizationService) {
        this.allocationRepository = allocationRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public ProjectResourceAllocationResponse getAllocation(UUID id) {
        ProjectResourceAllocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> CapacityExceptions.allocationNotFound(id));
        authorizationService.requireAllocationView(allocation.workspaceId());
        return ProjectResourceAllocationResponse.from(allocation);
    }

    @Transactional(readOnly = true)
    public PageResult<ProjectResourceAllocationResponse> searchAllocations(SearchProjectResourceAllocationQuery query) {
        authorizationService.requireAllocationView(query.workspaceId());

        ProjectResourceAllocationStatus status = CapacityEnumParser.parseOptional(
                ProjectResourceAllocationStatus.class, query.status(),
                "PROJECT_ALLOCATION_INVALID_STATUS", "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), CapacitySortFields.START_DATE, false);

        return allocationRepository
                .search(query.workspaceId(), query.projectId(), query.workspaceMemberId(), query.userId(), status, pageQuery)
                .map(ProjectResourceAllocationResponse::from);
    }
}
