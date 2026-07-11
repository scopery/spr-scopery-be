package com.company.scopery.modules.notification.emailrule.application.service;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailrule.application.query.SearchEmailRulesQuery;
import com.company.scopery.modules.notification.emailrule.application.response.EmailRuleResponse;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRuleRepository;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleScope;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRuleSearchCriteria;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleStatus;
import com.company.scopery.modules.notification.shared.NotificationEnumParser;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmailRuleQueryService {

    private final EmailRuleRepository ruleRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;

    public EmailRuleQueryService(EmailRuleRepository ruleRepository,
                                  CurrentUserAuthorizationService currentUserAuthorizationService,
                                  WorkspaceIamIntegrationService workspaceIamIntegrationService) {
        this.ruleRepository = ruleRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
    }

    @Transactional(readOnly = true)
    public EmailRuleResponse getRule(UUID id) {
        EmailRule rule = findOrThrow(id);
        if (rule.scope() == EmailRuleScope.WORKSPACE) {
            requireWorkspaceView(rule.workspaceId());
        }
        return EmailRuleResponse.from(rule);
    }

    @Transactional(readOnly = true)
    public PageResponse<EmailRuleResponse> searchRules(SearchEmailRulesQuery query) {
        EmailRuleScope scope = query.scope() != null ? NotificationEnumParser.parseRuleScope(query.scope()) : null;
        EmailRuleStatus status = query.status() != null ? NotificationEnumParser.parseRuleStatus(query.status()) : null;

        UUID workspaceId = query.workspaceId();
        if (workspaceId != null) {
            requireWorkspaceView(workspaceId);
        } else {
            // Without a workspace context, only the shared system catalog is browsable —
            // otherwise this would enumerate every workspace's email rules at once.
            scope = EmailRuleScope.SYSTEM;
        }

        EmailRuleSearchCriteria criteria = new EmailRuleSearchCriteria(
                query.keyword(), scope, status, workspaceId,
                query.eventDefinitionId(), query.templateId(), query.page(), query.size());

        List<EmailRuleResponse> items = ruleRepository.findAll(criteria)
                .stream().map(EmailRuleResponse::from).toList();
        long total = ruleRepository.countAll(criteria);
        int totalPages = query.size() == 0 ? 1 : (int) Math.ceil((double) total / query.size());
        return new PageResponse<>(items, query.page(), query.size(), total, totalPages,
                query.page() == 0, query.page() >= totalPages - 1);
    }

    private EmailRule findOrThrow(UUID id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> NotificationExceptions.emailRuleNotFound(id));
    }

    private void requireWorkspaceView(UUID workspaceId) {
        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        workspaceIamIntegrationService.requireWorkspaceAccess(
                workspaceId, actorId, IamAuthorities.NOTIFICATION_VIEW_RULE);
    }
}
