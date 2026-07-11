package com.company.scopery.modules.iam.roleassignment.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.roleassignment.application.query.SearchRoleAssignmentQuery;
import com.company.scopery.modules.iam.roleassignment.application.response.IamRoleAssignmentResponse;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.roleassignment.domain.enums.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.enums.RoleAssigneeType;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamRoleAssignmentQueryService {

    private final IamRoleAssignmentRepository assignmentRepository;

    public IamRoleAssignmentQueryService(IamRoleAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional(readOnly = true)
    public IamRoleAssignmentResponse getAssignment(UUID id) {
        IamRoleAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamRoleAssignmentNotFound(id));
        return IamRoleAssignmentResponse.from(assignment);
    }

    @Transactional(readOnly = true)
    public PageResult<IamRoleAssignmentResponse> searchAssignments(SearchRoleAssignmentQuery query) {
        RoleAssigneeType assigneeType = IamEnumParser.parseOptional(
                RoleAssigneeType.class, query.assigneeType(),
                IamErrorCatalog.INVALID_ROLE_ASSIGNEE_TYPE.code(), "assigneeType");
        IamRoleAssignmentStatus status = IamEnumParser.parseOptional(
                IamRoleAssignmentStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_ROLE_ASSIGNMENT_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), IamSortFields.CREATED_AT, false);
        return assignmentRepository.findAll(
                query.roleId(), query.assigneeId(), assigneeType, status,
                query.workspaceId(), pageQuery)
                .map(IamRoleAssignmentResponse::from);
    }
}
