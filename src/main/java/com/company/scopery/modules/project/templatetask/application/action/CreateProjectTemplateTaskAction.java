package com.company.scopery.modules.project.templatetask.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.task.domain.enums.TaskPriority;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import com.company.scopery.modules.project.templatetask.application.command.CreateProjectTemplateTaskCommand;
import com.company.scopery.modules.project.templatetask.application.response.ProjectTemplateTaskResponse;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTask;
import com.company.scopery.modules.project.templatetask.domain.model.ProjectTemplateTaskRepository;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CreateProjectTemplateTaskAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateTaskRepository taskRepository;
    private final ProjectTemplatePhaseRepository phaseRepository;
    private final ProjectTemplateWbsNodeRepository wbsRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public CreateProjectTemplateTaskAction(ProjectTemplateRepository templateRepository,
                                           ProjectTemplateTaskRepository taskRepository,
                                           ProjectTemplatePhaseRepository phaseRepository,
                                           ProjectTemplateWbsNodeRepository wbsRepository,
                                           TemplateVersionMutationGuard mutationGuard,
                                           TemplateAccessSupport authorizationSupport,
                                           ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.taskRepository = taskRepository;
        this.phaseRepository = phaseRepository;
        this.wbsRepository = wbsRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectTemplateTaskResponse execute(CreateProjectTemplateTaskCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        ProjectTemplatePhase phase = phaseRepository.findById(cmd.templatePhaseId())
                .orElseThrow(() -> ProjectExceptions.projectTemplatePhaseNotFound(cmd.templatePhaseId()));
        if (!phase.templateVersionId().equals(cmd.versionId())) {
            throw ProjectExceptions.projectTemplateTaskPhaseMismatch(phase.id(), cmd.versionId());
        }

        if (cmd.templateWbsNodeId() != null) {
            ProjectTemplateWbsNode wbs = wbsRepository.findById(cmd.templateWbsNodeId())
                    .orElseThrow(() -> ProjectExceptions.projectTemplateWbsNodeNotFound(cmd.templateWbsNodeId()));
            if (!wbs.templateVersionId().equals(cmd.versionId())) {
                throw ProjectExceptions.projectTemplateTaskWbsMismatch(wbs.id(), cmd.versionId());
            }
        }

        if (cmd.estimateHours() != null && cmd.estimateHours().compareTo(BigDecimal.ZERO) <= 0) {
            throw ProjectExceptions.projectTemplateTaskInvalidEstimate();
        }

        TaskPriority priority = ProjectEnumParser.parseOptional(
                TaskPriority.class, cmd.defaultPriority(), "PROJECT_TEMPLATE_TASK_INVALID_PRIORITY", "defaultPriority");

        ProjectTemplateTask task = ProjectTemplateTask.create(
                cmd.versionId(),
                cmd.templatePhaseId(),
                cmd.templateWbsNodeId(),
                cmd.code(),
                cmd.title(),
                cmd.description(),
                priority,
                cmd.estimateHours(),
                cmd.dueOffsetDays(),
                cmd.startOffsetDays(),
                cmd.defaultAssigneeRoleCode(),
                cmd.deliverableDocumentTypeId()
        );
        ProjectTemplateTask saved = taskRepository.save(task);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_TASK,
                saved.id(),
                ProjectActivityActions.PROJECT_TEMPLATE_TASK_CREATED,
                "Template task created: " + saved.title()
        );

        return ProjectTemplateTaskResponse.from(saved);
    }
}
