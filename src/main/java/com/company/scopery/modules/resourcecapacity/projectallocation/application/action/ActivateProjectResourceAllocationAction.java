package com.company.scopery.modules.resourcecapacity.projectallocation.application.action;

import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.ActivateProjectResourceAllocationCommand;
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

import java.math.BigDecimal;
import java.util.List;

@Component
public class ActivateProjectResourceAllocationAction {

    private final ProjectResourceAllocationRepository allocationRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public ActivateProjectResourceAllocationAction(ProjectResourceAllocationRepository allocationRepository,
                                                   CapacityActivityLogger activityLogger,
                                                   CapacityWorkspaceAuthorizationService authorizationService,
                                                   CapacityPlatformPublisher platformPublisher) {
        this.allocationRepository = allocationRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectResourceAllocationResponse execute(ActivateProjectResourceAllocationCommand cmd) {
        ProjectResourceAllocation allocation = allocationRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.allocationNotFound(cmd.id()));
        authorizationService.requireAllocationUpdate(allocation.workspaceId());

        List<ProjectResourceAllocation> overlapping = allocationRepository
                .findActiveByUserIdAndDateRange(allocation.userId(), allocation.startDate(), allocation.endDate());
        BigDecimal total = overlapping.stream()
                .filter(existing -> !existing.id().equals(allocation.id()))
                .map(ProjectResourceAllocation::allocationPercent)
                .reduce(allocation.allocationPercent(), BigDecimal::add);
        if (total.compareTo(new BigDecimal("100")) > 0) {
            throw CapacityExceptions.allocationOverAllocated(allocation.userId(), total);
        }

        ProjectResourceAllocation saved = allocationRepository.save(allocation.activate());

        platformPublisher.enqueueAllocation(saved, "PROJECT_ALLOCATION_ACTIVATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.PROJECT_RESOURCE_ALLOCATION,
                saved.id(),
                CapacityActivityActions.PROJECT_ALLOCATION_ACTIVATED,
                "Project resource allocation activated: " + saved.id());

        return ProjectResourceAllocationResponse.from(saved);
    }
}
