package com.company.scopery.modules.iam.grant.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.grant.application.query.SearchIamAccessGrantQuery;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantPermissionActionResponse;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantRightResponse;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IamAccessGrantQueryService {

    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantRightRepository grantRightRepository;
    private final IamAccessGrantPermissionActionRepository grantActionRepository;
    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;
    private final IamRightRepository rightRepository;

    public IamAccessGrantQueryService(IamAccessGrantRepository grantRepository,
                                      IamAccessGrantRightRepository grantRightRepository,
                                      IamAccessGrantPermissionActionRepository grantActionRepository,
                                      IamPermissionRepository permissionRepository,
                                      IamPermissionActionDefinitionRepository actionRepository,
                                      IamRightRepository rightRepository) {
        this.grantRepository = grantRepository;
        this.grantRightRepository = grantRightRepository;
        this.grantActionRepository = grantActionRepository;
        this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository;
        this.rightRepository = rightRepository;
    }

    @Transactional(readOnly = true)
    public IamAccessGrantResponse getGrant(UUID id) {
        return IamAccessGrantResponse.from(grantRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamAccessGrantNotFound(id)));
    }

    @Transactional(readOnly = true)
    public List<IamAccessGrantRightResponse> getGrantRights(UUID grantId) {
        grantRepository.findById(grantId)
                .orElseThrow(() -> IamExceptions.iamAccessGrantNotFound(grantId));
        return grantRightRepository.findByGrantId(grantId).stream()
                .map(IamAccessGrantRightResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResult<IamAccessGrantResponse> searchGrants(SearchIamAccessGrantQuery query) {
        IamAccessGrantStatus status = IamEnumParser.parseOptional(
                IamAccessGrantStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_ACCESS_GRANT_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), IamSortFields.CREATED_AT, false);
        return grantRepository.findAll(query.subjectId(), query.resourceId(), query.workspaceId(), status, pageQuery)
                .map(IamAccessGrantResponse::from);
    }

    @Transactional(readOnly = true)
    public List<IamAccessGrantPermissionActionResponse> getGrantActions(UUID grantId) {
        IamAccessGrant grant = grantRepository.findById(grantId)
                .orElseThrow(() -> IamExceptions.iamAccessGrantNotFound(grantId));
        return grantActionRepository.findByGrantId(grantId).stream()
                .map(grantAction -> {
                    IamPermissionActionDefinition action = actionRepository.findById(grantAction.permissionActionId())
                            .orElseThrow(() -> IamExceptions.iamPermissionActionNotFound(
                                    grantAction.permissionActionId()));
                    return toResponse(grant, action, grantAction);
                })
                .sorted(Comparator
                        .comparing(IamAccessGrantPermissionActionResponse::permissionCode)
                        .thenComparing(IamAccessGrantPermissionActionResponse::actionCode))
                .toList();
    }

    private IamAccessGrantPermissionActionResponse toResponse(
            IamAccessGrant grant,
            IamPermissionActionDefinition action,
            com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction grantAction) {
        IamPermission permission = permissionRepository.findById(action.permissionId())
                .orElseThrow(() -> IamExceptions.iamPermissionNotFound(action.permissionId()));
        String legacyRightCode = Optional.ofNullable(action.rightId())
                .flatMap(rightRepository::findById)
                .map(right -> right.code().value())
                .orElse(null);
        return new IamAccessGrantPermissionActionResponse(
                grant.id(),
                grant.resourceId(),
                grant.workspaceId(),
                action.id(),
                permission.id(),
                permission.code().value(),
                action.actionCode(),
                action.rightId(),
                legacyRightCode,
                grantAction.createdAt());
    }
}
