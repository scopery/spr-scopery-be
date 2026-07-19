package com.company.scopery.modules.clientportal.invite.domain.model;
import java.util.*;
public interface ExternalPortalInviteRepository {
    ExternalPortalInvite save(ExternalPortalInvite entity);
    Optional<ExternalPortalInvite> findByIdAndProjectId(UUID id, UUID projectId);
    Optional<ExternalPortalInvite> findByInviteTokenHash(String inviteTokenHash);
    List<ExternalPortalInvite> findByProjectId(UUID projectId);
}
