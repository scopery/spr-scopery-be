package com.company.scopery.modules.iam.user.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.constant.IamSortFields;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.user.application.command.CreateIamUserCommand;
import com.company.scopery.modules.iam.user.application.command.UpdateIamUserCommand;
import com.company.scopery.modules.iam.user.application.query.SearchIamUserQuery;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IamUserApplicationService {

    private final IamUserRepository iamUserRepository;
    private final IamActivityLogger activityLogger;

    public IamUserApplicationService(IamUserRepository iamUserRepository,
                                      IamActivityLogger activityLogger) {
        this.iamUserRepository = iamUserRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamUserResponse createUser(CreateIamUserCommand command) {
        Username username = Username.of(command.username());
        EmailAddress email = EmailAddress.of(command.email());

        if (iamUserRepository.existsByUsername(username)) {
            throw IamExceptions.usernameAlreadyExists(username.value());
        }
        if (iamUserRepository.existsByEmail(email)) {
            throw IamExceptions.emailAlreadyExists(email.value());
        }

        IamUser user = IamUser.create(username, email, command.fullName());
        IamUser saved = iamUserRepository.save(user);

        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.CREATE_IAM_USER, "User created: " + saved.username().value());

        return IamUserResponse.from(saved);
    }

    @Transactional
    public IamUserResponse updateUser(UpdateIamUserCommand command) {
        IamUser user = findOrThrow(command.id());
        if (user.status() == IamUserStatus.SUSPENDED) {
            throw IamExceptions.iamUserSuspendedCannotBeUpdated(user.username().value());
        }
        IamUser saved = iamUserRepository.save(user.update(command.fullName()));

        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.UPDATE_IAM_USER, "User updated: " + saved.username().value());

        return IamUserResponse.from(saved);
    }

    @Transactional
    public IamUserResponse activateUser(UUID id) {
        IamUser saved = iamUserRepository.save(findOrThrow(id).activate());
        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.ACTIVATE_IAM_USER, "User activated: " + saved.username().value());
        return IamUserResponse.from(saved);
    }

    @Transactional
    public IamUserResponse deactivateUser(UUID id) {
        IamUser saved = iamUserRepository.save(findOrThrow(id).deactivate());
        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.DEACTIVATE_IAM_USER, "User deactivated: " + saved.username().value());
        return IamUserResponse.from(saved);
    }

    @Transactional
    public IamUserResponse suspendUser(UUID id) {
        IamUser saved = iamUserRepository.save(findOrThrow(id).suspend());
        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.SUSPEND_IAM_USER, "User suspended: " + saved.username().value());
        return IamUserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public IamUserResponse getUser(UUID id) {
        return IamUserResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<IamUserResponse> searchUsers(SearchIamUserQuery query) {
        IamUserStatus status = IamEnumParser.parseOptional(
                IamUserStatus.class, query.status(),
                IamErrorCatalog.INVALID_IAM_USER_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, IamSortFields.CREATED_AT));
        return iamUserRepository.findAll(query.keyword(), status, pageable)
                .map(IamUserResponse::from);
    }

    private IamUser findOrThrow(UUID id) {
        return iamUserRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamUserNotFound(id));
    }
}
