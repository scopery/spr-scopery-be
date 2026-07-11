package com.company.scopery.modules.iam.user.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.user.application.query.SearchIamUserQuery;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamUserQueryService {

    private final IamUserRepository iamUserRepository;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public IamUserQueryService(IamUserRepository iamUserRepository,
                                IamSystemAuthorizationService systemAuthorizationService) {
        this.iamUserRepository = iamUserRepository;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    @Transactional(readOnly = true)
    public IamUserResponse getUser(UUID id) {
        systemAuthorizationService.requireSystemRight(IamAuthorities.SYSTEM_IAM_VIEW_USER.legacyRightCode());
        return IamUserResponse.from(iamUserRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamUserNotFound(id)));
    }

    @Transactional(readOnly = true)
    public PageResult<IamUserResponse> searchUsers(SearchIamUserQuery query) {
        systemAuthorizationService.requireSystemRight(IamAuthorities.SYSTEM_IAM_VIEW_USER.legacyRightCode());
        IamUserStatus status = IamEnumParser.parseOptional(
                IamUserStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_USER_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), IamSortFields.CREATED_AT, false);
        return iamUserRepository.findAll(query.keyword(), status, pageQuery)
                .map(IamUserResponse::from);
    }
}
