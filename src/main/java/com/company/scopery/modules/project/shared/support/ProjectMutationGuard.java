package com.company.scopery.modules.project.shared.support;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Central guard: archived (and otherwise non-mutable) projects must not allow
 * phase / WBS / task / dependency mutations.
 * After a current baseline exists (strict mode), controlled edits require a ChangeRequest
 * unless {@link BaselineApplyContext} is active.
 */
@Component
public class ProjectMutationGuard {

    private final ProjectRepository projectRepository;

    public ProjectMutationGuard(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project requireMutableProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        assertMutable(project);
        return project;
    }

    public void assertMutable(Project project) {
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectExceptions.projectAlreadyArchived(project.id());
        }
        if (project.status() != ProjectStatus.DRAFT && project.status() != ProjectStatus.ACTIVE) {
            throw ProjectExceptions.projectNotActiveOrDraft(project.id());
        }
        assertBaselineAllowsMutation(project);
    }

    public void assertBaselineAllowsMutation(Project project) {
        if (project.currentBaselineId() != null && !BaselineApplyContext.isActive()) {
            throw ProjectExceptions.postBaselineEditBlocked(project.id());
        }
    }
}
