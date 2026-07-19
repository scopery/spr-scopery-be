package com.company.scopery.modules.externalparty.authority.domain.model;
import java.util.*;
public interface ProjectApprovalAuthorityRepository {
    ProjectApprovalAuthority save(ProjectApprovalAuthority entity);
    Optional<ProjectApprovalAuthority> findByIdAndProjectId(UUID id, UUID projectId);
    List<ProjectApprovalAuthority> findByProjectId(UUID projectId);
}
