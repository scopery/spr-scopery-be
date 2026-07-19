package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.application.command.SuspendIamUserCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class SuspendIamUserAction {

    private final IamUserRepository iamUserRepository;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final IamActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;

    public SuspendIamUserAction(IamUserRepository iamUserRepository,
                                IamSystemAuthorizationService systemAuthorizationService,
                                IamActivityLogger activityLogger,
                                ImmutableAuditEventService auditEventService) {
        this.iamUserRepository = iamUserRepository;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public IamUserResponse execute(SuspendIamUserCommand command) {
        systemAuthorizationService.requireSystemRight(IamAuthorities.SYSTEM_IAM_MANAGE_USER.legacyRightCode());
        IamUser saved = iamUserRepository.save(iamUserRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamUserNotFound(command.id())).suspend());
        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.SUSPEND_IAM_USER, "User suspended: " + saved.username().value());
        auditEventService.record(AuditEventType.IAM_USER_SUSPENDED, saved.id(), "USER",
                "IAM_USER", saved.id(), null, null, null,
                java.util.Map.of("status", saved.status().name()), "User suspended");
        return IamUserResponse.from(saved);
    }
}
