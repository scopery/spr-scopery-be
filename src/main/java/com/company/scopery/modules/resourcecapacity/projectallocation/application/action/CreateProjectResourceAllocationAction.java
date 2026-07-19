package com.company.scopery.modules.resourcecapacity.projectallocation.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.CreateProjectResourceAllocationCommand;
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
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class CreateProjectResourceAllocationAction {

    private final ProjectResourceAllocationRepository allocationRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final CapacityActivityLogger activityLogger;
    private final CapacityWorkspaceAuthorizationService authorizationService;
    private final CapacityPlatformPublisher platformPublisher;

    public CreateProjectResourceAllocationAction(ProjectResourceAllocationRepository allocationRepository,
                                                 ProjectRepository projectRepository,
                                                 WorkspaceMemberRepository workspaceMemberRepository,
                                                 CapacityActivityLogger activityLogger,
                                                 CapacityWorkspaceAuthorizationService authorizationService,
                                                 CapacityPlatformPublisher platformPublisher) {
        this.allocationRepository = allocationRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public ProjectResourceAllocationResponse execute(CreateProjectResourceAllocationCommand cmd) {
        authorizationService.requireAllocationCreate(cmd.workspaceId());

        Project project = projectRepository.findById(cmd.projectId())
                .orElseThrow(() -> CapacityExceptions.allocationProjectNotFound(cmd.projectId()));
        if (!project.workspaceId().equals(cmd.workspaceId())) {
            throw CapacityExceptions.allocationProjectWorkspaceMismatch(cmd.projectId(), cmd.workspaceId());
        }

        WorkspaceMember member = workspaceMemberRepository.findById(cmd.workspaceMemberId())
                .orElseThrow(() -> CapacityExceptions.allocationMemberNotFound(cmd.workspaceMemberId()));
        if (!member.workspaceId().equals(cmd.workspaceId())) {
            throw CapacityExceptions.allocationMemberNotFound(cmd.workspaceMemberId());
        }
        if (member.status() != WorkspaceMemberStatus.ACTIVE) {
            throw CapacityExceptions.allocationMemberInactive(cmd.workspaceMemberId());
        }

        AllocationType allocationType = CapacityEnumParser.parseRequired(
                AllocationType.class, cmd.allocationType(),
                "PROJECT_ALLOCATION_INVALID_TYPE", "allocationType");

        validatePercent(cmd.allocationPercent());
        validateDateRange(cmd.startDate(), cmd.endDate());
        checkOverAllocation(member.userId(), cmd.startDate(), cmd.endDate(), cmd.allocationPercent(), cmd.workspaceId());

        ProjectResourceAllocation allocation = ProjectResourceAllocation.create(
                cmd.workspaceId(), cmd.projectId(), cmd.workspaceMemberId(), member.userId(),
                cmd.allocationPercent(), allocationType, cmd.startDate(), cmd.endDate(), cmd.notes());
        ProjectResourceAllocation saved = allocationRepository.save(allocation);

        platformPublisher.enqueueAllocation(saved, "PROJECT_ALLOCATION_CREATED");
        activityLogger.logSuccess(
                CapacityEntityTypes.PROJECT_RESOURCE_ALLOCATION,
                saved.id(),
                CapacityActivityActions.PROJECT_ALLOCATION_CREATED,
                "Project resource allocation created for member: " + saved.workspaceMemberId());
        platformPublisher.audit(AuditEventType.PROJECT_ALLOCATION_CHANGED, null,
                CapacityEntityTypes.PROJECT_RESOURCE_ALLOCATION, saved.id(),
                null, saved.workspaceId(), Map.of("allocationPercent", saved.allocationPercent()),
                "Project resource allocation created: " + saved.id());

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

    private void checkOverAllocation(java.util.UUID userId, LocalDate startDate, LocalDate endDate,
                                     BigDecimal newPercent, java.util.UUID workspaceId) {
        List<ProjectResourceAllocation> overlapping =
                allocationRepository.findActiveByUserIdAndDateRange(userId, startDate, endDate);
        BigDecimal total = overlapping.stream()
                .map(ProjectResourceAllocation::allocationPercent)
                .reduce(newPercent, BigDecimal::add);
        if (total.compareTo(new BigDecimal("100")) > 0) {
            platformPublisher.audit(AuditEventType.RESOURCE_OVER_ALLOCATION_BLOCKED, null,
                    CapacityEntityTypes.PROJECT_RESOURCE_ALLOCATION, null,
                    null, workspaceId, Map.of("userId", userId, "totalAllocationPercent", total),
                    "Over-allocation blocked for user: " + userId);
            throw CapacityExceptions.allocationOverAllocated(userId, total);
        }
    }
}
