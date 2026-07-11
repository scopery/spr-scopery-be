package com.company.scopery.modules.project.wbs.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.wbs.application.query.SearchWbsNodeQuery;
import com.company.scopery.modules.project.wbs.application.response.WbsNodeResponse;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.company.scopery.modules.project.shared.constant.ProjectSortFields.SORT_ORDER;

@Service
public class WbsNodeQueryService {

    private final WbsNodeRepository wbsNodeRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public WbsNodeQueryService(WbsNodeRepository wbsNodeRepository,
                               ProjectWorkspaceAuthorizationService authorizationService) {
        this.wbsNodeRepository = wbsNodeRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public WbsNodeResponse getWbsNode(UUID projectId, UUID id) {
        authorizationService.requireProjectPermission(projectId, IamAuthorities.PROJECT_WBS_VIEW);

        WbsNode node = wbsNodeRepository.findById(id)
                .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(id));

        if (!node.projectId().equals(projectId)) {
            throw ProjectExceptions.wbsNodeProjectMismatch(id, projectId);
        }

        return WbsNodeResponse.from(node);
    }

    @Transactional(readOnly = true)
    public PageResult<WbsNodeResponse> searchWbsNodes(SearchWbsNodeQuery query) {
        authorizationService.requireProjectPermission(query.projectId(), IamAuthorities.PROJECT_WBS_VIEW);

        WbsNodeStatus status = ProjectEnumParser.parseOptional(
                WbsNodeStatus.class, query.status(), "WBS_NODE_INVALID_STATUS", "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), SORT_ORDER, true);

        return wbsNodeRepository.search(
                query.projectId(),
                query.phaseId(),
                query.parentId(),
                status,
                pageQuery
        ).map(WbsNodeResponse::from);
    }

    @Transactional(readOnly = true)
    public List<WbsNodeResponse> getWbsTree(UUID projectId, UUID phaseId) {
        authorizationService.requireProjectPermission(projectId, IamAuthorities.PROJECT_WBS_VIEW);

        List<WbsNode> nodes = wbsNodeRepository.findAllByProjectId(projectId);

        return nodes.stream()
                .filter(n -> phaseId == null || phaseId.equals(n.projectPhaseId()))
                .sorted(Comparator.comparing(WbsNode::path))
                .map(WbsNodeResponse::from)
                .toList();
    }
}
