package com.company.scopery.modules.project.templatetask.application.service;

import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatetask.application.response.ProjectTemplateTaskResponse;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersion;
import com.company.scopery.modules.project.templateversion.domain.model.ProjectTemplateVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectTemplateTaskQueryService {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateVersionRepository versionRepository;
    private final ProjectTemplateTaskRepository taskRepository;
    private final TemplateAccessSupport authorizationSupport;

    public ProjectTemplateTaskQueryService(ProjectTemplateRepository templateRepository,
                                           ProjectTemplateVersionRepository versionRepository,
                                           ProjectTemplateTaskRepository taskRepository,
                                           TemplateAccessSupport authorizationSupport) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.taskRepository = taskRepository;
        this.authorizationSupport = authorizationSupport;
    }

    @Transactional(readOnly = true)
    public List<ProjectTemplateTaskResponse> listTasks(UUID templateId, UUID versionId) {
        authorize(templateId, versionId);
        return taskRepository.findByTemplateVersionId(versionId).stream()
                .map(ProjectTemplateTaskResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectTemplateTaskResponse getTask(UUID templateId, UUID versionId, UUID taskId) {
        authorize(templateId, versionId);
        ProjectTemplateTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateTaskNotFound(taskId));
        if (!task.templateVersionId().equals(versionId)) {
            throw ProjectExceptions.projectTemplateTaskPathMismatch(taskId, versionId);
        }
        return ProjectTemplateTaskResponse.from(task);
    }

    private void authorize(UUID templateId, UUID versionId) {
        ProjectTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(templateId));
        authorizationSupport.requireView(template);
        ProjectTemplateVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> ProjectExceptions.projectTemplateVersionNotFound(versionId));
        if (!version.projectTemplateId().equals(templateId)) {
            throw ProjectExceptions.projectTemplateVersionNotFound(versionId);
        }
    }
}
