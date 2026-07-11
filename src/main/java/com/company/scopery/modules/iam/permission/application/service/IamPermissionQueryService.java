package com.company.scopery.modules.iam.permission.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.permission.application.query.SearchIamPermissionQuery;
import com.company.scopery.modules.iam.permission.application.response.IamPermissionActionResponse;
import com.company.scopery.modules.iam.permission.application.response.IamPermissionResponse;
import com.company.scopery.modules.iam.permission.domain.enums.IamDataAccessPolicy;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionAssignableSubjectType;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionCategory;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionRiskLevel;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;
import com.company.scopery.modules.iam.permission.domain.enums.IamResourceScopeLevel;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IamPermissionQueryService {

    private static final int MATRIX_PAGE_SIZE = 100;

    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;
    private final IamRightRepository rightRepository;

    public IamPermissionQueryService(IamPermissionRepository permissionRepository,
                                     IamPermissionActionDefinitionRepository actionRepository,
                                     IamRightRepository rightRepository) {
        this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository;
        this.rightRepository = rightRepository;
    }

    @Transactional(readOnly = true)
    public IamPermissionResponse getPermission(UUID id) {
        IamPermission permission = findPermissionOrThrow(id);
        return toResponse(permission, actionRepository.findByPermissionId(permission.id()));
    }

    @Transactional(readOnly = true)
    public List<IamPermissionActionResponse> getPermissionActions(UUID permissionId) {
        IamPermission permission = findPermissionOrThrow(permissionId);
        return toActionResponses(permission, actionRepository.findByPermissionId(permission.id()));
    }

    @Transactional(readOnly = true)
    public PageResult<IamPermissionResponse> searchPermissions(SearchIamPermissionQuery query) {
        IamResourceScopeLevel resourceScopeLevel = IamEnumParser.parseOptional(
                IamResourceScopeLevel.class, query.resourceScopeLevel(),
                IamErrorCatalog.INVALID_IAM_RESOURCE_SCOPE_LEVEL.code(), "resourceScopeLevel");
        IamDataAccessPolicy dataAccessPolicy = IamEnumParser.parseOptional(
                IamDataAccessPolicy.class, query.dataAccessPolicy(),
                IamErrorCatalog.INVALID_IAM_DATA_ACCESS_POLICY.code(), "dataAccessPolicy");
        IamPermissionCategory permissionCategory = IamEnumParser.parseOptional(
                IamPermissionCategory.class, query.permissionCategory(),
                IamErrorCatalog.INVALID_IAM_PERMISSION_CATEGORY.code(), "permissionCategory");
        IamPermissionRiskLevel riskLevel = IamEnumParser.parseOptional(
                IamPermissionRiskLevel.class, query.riskLevel(),
                IamErrorCatalog.INVALID_IAM_PERMISSION_RISK_LEVEL.code(), "riskLevel");
        IamPermissionAssignableSubjectType assignableSubjectType = IamEnumParser.parseOptional(
                IamPermissionAssignableSubjectType.class, query.assignableSubjectType(),
                IamErrorCatalog.INVALID_IAM_PERMISSION_ASSIGNABLE_SUBJECT_TYPE.code(), "assignableSubjectType");
        IamPermissionStatus status = IamEnumParser.parseOptional(
                IamPermissionStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_PERMISSION_STATUS.code(), "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), IamSortFields.CODE, true);
        PageResult<IamPermission> permissions = permissionRepository.findAll(
                query.keyword(), query.moduleCode(), resourceScopeLevel, dataAccessPolicy,
                permissionCategory, riskLevel, assignableSubjectType, status, pageQuery);

        Map<UUID, List<IamPermissionActionDefinition>> actionsByPermissionId =
                actionRepository.findByPermissionIds(permissions.content().stream()
                                .map(IamPermission::id)
                                .toList())
                        .stream()
                        .collect(Collectors.groupingBy(IamPermissionActionDefinition::permissionId));

        return permissions.map(permission -> toResponse(
                permission, actionsByPermissionId.getOrDefault(permission.id(), List.of())));
    }

    @Transactional(readOnly = true)
    public List<IamPermissionResponse> getPermissionMatrix() {
        var query = new SearchIamPermissionQuery(null, null, null, null, null, null, null,
                IamPermissionStatus.ACTIVE.name(), 0, MATRIX_PAGE_SIZE);
        return searchPermissions(query).content();
    }

    private IamPermission findPermissionOrThrow(UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamPermissionNotFound(id));
    }

    private IamPermissionResponse toResponse(IamPermission permission,
                                             List<IamPermissionActionDefinition> actions) {
        return IamPermissionResponse.from(permission, toActionResponses(permission, actions));
    }

    private List<IamPermissionActionResponse> toActionResponses(IamPermission permission,
                                                                List<IamPermissionActionDefinition> actions) {
        Map<UUID, String> rightCodesById = resolveRightCodes(actions);
        return actions.stream()
                .map(action -> IamPermissionActionResponse.from(
                        action, permission.code().value(), rightCodesById.get(action.rightId())))
                .toList();
    }

    private Map<UUID, String> resolveRightCodes(List<IamPermissionActionDefinition> actions) {
        return actions.stream()
                .map(IamPermissionActionDefinition::rightId)
                .filter(id -> id != null)
                .distinct()
                .map(rightRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toMap(IamRight::id, right -> right.code().value()));
    }
}
