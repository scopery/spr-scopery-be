package com.company.scopery.modules.iam.role.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.role.application.query.SearchIamRoleQuery;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamRoleQueryService {

    private final IamRoleRepository iamRoleRepository;

    public IamRoleQueryService(IamRoleRepository iamRoleRepository) {
        this.iamRoleRepository = iamRoleRepository;
    }

    @Transactional(readOnly = true)
    public IamRoleResponse getRole(UUID id) {
        return IamRoleResponse.from(iamRoleRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamRoleNotFound(id)));
    }

    @Transactional(readOnly = true)
    public PageResult<IamRoleResponse> searchRoles(SearchIamRoleQuery query) {
        IamRoleStatus status = IamEnumParser.parseOptional(
                IamRoleStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_ROLE_STATUS.code(), "status");
        IamRoleScope roleScope = IamEnumParser.parseOptional(
                IamRoleScope.class, query.roleScope(),
                IamErrorCatalog.INVALID_IAM_ROLE_SCOPE.code(), "roleScope");
        IamRoleSource roleSource = IamEnumParser.parseOptional(
                IamRoleSource.class, query.roleSource(),
                IamErrorCatalog.INVALID_IAM_ROLE_SOURCE.code(), "roleSource");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), IamSortFields.CREATED_AT, false);
        return iamRoleRepository.findAll(query.keyword(), query.workspaceId(), roleScope,
                        roleSource, status, query.includeDeleted(), pageQuery)
                .map(IamRoleResponse::from);
    }
}
