package com.company.scopery.modules.resourcecapacity.projectallocation.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.UpdateProjectResourceAllocationCommand;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.response.ProjectResourceAllocationResponse;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.AllocationType;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityActivityActions;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityEntityTypes;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityExceptions;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.resourcecapacity.shared.util.CapacityEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class UpdateProjectResourceAllocationAction {

    private final ProjectResourceAllocationRepository allocationRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public UpdateProjectResourceAllocationAction(ProjectResourceAllocationRepository allocationRepository,
                                                 CapacityActivityLogger activityLogger,
                                                 CapacityWorkspaceAuthorizationService authorizationService,
                                                 CapacityPlatformPublisher platformPublisher) {
        this.allocationRepository = allocationRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectResourceAllocationResponse execute(UpdateProjectResourceAllocationCommand cmd) {
        ProjectResourceAllocation allocation = allocationRepository.findById(cmd.id())
                .orElseThrow(() -> CapacityExceptions.allocationNotFound(cmd.id()));
        authorizationService.requireAllocationUpdate(allocation.workspaceId());

        AllocationType allocationType = CapacityEnumParser.parseRequired(
                AllocationType.class, cmd.allocationType(),
                "PROJECT_ALLOCATION_INVALID_TYPE", "allocationType");

        validatePercent(cmd.allocationPercent());
        validateDateRange(cmd.startDate(), cmd.endDate());
        checkOverAllocation(allocation, cmd.startDate(), cmd.endDate(), cmd.allocationPercent());

        ProjectResourceAllocation updated = allocation.update(
                cmd.allocationPercent(), allocationType, cmd.startDate(), cmd.endDate(), cmd.notes());
        ProjectResourceAllocation saved = allocationRepository.save(updated);

        platformPublisher.enqueueAllocation(saved, "PROJECT_ALLOCATION_UPDATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.PROJECT_RESOURCE_ALLOCATION,
                saved.id(),
                CapacityActivityActions.PROJECT_ALLOCATION_UPDATED,
                "Project resource allocation updated: " + saved.id());
        platformPublisher.audit(AuditEventType.PROJECT_ALLOCATION_CHANGED, null,
                CapacityEntityTypes.PROJECT_RESOURCE_ALLOCATION, saved.id(),
                null, saved.workspaceId(), Map.of("allocationPercent", saved.allocationPercent()),
                "Project resource allocation updated: " + saved.id());

        return ProjectResourceAllocationResponse.from(saved);
    }

    private void validatePercent(BigDecimal percent) {
        if (percent == null || percent.compareTo(BigDecimal.ZERO) <= 0 || percent.compareTo(new BigDecimal("100")) > 0) {
            throw CapacityExceptions.allocationInvalidPercent(percent);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || (endDate != null && endDate.isBefore(startDate))) {
            throw CapacityExceptions.allocationDateRangeInvalid();
        }
    }

    private void checkOverAllocation(ProjectResourceAllocation current, LocalDate startDate, LocalDate endDate,
                                     BigDecimal newPercent) {
        List<ProjectResourceAllocation> overlapping =
                allocationRepository.findActiveByUserIdAndDateRange(current.userId(), startDate, endDate);
        BigDecimal total = overlapping.stream()
                .filter(existing -> !existing.id().equals(current.id()))
                .map(ProjectResourceAllocation::allocationPercent)
                .reduce(newPercent, BigDecimal::add);
        if (total.compareTo(new BigDecimal("100")) > 0) {
            UUID userId = current.userId();
            platformPublisher.audit(AuditEventType.RESOURCE_OVER_ALLOCATION_BLOCKED, null,
                    CapacityEntityTypes.PROJECT_RESOURCE_ALLOCATION, current.id(),
                    null, current.workspaceId(), Map.of("userId", userId, "totalAllocationPercent", total),
                    "Over-allocation blocked for user: " + userId);
            throw CapacityExceptions.allocationOverAllocated(userId, total);
        }
    }
}
