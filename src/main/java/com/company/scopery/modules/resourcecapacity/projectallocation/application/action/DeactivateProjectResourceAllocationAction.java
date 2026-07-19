package com.company.scopery.modules.resourcecapacity.projectallocation.application.action;

import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.DeactivateProjectResourceAllocationCommand;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.response.ProjectResourceAllocationResponse;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeactivateProjectResourceAllocationAction {

    private final ProjectResourceAllocationRepository allocationRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public DeactivateProjectResourceAllocationAction(ProjectResourceAllocationRepository allocationRepository,
                                                     CapacityActivityLogger activityLogger,
                                                     CapacityWorkspaceAuthorizationService authorizationService,
                                                     CapacityPlatformPublisher platformPublisher) {
        this.allocationRepository = allocationRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectResourceAllocationResponse execute(DeactivateProjectResourceAllocationCommand cmd) {
        ProjectResourceAllocation allocation = allocationRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.allocationNotFound(cmd.id()));
        authorizationService.requireAllocationUpdate(allocation.workspaceId());

        ProjectResourceAllocation saved = allocationRepository.save(allocation.deactivate());

        platformPublisher.enqueueAllocation(saved, "PROJECT_ALLOCATION_DEACTIVATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.PROJECT_RESOURCE_ALLOCATION,
                saved.id(),
                CapacityActivityActions.PROJECT_ALLOCATION_DEACTIVATED,
                "Project resource allocation deactivated: " + saved.id());

        return ProjectResourceAllocationResponse.from(saved);
    }
}
