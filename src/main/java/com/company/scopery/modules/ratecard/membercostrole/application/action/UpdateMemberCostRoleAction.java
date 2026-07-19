package com.company.scopery.modules.ratecard.membercostrole.application.action;

import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;
import com.company.scopery.modules.ratecard.costrole.domain.model.CostRoleRepository;
import com.company.scopery.modules.ratecard.membercostrole.application.command.UpdateMemberCostRoleCommand;
import com.company.scopery.modules.ratecard.membercostrole.application.response.MemberCostRoleResponse;
import com.company.scopery.modules.ratecard.membercostrole.domain.enums.MemberCostRoleStatus;
import com.company.scopery.modules.ratecard.membercostrole.domain.model.WorkspaceMemberCostRoleRepository;
import com.company.scopery.modules.ratecard.shared.activity.RateCardActivityLogger;
import com.company.scopery.modules.ratecard.shared.authorization.RateCardAuthorizationService;
import com.company.scopery.modules.ratecard.shared.constant.RateCardActivityActions;
import com.company.scopery.modules.ratecard.shared.constant.RateCardEntityTypes;
import com.company.scopery.modules.ratecard.shared.error.RateCardExceptions;
import com.company.scopery.modules.ratecard.shared.support.RateCardPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateMemberCostRoleAction {
    private final WorkspaceMemberCostRoleRepository assignmentRepository;
    private final CostRoleRepository costRoleRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public UpdateMemberCostRoleAction(WorkspaceMemberCostRoleRepository assignmentRepository,
                                      CostRoleRepository costRoleRepository,
                                      RateCardAuthorizationService authorizationService,
                                      RateCardActivityLogger activityLogger,
                                      RateCardPlatformPublisher platformPublisher) {
        this.assignmentRepository = assignmentRepository; this.costRoleRepository = costRoleRepository;
        this.authorizationService = authorizationService; this.activityLogger = activityLogger;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public MemberCostRoleResponse execute(UpdateMemberCostRoleCommand cmd) {
        var assignment = assignmentRepository.findById(cmd.id())
                .orElseThrow(() -> RateCardExceptions.memberAssignmentNotFound(cmd.id()));
        authorizationService.requireMemberCostRoleManage(assignment.workspaceId());
        if (assignment.status() == MemberCostRoleStatus.ARCHIVED) {
            throw RateCardExceptions.memberAssignmentNotFound(cmd.id());
        }
        if (cmd.effectiveFrom() == null || (cmd.effectiveTo() != null && cmd.effectiveTo().isBefore(cmd.effectiveFrom()))) {
            throw RateCardExceptions.memberDateRangeInvalid();
        }
        var role = costRoleRepository.findById(cmd.costRoleId())
                .orElseThrow(() -> RateCardExceptions.costRoleNotFound(cmd.costRoleId()));
        if (role.status() != CostRoleStatus.ACTIVE) throw RateCardExceptions.memberRoleInactive(role.id());
        boolean isDefault = cmd.isDefault() == null || cmd.isDefault();
        if (isDefault) {
            for (var existing : assignmentRepository.findActiveDefaultsByMember(assignment.workspaceMemberId())) {
                if (!existing.id().equals(assignment.id()) && existing.overlaps(cmd.effectiveFrom(), cmd.effectiveTo())) {
                    throw RateCardExceptions.memberOverlap(assignment.workspaceMemberId());
                }
            }
        }
        var saved = assignmentRepository.save(assignment.update(cmd.costRoleId(), isDefault, cmd.effectiveFrom(), cmd.effectiveTo()));
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_MEMBER_COST_ROLE, saved.id(),
                "MEMBER_COST_ROLE_UPDATED", RateCardPlatformPublisher.mapOf("id", saved.id()));
        activityLogger.logSuccess(RateCardEntityTypes.MEMBER_COST_ROLE, saved.id(),
                RateCardActivityActions.MEMBER_COST_ROLE_UPDATED, "Member cost role updated");
        return MemberCostRoleResponse.from(saved);
    }
}
