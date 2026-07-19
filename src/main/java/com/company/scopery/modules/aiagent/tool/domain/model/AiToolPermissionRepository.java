package com.company.scopery.modules.aiagent.tool.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiToolPermissionRepository {

    AiToolPermission save(AiToolPermission permission);

    Optional<AiToolPermission> findById(UUID id);

    boolean existsByToolIdAndPermissionCode(UUID toolId, String permissionCode);

    List<AiToolPermission> findByToolId(UUID toolId);

    void deleteById(UUID id);
}
