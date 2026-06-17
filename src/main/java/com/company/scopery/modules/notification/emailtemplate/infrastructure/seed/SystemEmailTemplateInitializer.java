package com.company.scopery.modules.notification.emailtemplate.infrastructure.seed;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionRepository;
import com.company.scopery.modules.notification.emailrule.domain.*;
import com.company.scopery.modules.notification.emailtemplate.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Order(20)
public class SystemEmailTemplateInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(SystemEmailTemplateInitializer.class);

    private final EmailTemplateRepository templateRepository;
    private final EmailRuleRepository ruleRepository;
    private final EventDefinitionRepository eventDefinitionRepository;

    public SystemEmailTemplateInitializer(EmailTemplateRepository templateRepository,
                                           EmailRuleRepository ruleRepository,
                                           EventDefinitionRepository eventDefinitionRepository) {
        this.templateRepository = templateRepository;
        this.ruleRepository = ruleRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        seedWorkspaceInvitationTemplate();
        seedJoinRequestApprovedTemplate();
        log.info("[SystemEmailTemplateSeed] System template seeding complete");
    }

    private void seedWorkspaceInvitationTemplate() {
        String templateCode = "WORKSPACE_INVITATION_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;

        UUID eventDefId = findEventDefinitionId("WORKSPACE_INVITATION_CREATED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition WORKSPACE_INVITATION_CREATED not found, skipping template seed");
            return;
        }

        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "Workspace Invitation Email",
                "Sent to users who are invited to join a workspace",
                eventDefId);
        template = templateRepository.save(template);

        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "You've been invited to join {{workspace.name}}",
                "<h1>You're invited!</h1><p>Hi {{invitee.name}},</p>" +
                "<p>{{inviter.name}} has invited you to join <strong>{{workspace.name}}</strong>.</p>" +
                "<p><a href=\"{{invitation.link}}\">Accept Invitation</a></p>" +
                "<p>This invitation expires at {{invitation.expiresAt}}.</p>",
                "Hi {{invitee.name}}, you've been invited to join {{workspace.name}} by {{inviter.name}}. " +
                "Accept at: {{invitation.link}}");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);

        seedRule("RULE_WORKSPACE_INVITATION_EMAIL", "Workspace Invitation Rule",
                eventDefId, template.id(), EmailRecipientStrategy.INVITEE_EMAIL, true);
    }

    private void seedJoinRequestApprovedTemplate() {
        String templateCode = "WORKSPACE_JOIN_REQUEST_APPROVED_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;

        UUID eventDefId = findEventDefinitionId("WORKSPACE_JOIN_REQUEST_APPROVED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition WORKSPACE_JOIN_REQUEST_APPROVED not found, skipping template seed");
            return;
        }

        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "Join Request Approved Email",
                "Sent when a workspace join request is approved",
                eventDefId);
        template = templateRepository.save(template);

        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "Your request to join {{workspace.name}} has been approved",
                "<h1>Welcome to {{workspace.name}}!</h1>" +
                "<p>Hi {{requester.name}},</p>" +
                "<p>Your request to join <strong>{{workspace.name}}</strong> has been approved by {{approver.name}}.</p>",
                "Hi {{requester.name}}, your request to join {{workspace.name}} has been approved.");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);

        seedRule("RULE_JOIN_REQUEST_APPROVED_EMAIL", "Join Request Approved Rule",
                eventDefId, template.id(), EmailRecipientStrategy.REQUESTER_EMAIL, true);
    }

    private void seedRule(String code, String name, UUID eventDefId, UUID templateId,
                           EmailRecipientStrategy strategy, boolean enabled) {
        if (ruleRepository.existsByCode(code)) return;
        EmailRule rule = EmailRule.createSystem(code, name, null, eventDefId, templateId, strategy, null, 10);
        if (!enabled) rule.disable();
        ruleRepository.save(rule);
    }

    private UUID findEventDefinitionId(String code) {
        return eventDefinitionRepository.findByCode(EventDefinitionCode.of(code))
                .map(e -> e.id())
                .orElse(null);
    }
}
