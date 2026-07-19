package com.company.scopery.modules.clientportal.comment.domain.model;
import java.util.*;
public interface ClientCommentRepository {
    ClientComment save(ClientComment entity);
    List<ClientComment> findByProjectId(UUID projectId);
    List<ClientComment> findByProjectIdAndTargetTypeAndTargetId(UUID projectId, String targetType, UUID targetId);
}
