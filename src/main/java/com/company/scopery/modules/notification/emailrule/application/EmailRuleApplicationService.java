package com.company.scopery.modules.notification.emailrule.application;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionStatus;
import com.company.scopery.modules.notification.emailrule.application.command.CreateEmailRuleCommand;
import com.company.scopery.modules.notification.emailrule.application.command.UpdateEmailRuleCommand;
import com.company.scopery.modules.notification.emailrule.application.query.SearchEmailRulesQuery;
import com.company.scopery.modules.notification.emailrule.application.response.EmailRuleResponse;
import com.company.scopery.modules.notification.emailrule.domain.*;
import com.company.scopery.modules.notification.emailtemplate.domain.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.EmailTemplateStatus;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.NotificationEnumParser;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmailRuleApplicationService {

    private final EmailRuleRepository ruleRepository;
    private final EmailTemplateRepository templateRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final NotificationActivityLogger activityLogger;

    public EmailRuleApplicationService(EmailRuleRepository ruleRepository,
                                        EmailTemplateRepository templateRepository,
                                        EventDefinitionRepository eventDefinitionRepository,
                                        NotificationActivityLogger activityLogger) {
        this.ruleRepository = ruleRepository;
        this.templateRepository = templateRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EmailRuleResponse createRule(CreateEmailRuleCommand cmd) {
        EmailRuleScope scope = NotificationEnumParser.parseRuleScope(cmd.scope());
        EmailRecipientStrategy strategy = NotificationEnumParser.parseRecipientStrategy(cmd.recipientStrategy());

        eventDefinitionRepository.findById(cmd.eventDefinitionId())
                .filter(e -> e.status() == EventDefinitionStatus.ACTIVE)
                .orElseThrow(() -> NotificationExceptions.emailRuleEventDefinitionNotFound(cmd.eventDefinitionId()));

        templateRepository.findById(cmd.templateId())
                .filter(t -> t.status() == EmailTemplateStatus.ACTIVE)
                .orElseThrow(() -> NotificationExceptions.emailRuleTemplateNotFound(cmd.templateId()));

        if (ruleRepository.existsByCode(cmd.code().trim().toUpperCase())) {
            throw NotificationExceptions.emailRuleCodeAlreadyExists(cmd.code());
        }

        EmailRule rule = scope == EmailRuleScope.SYSTEM
                ? EmailRule.createSystem(cmd.code(), cmd.name(), cmd.description(),
                        cmd.eventDefinitionId(), cmd.templateId(), strategy, cmd.recipientConfigJson(), cmd.priority())
                : EmailRule.createWorkspace(cmd.code(), cmd.name(), cmd.description(),
                        cmd.workspaceId(), cmd.eventDefinitionId(), cmd.templateId(),
                        strategy, cmd.recipientConfigJson(), cmd.priority());

        rule = ruleRepository.save(rule);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, rule.id(),
                NotificationActivityActions.CREATE_EMAIL_RULE,
                "Email rule created: " + rule.code());
        return EmailRuleResponse.from(rule);
    }

    @Transactional
    public EmailRuleResponse updateRule(UpdateEmailRuleCommand cmd) {
        EmailRule rule = findOrThrow(cmd.id());
        if (rule.status() == EmailRuleStatus.DELETED) {
            throw NotificationExceptions.emailRuleNotFound(cmd.id());
        }
        EmailRecipientStrategy strategy = NotificationEnumParser.parseRecipientStrategy(cmd.recipientStrategy());
        rule.update(cmd.name(), cmd.description(), strategy, cmd.recipientConfigJson(), cmd.priority());
        rule = ruleRepository.save(rule);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, rule.id(),
                NotificationActivityActions.UPDATE_EMAIL_RULE,
                "Email rule updated: " + rule.code());
        return EmailRuleResponse.from(rule);
    }

    @Transactional
    public EmailRuleResponse activateRule(UUID id) {
        EmailRule rule = findOrThrow(id);
        rule.activate();
        rule = ruleRepository.save(rule);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, id,
                NotificationActivityActions.ACTIVATE_EMAIL_RULE, null);
        return EmailRuleResponse.from(rule);
    }

    @Transactional
    public EmailRuleResponse deactivateRule(UUID id) {
        EmailRule rule = findOrThrow(id);
        rule.deactivate();
        rule = ruleRepository.save(rule);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, id,
                NotificationActivityActions.DEACTIVATE_EMAIL_RULE, null);
        return EmailRuleResponse.from(rule);
    }

    @Transactional
    public EmailRuleResponse enableRule(UUID id) {
        EmailRule rule = findOrThrow(id);
        rule.enable();
        return EmailRuleResponse.from(ruleRepository.save(rule));
    }

    @Transactional
    public EmailRuleResponse disableRule(UUID id) {
        EmailRule rule = findOrThrow(id);
        rule.disable();
        return EmailRuleResponse.from(ruleRepository.save(rule));
    }

    @Transactional
    public void deleteRule(UUID id) {
        EmailRule rule = findOrThrow(id);
        rule.softDelete();
        ruleRepository.save(rule);
        activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, id,
                NotificationActivityActions.SOFT_DELETE_EMAIL_RULE, null);
    }

    @Transactional(readOnly = true)
    public EmailRuleResponse getRule(UUID id) {
        return EmailRuleResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EmailRuleResponse> searchRules(SearchEmailRulesQuery query) {
        EmailRuleScope scope = query.scope() != null ? NotificationEnumParser.parseRuleScope(query.scope()) : null;
        EmailRuleStatus status = query.status() != null ? NotificationEnumParser.parseRuleStatus(query.status()) : null;

        EmailRuleSearchCriteria criteria = new EmailRuleSearchCriteria(
                query.keyword(), scope, status, query.workspaceId(),
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
}
