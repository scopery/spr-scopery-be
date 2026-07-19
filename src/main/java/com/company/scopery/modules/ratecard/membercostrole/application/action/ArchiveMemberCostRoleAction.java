package com.company.scopery.modules.ratecard.membercostrole.application.action;

import com.company.scopery.modules.ratecard.membercostrole.application.command.ArchiveMemberCostRoleCommand;
import com.company.scopery.modules.ratecard.membercostrole.application.response.MemberCostRoleResponse;
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
public class ArchiveMemberCostRoleAction {
    private final WorkspaceMemberCostRoleRepository assignmentRepository;
    private final RateCardAuthorizationService authorizationService;
    private final RateCardActivityLogger activityLogger;
    private final RateCardPlatformPublisher platformPublisher;

    public ArchiveMemberCostRoleAction(WorkspaceMemberCostRoleRepository assignmentRepository,
                                       RateCardAuthorizationService authorizationService,
                                       RateCardActivityLogger activityLogger,
                                       RateCardPlatformPublisher platformPublisher) {
        this.assignmentRepository = assignmentRepository; this.authorizationService = authorizationService;
        this.activityLogger = activityLogger; this.platformPublisher = platformPublisher;
    }

    @Transactional
    public MemberCostRoleResponse execute(ArchiveMemberCostRoleCommand cmd) {
        var assignment = assignmentRepository.findById(cmd.id())
                .orElseThrow(() -> RateCardExceptions.memberAssignmentNotFound(cmd.id()));
        authorizationService.requireMemberCostRoleManage(assignment.workspaceId());
        var saved = assignmentRepository.save(assignment.archive(authorizationService.currentUserId()));
        platformPublisher.enqueue(RateCardPlatformPublisher.AGGREGATE_MEMBER_COST_ROLE, saved.id(),
                "MEMBER_COST_ROLE_ARCHIVED", RateCardPlatformPublisher.mapOf("id", saved.id()));
        activityLogger.logSuccess(RateCardEntityTypes.MEMBER_COST_ROLE, saved.id(),
                RateCardActivityActions.MEMBER_COST_ROLE_ARCHIVED, "Member cost role archived");
        return MemberCostRoleResponse.from(saved);
    }
}
