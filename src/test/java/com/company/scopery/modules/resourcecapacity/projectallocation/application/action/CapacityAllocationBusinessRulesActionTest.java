package com.company.scopery.modules.resourcecapacity.projectallocation.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.resourcecapacity.projectallocation.application.command.CreateProjectResourceAllocationCommand;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.enums.AllocationType;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocation;
import com.company.scopery.modules.resourcecapacity.projectallocation.domain.model.ProjectResourceAllocationRepository;
import com.company.scopery.modules.resourcecapacity.shared.activity.CapacityActivityLogger;
import com.company.scopery.modules.resourcecapacity.shared.authorization.CapacityWorkspaceAuthorizationService;
import com.company.scopery.modules.resourcecapacity.shared.error.CapacityErrorCatalog;
import com.company.scopery.modules.resourcecapacity.shared.support.CapacityPlatformPublisher;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapacityAllocationBusinessRulesActionTest {

    @Mock private ProjectResourceAllocationRepository allocationRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private CapacityActivityLogger activityLogger;
    @Mock private CapacityWorkspaceAuthorizationService authorizationService;
    @Mock private CapacityPlatformPublisher platformPublisher;

    private CreateProjectResourceAllocationAction createProjectResourceAllocationAction;

    private final UUID workspaceId = UUID.randomUUID();
    private final UUID otherWorkspaceId = UUID.randomUUID();
    private final UUID organizationId = UUID.randomUUID();
    private final UUID projectId = UUID.randomUUID();
    private final UUID memberId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final LocalDate startDate = LocalDate.of(2026, 1, 1);
    private final LocalDate endDate = LocalDate.of(2026, 12, 31);

    @BeforeEach
    void setUp() {
        createProjectResourceAllocationAction = new CreateProjectResourceAllocationAction(
                allocationRepository, projectRepository, workspaceMemberRepository,
                activityLogger, authorizationService, platformPublisher);
    }

    @Test
    void createAllocation_valid_success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project(workspaceId)));
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.ACTIVE)));
        when(allocationRepository.findActiveByUserIdAndDateRange(userId, startDate, endDate)).thenReturn(List.of());
        when(allocationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = createProjectResourceAllocationAction.execute(new CreateProjectResourceAllocationCommand(
                workspaceId, projectId, memberId, new BigDecimal("50"), "PLANNED", startDate, endDate, null));

        assertThat(response.allocationPercent()).isEqualByComparingTo(new BigDecimal("50"));
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    void createAllocation_projectDifferentWorkspace_rejected() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project(otherWorkspaceId)));

        assertThatThrownBy(() -> createProjectResourceAllocationAction.execute(new CreateProjectResourceAllocationCommand(
                workspaceId, projectId, memberId, new BigDecimal("50"), "PLANNED", startDate, endDate, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.PROJECT_ALLOCATION_PROJECT_WORKSPACE_MISMATCH.code()));
    }

    @Test
    void createAllocation_inactiveMember_rejected() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project(workspaceId)));
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.INACTIVE)));

        assertThatThrownBy(() -> createProjectResourceAllocationAction.execute(new CreateProjectResourceAllocationCommand(
                workspaceId, projectId, memberId, new BigDecimal("50"), "PLANNED", startDate, endDate, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.PROJECT_ALLOCATION_MEMBER_INACTIVE.code()));
    }

    @Test
    void createAllocation_invalidPercentZero_rejected() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project(workspaceId)));
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.ACTIVE)));

        assertThatThrownBy(() -> createProjectResourceAllocationAction.execute(new CreateProjectResourceAllocationCommand(
                workspaceId, projectId, memberId, BigDecimal.ZERO, "PLANNED", startDate, endDate, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.PROJECT_ALLOCATION_INVALID_PERCENT.code()));
    }

    @Test
    void createAllocation_invalidPercentOver100_rejected() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project(workspaceId)));
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.ACTIVE)));

        assertThatThrownBy(() -> createProjectResourceAllocationAction.execute(new CreateProjectResourceAllocationCommand(
                workspaceId, projectId, memberId, new BigDecimal("150"), "PLANNED", startDate, endDate, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(CapacityErrorCatalog.PROJECT_ALLOCATION_INVALID_PERCENT.code()));
    }

    @Test
    void createAllocation_overAllocation_rejected() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project(workspaceId)));
        when(workspaceMemberRepository.findById(memberId)).thenReturn(Optional.of(member(WorkspaceMemberStatus.ACTIVE)));

        ProjectResourceAllocation existing = ProjectResourceAllocation.create(
                workspaceId, UUID.randomUUID(), memberId, userId, new BigDecimal("60"),
                AllocationType.CONFIRMED, startDate, endDate, null);
        when(allocationRepository.findActiveByUserIdAndDateRange(userId, startDate, endDate)).thenReturn(List.of(existing));

        assertThatThrownBy(() -> createProjectResourceAllocationAction.execute(new CreateProjectResourceAllocationCommand(
                workspaceId, projectId, memberId, new BigDecimal("50"), "PLANNED", startDate, endDate, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(CapacityErrorCatalog.PROJECT_ALLOCATION_OVER_ALLOCATED.code());
                });
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Project project(UUID ownerWorkspaceId) {
        Instant now = Instant.now();
        return new Project(projectId, ownerWorkspaceId, organizationId, "PRJ_01", "Project", null,
                UUID.randomUUID(), "USD", null, null, ProjectStatus.ACTIVE, null, null, null, null,
                null, null, null, null, null, null, null, null, null, 0, now, now);
    }

    private WorkspaceMember member(WorkspaceMemberStatus status) {
        Instant now = Instant.now();
        return new WorkspaceMember(memberId, workspaceId, userId, status, now, now, now);
    }
}
