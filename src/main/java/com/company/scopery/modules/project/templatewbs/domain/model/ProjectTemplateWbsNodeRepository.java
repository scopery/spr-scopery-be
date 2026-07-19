package com.company.scopery.modules.project.templatewbs.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectTemplateWbsNodeRepository {

    ProjectTemplateWbsNode save(ProjectTemplateWbsNode node);

    Optional<ProjectTemplateWbsNode> findById(UUID id);

    void deleteById(UUID id);

    List<ProjectTemplateWbsNode> findByTemplateVersionId(UUID templateVersionId);

    List<ProjectTemplateWbsNode> findChildrenByParentId(UUID parentId);

    boolean existsByTemplatePhaseId(UUID templatePhaseId);
}
