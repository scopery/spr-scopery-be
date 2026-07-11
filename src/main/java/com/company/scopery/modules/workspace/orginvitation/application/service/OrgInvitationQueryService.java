package com.company.scopery.modules.workspace.orginvitation.application.service;

import com.company.scopery.modules.workspace.orginvitation.application.response.OrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitationRepository;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrgInvitationQueryService {

    private final OrgInvitationRepository invitationRepository;

    public OrgInvitationQueryService(OrgInvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @Transactional(readOnly = true)
    public OrgInvitationResponse getInvitation(UUID id) {
        return invitationRepository.findById(id)
                .map(OrgInvitationResponse::from)
                .orElseThrow(() -> WorkspaceExceptions.orgInvitationNotFound(id));
    }
}
