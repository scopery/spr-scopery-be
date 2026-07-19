package com.company.scopery.modules.project.templatetask.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectTemplateTaskRepository {

    ProjectTemplateTask save(ProjectTemplateTask task);

    Optional<ProjectTemplateTask> findById(UUID id);

    void deleteById(UUID id);

    List<ProjectTemplateTask> findByTemplateVersionId(UUID templateVersionId);

    boolean existsByTemplatePhaseId(UUID templatePhaseId);

    boolean existsByTemplateWbsNodeId(UUID templateWbsNodeId);
}
