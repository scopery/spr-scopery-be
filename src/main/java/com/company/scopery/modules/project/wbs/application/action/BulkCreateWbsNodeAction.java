package com.company.scopery.modules.project.wbs.application.action;

import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.wbs.application.command.BulkCreateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.command.CreateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.response.WbsNodeResponse;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class BulkCreateWbsNodeAction {

    private final WbsNodeRepository wbsNodeRepository;
    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;

    public BulkCreateWbsNodeAction(WbsNodeRepository wbsNodeRepository,
                                   ProjectPhaseRepository projectPhaseRepository,
                                   ProjectWorkspaceAuthorizationService authorizationService,
                                   ProjectMutationGuard mutationGuard) {
        this.wbsNodeRepository = wbsNodeRepository;
        this.projectPhaseRepository = projectPhaseRepository;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
    }

    @Transactional
    public List<WbsNodeResponse> execute(BulkCreateWbsNodeCommand cmd) {
        authorizationService.requireWbsCreate(cmd.projectId());
        mutationGuard.requireMutableProject(cmd.projectId());

        // Track nodes saved in this batch so items can reference each other as parents
        Map<UUID, WbsNode> savedThisBatch = new LinkedHashMap<>();
        List<WbsNodeResponse> results = new ArrayList<>();

        for (CreateWbsNodeCommand item : cmd.items()) {
            ProjectPhase phase = projectPhaseRepository.findById(item.projectPhaseId())
                    .orElseThrow(() -> ProjectExceptions.projectPhaseNotFound(item.projectPhaseId()));

            if (!phase.projectId().equals(cmd.projectId())) {
                throw ProjectExceptions.wbsNodePhaseMismatch(item.projectPhaseId(), cmd.projectId());
            }

            if (phase.status() != ProjectPhaseStatus.PLANNED && phase.status() != ProjectPhaseStatus.ACTIVE) {
                throw ProjectExceptions.projectPhaseNotActive(phase.id());
            }

            WbsNode parent = null;
            if (item.parentId() != null) {
                // Check within-batch parents first (supports creating parent+child in one batch)
                parent = savedThisBatch.get(item.parentId());
                if (parent == null) {
                    parent = wbsNodeRepository.findById(item.parentId())
                            .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(item.parentId()));
                    if (!parent.projectId().equals(cmd.projectId())) {
                        throw ProjectExceptions.wbsNodeProjectMismatch(parent.id(), cmd.projectId());
                    }
                    if (!parent.projectPhaseId().equals(item.projectPhaseId())) {
                        throw ProjectExceptions.wbsNodePhaseMismatch(parent.id(), item.projectPhaseId());
                    }
                }
            }

            if (wbsNodeRepository.existsByProjectIdAndCode(cmd.projectId(), item.code())) {
                throw ProjectExceptions.wbsNodeCodeAlreadyExists(item.code(), cmd.projectId());
            }

            if (wbsNodeRepository.existsBySortOrderUnderParent(cmd.projectId(), item.parentId(), item.sortOrder())) {
                throw ProjectExceptions.wbsNodeSortOrderConflict(item.sortOrder());
            }

            WbsNodeType nodeType = ProjectEnumParser.parseRequired(
                    WbsNodeType.class, item.nodeType(), "WBS_NODE_INVALID_TYPE", "nodeType");

            int level = (parent == null) ? 1 : parent.level() + 1;
            String path = (parent == null) ? item.code() : parent.path() + "/" + item.code();

            WbsNode node = WbsNode.create(
                    cmd.projectId(),
                    item.projectPhaseId(),
                    item.parentId(),
                    item.code(),
                    item.title(),
                    item.description(),
                    nodeType,
                    level,
                    path,
                    item.sortOrder()
            );

            WbsNode saved = wbsNodeRepository.save(node);
            savedThisBatch.put(saved.id(), saved);
            results.add(WbsNodeResponse.from(saved));
        }

        return results;
    }
}
