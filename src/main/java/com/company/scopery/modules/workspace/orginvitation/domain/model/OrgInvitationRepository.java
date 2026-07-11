package com.company.scopery.modules.workspace.orginvitation.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface OrgInvitationRepository {

    OrgInvitation save(OrgInvitation invitation);

    Optional<OrgInvitation> findById(UUID id);

    Optional<OrgInvitation> findByToken(String token);
}
