package com.company.scopery.modules.resourcecapacity.projectallocation.application.action;

import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.ArchiveProjectResourceAllocationCommand;
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
public class ArchiveProjectResourceAllocationAction {

    private final ProjectResourceAllocationRepository allocationRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public ArchiveProjectResourceAllocationAction(ProjectResourceAllocationRepository allocationRepository,
                                                  CapacityActivityLogger activityLogger,
                                                  CapacityWorkspaceAuthorizationService authorizationService,
                                                  CapacityPlatformPublisher platformPublisher) {
        this.allocationRepository = allocationRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectResourceAllocationResponse execute(ArchiveProjectResourceAllocationCommand cmd) {
        ProjectResourceAllocation allocation = allocationRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.allocationNotFound(cmd.id()));
        authorizationService.requireAllocationArchive(allocation.workspaceId());

        ProjectResourceAllocation saved = allocationRepository.save(allocation.archive(null));

        platformPublisher.enqueueAllocation(saved, "PROJECT_ALLOCATION_ARCHIVED");
        activityLogger.logSuccess(
                CapacityEntityTypes.PROJECT_RESOURCE_ALLOCATION,
                saved.id(),
                CapacityActivityActions.PROJECT_ALLOCATION_ARCHIVED,
                "Project resource allocation archived: " + saved.id());

        return ProjectResourceAllocationResponse.from(saved);
    }
}
