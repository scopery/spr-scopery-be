package com.company.scopery.modules.project.wbs.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WbsNodeRepository {

    WbsNode save(WbsNode node);

    Optional<WbsNode> findById(UUID id);

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    boolean existsBySortOrderUnderParent(UUID projectId, UUID parentId, int sortOrder);

    boolean hasActiveChildrenOrLinkedTasks(UUID nodeId);

    List<WbsNode> findAllDescendants(UUID nodeId);

    PageResult<WbsNode> search(UUID projectId, UUID phaseId, UUID parentId, WbsNodeStatus status, PageQuery pageQuery);

    List<WbsNode> findAllByProjectId(UUID projectId);
}
