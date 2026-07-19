package com.company.scopery.modules.notification.advanced.digest.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.advanced.digest.application.command.CreateDigestRuleCommand;
import com.company.scopery.modules.notification.advanced.digest.application.response.DigestRuleResponse;
import com.company.scopery.modules.notification.advanced.digest.domain.model.*;
import com.company.scopery.modules.notification.advanced.shared.error.AdvancedNotificationExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateDigestRuleAction {
    private final DigestRuleRepository rules; private final CurrentUserAuthorizationService currentUser; private final WorkspaceIamIntegrationService iam;
    public CreateDigestRuleAction(DigestRuleRepository rules, CurrentUserAuthorizationService currentUser, WorkspaceIamIntegrationService iam) {
        this.rules=rules; this.currentUser=currentUser; this.iam=iam;
    }
    @Transactional
    public DigestRuleResponse execute(CreateDigestRuleCommand c) {
        try { iam.requireWorkspaceAccess(c.workspaceId(), currentUser.resolveCurrentUser().id(), IamAuthorities.DIGEST_RULE_MANAGE); }
        catch (RuntimeException ex) { throw AdvancedNotificationExceptions.accessDenied(); }
        return DigestRuleResponse.from(rules.save(DigestRule.create(c.workspaceId(), c.code(), c.name(), c.scope(), c.frequency(), c.scheduleConfigJson())));
    }
}
