package com.company.scopery.modules.project.templatewbs.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.TemplateAccessSupport;
import com.company.scopery.modules.project.shared.support.TemplateVersionMutationGuard;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplate;
import com.company.scopery.modules.project.template.domain.model.ProjectTemplateRepository;
import com.company.scopery.modules.project.templatewbs.application.command.MoveProjectTemplateWbsNodeCommand;
import com.company.scopery.modules.project.templatewbs.application.response.ProjectTemplateWbsNodeResponse;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNode;
import com.company.scopery.modules.project.templatewbs.domain.model.ProjectTemplateWbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class MoveProjectTemplateWbsNodeAction {

    private final ProjectTemplateRepository templateRepository;
    private final ProjectTemplateWbsNodeRepository wbsRepository;
    private final TemplateVersionMutationGuard mutationGuard;
    private final TemplateAccessSupport authorizationSupport;
    private final ProjectActivityLogger activityLogger;

    public MoveProjectTemplateWbsNodeAction(ProjectTemplateRepository templateRepository,
                                            ProjectTemplateWbsNodeRepository wbsRepository,
                                            TemplateVersionMutationGuard mutationGuard,
                                            TemplateAccessSupport authorizationSupport,
                                            ProjectActivityLogger activityLogger) {
        this.templateRepository = templateRepository;
        this.wbsRepository = wbsRepository;
        this.mutationGuard = mutationGuard;
        this.authorizationSupport = authorizationSupport;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProjectTemplateWbsNodeResponse execute(MoveProjectTemplateWbsNodeCommand cmd) {
        ProjectTemplate template = templateRepository.findById(cmd.templateId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateNotFound(cmd.templateId()));
        authorizationSupport.requireUpdate(template);
        mutationGuard.requireDraftVersion(cmd.templateId(), cmd.versionId());

        ProjectTemplateWbsNode node = wbsRepository.findById(cmd.templateWbsNodeId())
                .orElseThrow(() -> ProjectExceptions.projectTemplateWbsNodeNotFound(cmd.templateWbsNodeId()));
        if (!node.templateVersionId().equals(cmd.versionId())) {
            throw ProjectExceptions.projectTemplateWbsNodePathMismatch(node.id(), cmd.versionId());
        }

        int newDepth = 0;
        if (cmd.newParentId() != null) {
            if (cmd.newParentId().equals(node.id())) {
                throw ProjectExceptions.projectTemplateWbsNodeCycleDetected();
            }
            ProjectTemplateWbsNode parent = wbsRepository.findById(cmd.newParentId())
                    .orElseThrow(() -> ProjectExceptions.projectTemplateWbsNodeParentInvalid(cmd.newParentId()));
            if (!parent.templateVersionId().equals(cmd.versionId())) {
                throw ProjectExceptions.projectTemplateWbsNodeParentInvalid(cmd.newParentId());
            }
            assertNoCycle(node.id(), cmd.newParentId());
            newDepth = parent.depth() + 1;
        }

        ProjectTemplateWbsNode saved = wbsRepository.save(
                node.move(cmd.newParentId(), newDepth, cmd.newOrderIndex()));

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_TEMPLATE_WBS_NODE,
                saved.id(),
                ProjectActivityActions.MOVE_PROJECT_TEMPLATE_WBS_NODE,
                "Template WBS node moved: " + saved.title()
        );

        return ProjectTemplateWbsNodeResponse.from(saved);
    }

    private void assertNoCycle(UUID nodeId, UUID newParentId) {
        Set<UUID> seen = new HashSet<>();
        UUID cur = newParentId;
        while (cur != null) {
            if (cur.equals(nodeId)) {
                throw ProjectExceptions.projectTemplateWbsNodeCycleDetected();
            }
            if (!seen.add(cur)) {
                throw ProjectExceptions.projectTemplateWbsNodeCycleDetected();
            }
            ProjectTemplateWbsNode parent = wbsRepository.findById(cur).orElse(null);
            cur = parent != null ? parent.parentId() : null;
        }
    }
}
