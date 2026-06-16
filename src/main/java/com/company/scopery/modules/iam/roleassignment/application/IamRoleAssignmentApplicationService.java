package com.company.scopery.modules.iam.roleassignment.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.role.domain.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.IamRoleStatus;
import com.company.scopery.modules.iam.roleassignment.application.command.AssignRoleCommand;
import com.company.scopery.modules.iam.roleassignment.application.query.SearchRoleAssignmentQuery;
import com.company.scopery.modules.iam.roleassignment.application.response.IamRoleAssignmentResponse;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.RoleAssigneeType;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamRoleAssignmentApplicationService {

    private final IamRoleAssignmentRepository assignmentRepository;
    private final IamRoleRepository roleRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final IamActivityLogger activityLogger;

    public IamRoleAssignmentApplicationService(IamRoleAssignmentRepository assignmentRepository,
                                                IamRoleRepository roleRepository,
                                                WorkspaceMemberRepository workspaceMemberRepository,
                                                IamActivityLogger activityLogger) {
        this.assignmentRepository = assignmentRepository;
        this.roleRepository = roleRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamRoleAssignmentResponse assignRole(AssignRoleCommand command) {
        RoleAssigneeType assigneeType = IamEnumParser.parseRequired(
                RoleAssigneeType.class, command.assigneeType(),
                IamErrorCatalog.INVALID_ROLE_ASSIGNEE_TYPE.code(), "assigneeType");

        var role = roleRepository.findById(command.roleId())
                .orElseThrow(() -> IamExceptions.iamRoleNotFound(command.roleId()));

        if (role.status() != IamRoleStatus.ACTIVE) {
            throw IamExceptions.iamRoleNotActiveForAssignment(role.code().value());
        }

        if (assigneeType == RoleAssigneeType.USER && command.workspaceId() != null) {
            if (!workspaceMemberRepository.isActiveMember(command.workspaceId(), command.assigneeId())) {
                throw IamExceptions.iamRoleAssignmentRequiresWorkspaceMember(
                        command.assigneeId(), command.workspaceId());
            }
        }

        if (assignmentRepository.existsActiveAssignment(
                assigneeType, command.assigneeId(), command.roleId(), command.workspaceId())) {
            throw IamExceptions.iamRoleAssignmentAlreadyExists(command.roleId(), command.assigneeId());
        }

        IamRoleAssignment assignment = IamRoleAssignment.create(
                assigneeType, command.assigneeId(), command.roleId(),
                command.workspaceId(), command.assignedBy());
        IamRoleAssignment saved = assignmentRepository.save(assignment);

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE_ASSIGNMENT, saved.id(),
                IamActivityActions.ASSIGN_IAM_ROLE,
                "Role " + command.roleId() + " assigned to " + assigneeType + " " + command.assigneeId());

        return IamRoleAssignmentResponse.from(saved);
    }

    @Transactional
    public IamRoleAssignmentResponse activateAssignment(UUID id) {
        IamRoleAssignment assignment = findOrThrow(id);
        IamRoleAssignment saved = assignmentRepository.save(assignment.activate());

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE_ASSIGNMENT, saved.id(),
                IamActivityActions.ACTIVATE_IAM_ROLE_ASSIGNMENT,
                "Role assignment activated: " + saved.id());

        return IamRoleAssignmentResponse.from(saved);
    }

    @Transactional
    public IamRoleAssignmentResponse deactivateAssignment(UUID id) {
        IamRoleAssignment assignment = findOrThrow(id);
        IamRoleAssignment saved = assignmentRepository.save(assignment.deactivate());

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE_ASSIGNMENT, saved.id(),
                IamActivityActions.DEACTIVATE_IAM_ROLE_ASSIGNMENT,
                "Role assignment deactivated: " + saved.id());

        return IamRoleAssignmentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public IamRoleAssignmentResponse getAssignment(UUID id) {
        return IamRoleAssignmentResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<IamRoleAssignmentResponse> searchAssignments(SearchRoleAssignmentQuery query) {
        RoleAssigneeType assigneeType = IamEnumParser.parseOptional(
                RoleAssigneeType.class, query.assigneeType(),
                IamErrorCatalog.INVALID_ROLE_ASSIGNEE_TYPE.code(), "assigneeType");
        IamRoleAssignmentStatus status = IamEnumParser.parseOptional(
                IamRoleAssignmentStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_ROLE_ASSIGNMENT_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, IamSortFields.CREATED_AT));
        return assignmentRepository.findAll(
                query.roleId(), query.assigneeId(), assigneeType, status,
                query.workspaceId(), pageable)
                .map(IamRoleAssignmentResponse::from);
    }

    private IamRoleAssignment findOrThrow(UUID id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamRoleAssignmentNotFound(id));
    }
}
