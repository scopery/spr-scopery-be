package com.company.scopery.modules.project.templatewbs.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhase;
import com.company.scopery.modules.project.templatephase.domain.model.ProjectTemplatePhaseRepository;
import com.company.scopery.modules.project.templatewbs.application.command.UpdateProjectTemplateWbsNodeCommand;
import com.company.scopery.modules.project.templatewbs.application.response.ProjectTemplateWbsNodeResponse;
import com.company.scopery.modules.project.templatewbs.domain.enums.TemplateWbsNodeType;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateProjectTemplateWbsNodeAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateWbsNodeRepository wbsRepository;
    private final ProjectTemplatePhaseRepository phaseRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public UpdateProjectTemplateWbsNodeAction(ProjectTemplateRepository templateRepository,
                                              ProjectTemplateWbsNodeRepository wbsRepository,
                                              ProjectTemplatePhaseRepository phaseRepository,
                                              TemplateVersionMutationGuard mutationGuard,
                                              TemplateAccessSupport authorizationSupport,
                                              ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.wbsRepository = wbsRepository;
        this.phaseRepository = phaseRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectTemplateWbsNodeResponse execute(UpdateProjectTemplateWbsNodeCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        ProjectTemplateWbsNode node = wbsRepository.findById(cmd.templateWbsNodeId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateWbsNodeNotFound(cmd.templateWbsNodeId()));
        if (!node.templateVersionId().equals(cmd.versionId())) {
            throw ProjectExceptions.projectTemplateWbsNodePathMismatch(node.id(), cmd.versionId());
        }

        if (cmd.templatePhaseId() != null) {
            ProjectTemplatePhase phase = phaseRepository.findById(cmd.templatePhaseId())
                    .orElseThrow(() -> ProjectExceptions.projectTemplatePhaseNotFound(cmd.templatePhaseId()));
            if (!phase.templateVersionId().equals(cmd.versionId())) {
                throw ProjectExceptions.projectTemplatePhasePathMismatch(phase.id(), cmd.versionId());
            }
        }

        TemplateWbsNodeType nodeType = ProjectEnumParser.parseOptional(
                TemplateWbsNodeType.class, cmd.nodeType(), "PROJECT_TEMPLATE_WBS_INVALID_TYPE", "nodeType");

        ProjectTemplateWbsNode saved = wbsRepository.save(node.update(
                cmd.code(),
                cmd.title(),
                cmd.description(),
                nodeType != null ? nodeType : node.nodeType(),
                cmd.templatePhaseId(),
                cmd.deliverableDocumentTypeId()
        ));

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_WBS_NODE,
                saved.id(),
                ProjectActivityActions.UPDATE_PROJECT_TEMPLATE_WBS_NODE,
                "Template WBS node updated: " + saved.title()
        );

        return ProjectTemplateWbsNodeResponse.from(saved);
    }
}
