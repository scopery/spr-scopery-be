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
import com.company.scopery.modules.project.templatewbs.application.command.CreateProjectTemplateWbsNodeCommand;
import com.company.scopery.modules.project.templatewbs.application.response.ProjectTemplateWbsNodeResponse;
import com.company.scopery.modules.project.templatewbs.domain.enums.TemplateWbsNodeType;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateProjectTemplateWbsNodeAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateWbsNodeRepository wbsRepository;
    private final ProjectTemplatePhaseRepository phaseRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public CreateProjectTemplateWbsNodeAction(ProjectTemplateRepository templateRepository,
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
    public ProjectTemplateWbsNodeResponse execute(CreateProjectTemplateWbsNodeCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        if (cmd.templatePhaseId() != null) {
            ProjectTemplatePhase phase = phaseRepository.findById(cmd.templatePhaseId())
                    .orElseThrow(() -> ProjectExceptions.projectTemplatePhaseNotFound(cmd.templatePhaseId()));
            if (!phase.templateVersionId().equals(cmd.versionId())) {
                throw ProjectExceptions.projectTemplatePhasePathMismatch(phase.id(), cmd.versionId());
            }
        }

        int depth = 0;
        if (cmd.parentId() != null) {
            ProjectTemplateWbsNode parent = wbsRepository.findById(cmd.parentId())
                    .orElseThrow(() -> ProjectExceptions.projectTemplateWbsNodeNotFound(cmd.parentId()));
            if (!parent.templateVersionId().equals(cmd.versionId())) {
                throw ProjectExceptions.projectTemplateWbsNodeParentInvalid(cmd.parentId());
            }
            depth = parent.depth() + 1;
        }

        TemplateWbsNodeType nodeType = ProjectEnumParser.parseOptional(
                TemplateWbsNodeType.class, cmd.nodeType(), "PROJECT_TEMPLATE_WBS_INVALID_TYPE", "nodeType");
        if (nodeType == null) {
            nodeType = TemplateWbsNodeType.WORK_PACKAGE;
        }

        ProjectTemplateWbsNode node = ProjectTemplateWbsNode.create(
                cmd.versionId(),
                cmd.parentId(),
                cmd.templatePhaseId(),
                cmd.code(),
                cmd.title(),
                cmd.description(),
                nodeType,
                depth,
                cmd.orderIndex(),
                cmd.deliverableDocumentTypeId()
        );
        ProjectTemplateWbsNode saved = wbsRepository.save(node);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_WBS_NODE,
                saved.id(),
                ProjectActivityActions.PROJECT_TEMPLATE_WBS_NODE_CREATED,
                "Template WBS node created: " + saved.title()
        );

        return ProjectTemplateWbsNodeResponse.from(saved);
    }
}
