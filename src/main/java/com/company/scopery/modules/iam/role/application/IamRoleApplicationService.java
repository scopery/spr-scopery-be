package com.company.scopery.modules.iam.role.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.role.application.command.CreateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.UpdateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.query.SearchIamRoleQuery;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import com.company.scopery.modules.iam.role.domain.IamRole;
import com.company.scopery.modules.iam.role.domain.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.IamRoleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamRoleApplicationService {

    private final IamRoleRepository iamRoleRepository;
    private final IamActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;

    public IamRoleApplicationService(IamRoleRepository iamRoleRepository,
                                      IamActivityLogger activityLogger,
                                      CurrentUserAuthorizationService currentUserService) {
        this.iamRoleRepository = iamRoleRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public IamRoleResponse createSystemRole(CreateIamRoleCommand command) {
        IamRoleCode code = IamRoleCode.of(command.code());

        if (iamRoleRepository.existsByCode(code)) {
            throw IamExceptions.roleCodeAlreadyExists(code.value());
        }

        IamRoleSource source = IamEnumParser.parseRequired(
                IamRoleSource.class, command.roleSource(),
                IamErrorCatalog.INVALID_IAM_ROLE_SOURCE.code(), "roleSource");

        UUID parentRoleId = resolveAndValidateParent(command.parentRoleId(), source);

        IamRole role = IamRole.createSystem(code, command.name(), command.description(), source, parentRoleId);
        IamRole saved = iamRoleRepository.save(role);

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.CREATE_SYSTEM_ROLE, "System role created: " + saved.code().value());

        return IamRoleResponse.from(saved);
    }

    @Transactional
    public IamRoleResponse createWorkspaceRole(CreateIamRoleCommand command) {
        if (command.workspaceId() == null) {
            throw IamExceptions.roleWorkspaceScopeRequiresWorkspaceId();
        }

        IamRoleCode code = IamRoleCode.of(command.code());

        if (iamRoleRepository.existsByCodeAndWorkspaceId(code, command.workspaceId())) {
            throw IamExceptions.roleWorkspaceCodeAlreadyExists(code.value(), command.workspaceId());
        }

        UUID parentRoleId = null;
        if (command.parentRoleId() != null) {
            IamRole parent = iamRoleRepository.findById(command.parentRoleId())
                    .orElseThrow(() -> IamExceptions.roleParentNotFound(command.parentRoleId()));
            if (parent.roleSource() != IamRoleSource.SYSTEM_TEMPLATE) {
                throw IamExceptions.roleParentMustBeTemplate(parent.code().value());
            }
            parentRoleId = parent.id();
        }

        IamRole role = IamRole.createWorkspace(code, command.name(), command.description(),
                command.workspaceId(), parentRoleId);
        IamRole saved = iamRoleRepository.save(role);

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.CREATE_WORKSPACE_ROLE,
                "Workspace role created: " + saved.code().value() + " in workspace " + command.workspaceId());

        return IamRoleResponse.from(saved);
    }

    @Transactional
    public IamRoleResponse updateRole(UpdateIamRoleCommand command) {
        IamRole role = findOrThrow(command.id());
        if (role.isDeleted()) {
            throw IamExceptions.roleDeletedCannotBeModified(role.id());
        }
        IamRole saved = iamRoleRepository.save(role.update(command.name(), command.description()));

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.UPDATE_IAM_ROLE, "Role updated: " + saved.code().value());

        return IamRoleResponse.from(saved);
    }

    @Transactional
    public IamRoleResponse activateRole(UUID id) {
        IamRole role = findOrThrow(id);
        if (role.isDeleted()) {
            throw IamExceptions.roleDeletedCannotBeModified(role.id());
        }
        IamRole saved = iamRoleRepository.save(role.activate());
        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.ACTIVATE_IAM_ROLE, "Role activated: " + saved.code().value());
        return IamRoleResponse.from(saved);
    }

    @Transactional
    public IamRoleResponse deactivateRole(UUID id) {
        IamRole role = findOrThrow(id);
        if (role.isDeleted()) {
            throw IamExceptions.roleDeletedCannotBeModified(role.id());
        }
        IamRole saved = iamRoleRepository.save(role.deactivate());
        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.DEACTIVATE_IAM_ROLE, "Role deactivated: " + saved.code().value());
        return IamRoleResponse.from(saved);
    }

    @Transactional
    public IamRoleResponse softDeleteRole(UUID id) {
        IamRole role = findOrThrow(id);
        if (role.isDeleted()) {
            throw IamExceptions.roleDeletedCannotBeModified(role.id());
        }
        if (role.roleSource() == IamRoleSource.SYSTEM_BUILT_IN) {
            throw IamExceptions.roleSystemBuiltInCannotBeDeleted(role.code().value());
        }
        UUID actorId = currentUserService.resolveCurrentUser().id();
        IamRole saved = iamRoleRepository.save(role.softDelete(actorId));

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.SOFT_DELETE_IAM_ROLE, "Role soft-deleted: " + saved.code().value());
        return IamRoleResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public IamRoleResponse getRole(UUID id) {
        return IamRoleResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<IamRoleResponse> searchRoles(SearchIamRoleQuery query) {
        IamRoleStatus status = IamEnumParser.parseOptional(
                IamRoleStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_ROLE_STATUS.code(), "status");
        IamRoleScope roleScope = IamEnumParser.parseOptional(
                IamRoleScope.class, query.roleScope(),
                IamErrorCatalog.INVALID_IAM_ROLE_SCOPE.code(), "roleScope");
        IamRoleSource roleSource = IamEnumParser.parseOptional(
                IamRoleSource.class, query.roleSource(),
                IamErrorCatalog.INVALID_IAM_ROLE_SOURCE.code(), "roleSource");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, IamSortFields.CREATED_AT));
        return iamRoleRepository.findAll(query.keyword(), query.workspaceId(), roleScope,
                        roleSource, status, query.includeDeleted(), pageable)
                .map(IamRoleResponse::from);
    }

    private IamRole findOrThrow(UUID id) {
        return iamRoleRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamRoleNotFound(id));
    }

    private UUID resolveAndValidateParent(UUID parentRoleId, IamRoleSource source) {
        if (parentRoleId == null) return null;
        IamRole parent = iamRoleRepository.findById(parentRoleId)
                .orElseThrow(() -> IamExceptions.roleParentNotFound(parentRoleId));
        if (parent.roleSource() != IamRoleSource.SYSTEM_TEMPLATE) {
            throw IamExceptions.roleParentMustBeTemplate(parent.code().value());
        }
        return parent.id();
    }
}
