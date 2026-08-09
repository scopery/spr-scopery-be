package com.company.scopery.modules.elicitation.session.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ElicitationSessionRepository {

    ElicitationSession save(ElicitationSession session);

    Optional<ElicitationSession> findById(UUID id);

    List<ElicitationSession> findAllByProjectId(UUID projectId);

    boolean existsActiveByProjectIdAndScopePackageId(UUID projectId, UUID scopePackageId);

    Optional<ElicitationSession> findActiveByProjectIdAndScopePackageId(UUID projectId, UUID scopePackageId);
}
