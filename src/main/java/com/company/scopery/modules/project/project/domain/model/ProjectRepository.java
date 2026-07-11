package com.company.scopery.modules.project.project.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    boolean existsByWorkspaceIdAndCode(UUID workspaceId, String code);
    PageResult<Project> search(UUID workspaceId, String keyword, ProjectStatus status, PageQuery pageQuery);
}
