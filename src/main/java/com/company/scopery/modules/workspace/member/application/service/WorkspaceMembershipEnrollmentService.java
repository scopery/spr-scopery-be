package com.company.scopery.modules.workspace.member.application.service;

import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Create or reactivate workspace membership after kick/deactivate.
 */
@Service
public class WorkspaceMembershipEnrollmentService {

    private final WorkspaceMemberRepository memberRepository;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public WorkspaceMembershipEnrollmentService(WorkspaceMemberRepository memberRepository,
                                                 WorkspaceIamIntegrationService iamIntegrationService) {
        this.memberRepository = memberRepository;
        this.iamIntegrationService = iamIntegrationService;
    }

    public WorkspaceMember ensureActiveMembership(UUID workspaceId, UUID userId, boolean failIfActive) {
        Optional<WorkspaceMember> existing = memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
        WorkspaceMember saved;
        if (existing.isEmpty()) {
            saved = memberRepository.save(WorkspaceMember.create(workspaceId, userId));
        } else {
            WorkspaceMember member = existing.get();
            if (member.status() == WorkspaceMemberStatus.ACTIVE) {
                if (failIfActive) {
                    throw WorkspaceExceptions.workspaceMemberAlreadyExists(workspaceId, userId);
                }
                return member;
            }
            saved = memberRepository.save(member.activate());
        }
        iamIntegrationService.ensureWorkspaceMemberBaselineAccess(saved.workspaceId(), saved.userId());
        return saved;
    }
}
