package com.company.scopery.modules.ratecard.membercostrole.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.membercostrole.application.command.AssignMemberCostRoleCommand;
import com.company.scopery.modules.ratecard.membercostrole.application.response.MemberCostRoleResponse;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleAssignment;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardActivityActions;
import com.company.scopery.modules.ratecard.shared.constant.RateCardEntityTypes;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AssignMemberCostRoleAction {
    private final WorkspaceMemberCostRoleRepository assignmentRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final CostRoleRepository costRoleRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public AssignMemberCostRoleAction(WorkspaceMemberCostRoleRepository assignmentRepository,
                                      WorkspaceMemberRepository workspaceMemberRepository,
                                      CostRoleRepository costRoleRepository,
                                      RateCardAuthorizationService authorizationService,
                                      RateCardActivityLogger activityLogger,
                                      RateCardPlatformPublisher platformPublisher) {
        this.assignmentRepository = assignmentRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.costRoleRepository = costRoleRepository;
        this.authorizationService = authorizationService;
        this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public MemberCostRoleResponse execute(AssignMemberCostRoleCommand cmd) {
        authorizationService.requireMemberCostRoleAssign(cmd.workspaceId());
        if (cmd.effectiveFrom() == null || (cmd.effectiveTo() != null && cmd.effectiveTo().isBefore(cmd.effectiveFrom()))) {
            throw RateCardExceptions.memberDateRangeInvalid();
        }
        var member = workspaceMemberRepository.findById(cmd.workspaceMemberId())
                .orElseThrow(() -> RateCardExceptions.memberNotFound(cmd.workspaceMemberId()));
        if (!member.workspaceId().equals(cmd.workspaceId())) {
            throw RateCardExceptions.memberNotFound(cmd.workspaceMemberId());
        }
        if (member.status() != WorkspaceMemberStatus.ACTIVE) {
            throw RateCardExceptions.memberInactive(cmd.workspaceMemberId());
        }
        var role = costRoleRepository.findById(cmd.costRoleId())
                .orElseThrow(() -> RateCardExceptions.costRoleNotFound(cmd.costRoleId()));
        if (role.status() != CostRoleStatus.ACTIVE) {
            throw RateCardExceptions.memberRoleInactive(role.id());
        }

        boolean isDefault = cmd.isDefault() == null || cmd.isDefault();
        if (isDefault) {
            for (var existing : assignmentRepository.findActiveDefaultsByMember(cmd.workspaceMemberId())) {
                if (existing.overlaps(cmd.effectiveFrom(), cmd.effectiveTo())) {
                    throw RateCardExceptions.memberOverlap(cmd.workspaceMemberId());
                }
            }
        }

        var saved = assignmentRepository.save(WorkspaceMemberCostRoleAssignment.create(
                cmd.workspaceId(), cmd.workspaceMemberId(), member.userId(), cmd.costRoleId(),
                isDefault, cmd.effectiveFrom(), cmd.effectiveTo()));
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_MEMBER_COST_ROLE, saved.id(),
                "MEMBER_COST_ROLE_ASSIGNED", RateCardPlatformPublisher.mapOf(
                        "id", saved.id(), "workspaceMemberId", saved.workspaceMemberId(), "costRoleId", saved.costRoleId()));
        platformPublisher.audit(AuditEventType.MEMBER_COST_ROLE_CHANGED, authorizationService.currentUserId(),
                RateCardPlatformPublisher.AGGREGATE_MEMBER_COST_ROLE, saved.id(),
                null, saved.workspaceId(), RateCardPlatformPublisher.mapOf("costRoleId", saved.costRoleId()),
                "Member cost role assigned");
        activityLogger.logSuccess(RateCardEntityTypes.MEMBER_COST_ROLE, saved.id(),
                RateCardActivityActions.MEMBER_COST_ROLE_ASSIGNED, "Member cost role assigned");
        return MemberCostRoleResponse.from(saved);
    }
}
