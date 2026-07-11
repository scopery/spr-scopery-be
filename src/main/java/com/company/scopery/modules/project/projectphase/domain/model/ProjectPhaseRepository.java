package com.company.scopery.modules.project.projectphase.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectPhaseRepository {

    ProjectPhase save(ProjectPhase phase);

    Optional<ProjectPhase> findById(UUID id);

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    boolean existsByProjectIdAndDisplayOrder(UUID projectId, int displayOrder);

    boolean hasActiveWbsNodesOrTasks(UUID phaseId);

    PageResult<ProjectPhase> search(UUID projectId, ProjectPhaseStatus status, PageQuery pageQuery);

    List<ProjectPhase> findAllByProjectId(UUID projectId);
}
