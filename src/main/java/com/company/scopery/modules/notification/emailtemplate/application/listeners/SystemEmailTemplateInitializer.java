package com.company.scopery.modules.notification.emailtemplate.application.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRuleRepository;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateVersion;
import com.company.scopery.modules.notification.emailtemplate.domain.valueobject.EmailTemplateCode;
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
        seedWorkspaceInvitationCreatedAliasTemplate();
        seedJoinRequestApprovedTemplate();
        seedJoinRequestCreatedAdminTemplate();
        seedJoinRequestRejectedTemplate();
        seedOrgInvitationTemplate();
        seedPasswordResetRequestTemplate();
        seedAiExecutionFailedTemplate();
        seedAiUsagePolicyBlockedTemplate();
        seedResourceOverloadDetectedTemplate();
        log.info("[SystemEmailTemplateSeed] System template seeding complete");
    }

    private void seedResourceOverloadDetectedTemplate() {
        String templateCode = "RESOURCE_OVERLOAD_DETECTED_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;
        UUID eventDefId = findEventDefinitionId("RESOURCE_OVERLOAD_DETECTED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition RESOURCE_OVERLOAD_DETECTED not found, skipping {}", templateCode);
            return;
        }
        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "Resource Overload Detected Email",
                "Seed-only ops alert when resource capacity overload is detected",
                eventDefId);
        template = templateRepository.save(template);
        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "Resource overload detected (count {{overloadCount}})",
                "<h1>Resource overload detected</h1>" +
                        "<p>Overload count: {{overloadCount}}</p>" +
                        "<p>Capacity gap hours: {{capacityGapHours}}</p>",
                "Resource overload detected. Count={{overloadCount}}, gapHours={{capacityGapHours}}.");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);
        if (!ruleRepository.existsByCode("RULE_RESOURCE_OVERLOAD_DETECTED_EMAIL")) {
            EmailRule staticRule = EmailRule.createSystem(
                    "RULE_RESOURCE_OVERLOAD_DETECTED_EMAIL",
                    "Resource Overload Detected Rule",
                    "Fan-out to ops mailbox when overload is detected (EmailOutbox path)",
                    eventDefId,
                    template.id(),
                    EmailRecipientStrategy.STATIC_EMAIL,
                    "{\"email\":\"ops@scopery.local\"}",
                    5);
            ruleRepository.save(staticRule);
        }
    }

    private void seedAiExecutionFailedTemplate() {
        String templateCode = "AI_EXECUTION_FAILED_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;
        UUID eventDefId = findEventDefinitionId("AI_EXECUTION_FAILED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition AI_EXECUTION_FAILED not found, skipping {}", templateCode);
            return;
        }
        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "AI Execution Failed Email",
                "Seed-only admin alert when an AI execution fails",
                eventDefId);
        template = templateRepository.save(template);
        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "AI execution failed: {{requestId}}",
                "<h1>AI execution failed</h1>" +
                "<p>Request ID: {{requestId}}</p>" +
                "<p>Error code: {{errorCode}}</p>" +
                "<p>Trace ID: {{traceId}}</p>",
                "AI execution failed for request {{requestId}} ({{errorCode}}). Trace: {{traceId}}");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);
        seedRule("RULE_AI_EXECUTION_FAILED_EMAIL", "AI Execution Failed Rule",
                eventDefId, template.id(), EmailRecipientStrategy.EVENT_TARGET_USER, false);
    }

    private void seedAiUsagePolicyBlockedTemplate() {
        String templateCode = "AI_USAGE_POLICY_BLOCKED_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;
        UUID eventDefId = findEventDefinitionId("AI_USAGE_POLICY_BLOCKED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition AI_USAGE_POLICY_BLOCKED not found, skipping {}", templateCode);
            return;
        }
        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "AI Usage Policy Blocked Email",
                "Seed-only admin alert when usage policy blocks an AI execution",
                eventDefId);
        template = templateRepository.save(template);
        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "AI usage policy blocked execution: {{requestId}}",
                "<h1>AI usage policy blocked</h1>" +
                "<p>Request ID: {{requestId}}</p>" +
                "<p>Policy: {{policyCode}}</p>" +
                "<p>Reason: {{blockReasonCode}}</p>" +
                "<p>Trace ID: {{traceId}}</p>",
                "AI usage policy {{policyCode}} blocked request {{requestId}}. Reason: {{blockReasonCode}}");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);
        seedRule("RULE_AI_USAGE_POLICY_BLOCKED_EMAIL", "AI Usage Policy Blocked Rule",
                eventDefId, template.id(), EmailRecipientStrategy.EVENT_TARGET_USER, false);
    }

    private void seedWorkspaceInvitationCreatedAliasTemplate() {
        // Spec Phase 03 name; reuse same event as legacy WORKSPACE_INVITATION_EMAIL.
        String templateCode = "WORKSPACE_INVITATION_CREATED_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;
        UUID eventDefId = findEventDefinitionId("WORKSPACE_INVITATION_CREATED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition WORKSPACE_INVITATION_CREATED not found, skipping {}", templateCode);
            return;
        }
        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "Workspace Invitation Created Email",
                "Phase 03 canonical code for workspace invitation emails",
                eventDefId);
        template = templateRepository.save(template);
        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "You've been invited to join {{workspace.name}}",
                "<h1>You're invited!</h1><p>Hi {{invitee.name}},</p>" +
                "<p>{{inviter.name}} has invited you to join <strong>{{workspace.name}}</strong>.</p>" +
                "<p><a href=\"{{invitation.link}}\">Accept Invitation</a></p>",
                "Hi {{invitee.name}}, invited to {{workspace.name}}. Accept: {{invitation.link}}");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);
        seedRule("RULE_WORKSPACE_INVITATION_CREATED_EMAIL", "Workspace Invitation Created Rule",
                eventDefId, template.id(), EmailRecipientStrategy.INVITEE_EMAIL, true);
    }

    private void seedOrgInvitationTemplate() {
        String templateCode = "ORG_INVITATION_CREATED_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;
        UUID eventDefId = findEventDefinitionId("ORG_INVITATION_CREATED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition ORG_INVITATION_CREATED not found, skipping template seed");
            return;
        }
        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "Organization Invitation Email",
                "Sent when a user is invited to join an organization",
                eventDefId);
        template = templateRepository.save(template);
        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "You've been invited to join {{organization.name}}",
                "<h1>Organization invitation</h1><p>Hi {{invitee.name}},</p>" +
                "<p>{{inviter.name}} invited you to join <strong>{{organization.name}}</strong>.</p>" +
                "<p><a href=\"{{invitation.url}}\">Accept invitation</a></p>" +
                "<p>Expires at {{invitation.expiresAt}}.</p>",
                "Hi {{invitee.name}}, invited to {{organization.name}}: {{invitation.url}}");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);
        seedRule("RULE_ORG_INVITATION_CREATED_EMAIL", "Org Invitation Created Rule",
                eventDefId, template.id(), EmailRecipientStrategy.INVITEE_EMAIL, true);
    }

    private void seedJoinRequestCreatedAdminTemplate() {
        String templateCode = "WORKSPACE_JOIN_REQUEST_CREATED_ADMIN_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;
        UUID eventDefId = findEventDefinitionId("WORKSPACE_JOIN_REQUEST_CREATED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition WORKSPACE_JOIN_REQUEST_CREATED not found, skipping template seed");
            return;
        }
        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "Join Request Created Admin Email",
                "Notifies workspace admins of a new join request (payload admin.email until right-based resolver is ready)",
                eventDefId);
        template = templateRepository.save(template);
        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "New join request for {{workspace.name}}",
                "<h1>Join request</h1><p>{{requester.name}} ({{requester.email}}) requested to join <strong>{{workspace.name}}</strong>.</p>",
                "{{requester.name}} ({{requester.email}}) requested to join {{workspace.name}}.");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);
        // Temporary: resolve via payload path admin.email using STATIC is wrong;
        // use EVENT_ACTOR until WORKSPACE_USERS_WITH_RIGHT is implemented — prefer INVITEE-like custom:
        // EmailRecipientResolver has no admin.email strategy; use STATIC only if configured.
        // Phase 03: seed rule disabled until admin recipient strategy exists; template still seeded.
        seedRule("RULE_JOIN_REQUEST_CREATED_ADMIN_EMAIL", "Join Request Created Admin Rule",
                eventDefId, template.id(), EmailRecipientStrategy.EVENT_ACTOR, false);
    }

    private void seedJoinRequestRejectedTemplate() {
        String templateCode = "WORKSPACE_JOIN_REQUEST_REJECTED_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;
        UUID eventDefId = findEventDefinitionId("WORKSPACE_JOIN_REQUEST_REJECTED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition WORKSPACE_JOIN_REQUEST_REJECTED not found, skipping template seed");
            return;
        }
        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "Join Request Rejected Email",
                "Sent when a workspace join request is rejected",
                eventDefId);
        template = templateRepository.save(template);
        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "Your request to join {{workspace.name}} was not approved",
                "<h1>Join request update</h1><p>Hi {{requester.name}},</p>" +
                "<p>Your request to join <strong>{{workspace.name}}</strong> was not approved.</p>",
                "Hi {{requester.name}}, your request to join {{workspace.name}} was not approved.");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);
        seedRule("RULE_JOIN_REQUEST_REJECTED_EMAIL", "Join Request Rejected Rule",
                eventDefId, template.id(), EmailRecipientStrategy.REQUESTER_EMAIL, true);
    }

    private void seedPasswordResetRequestTemplate() {
        String templateCode = "IAM_PASSWORD_RESET_REQUEST_EMAIL";
        if (templateRepository.existsByCode(templateCode)) return;

        UUID eventDefId = findEventDefinitionId("IAM_PASSWORD_RESET_REQUESTED");
        if (eventDefId == null) {
            log.warn("[SystemEmailTemplateSeed] Event definition IAM_PASSWORD_RESET_REQUESTED not found, skipping template seed");
            return;
        }

        EmailTemplate template = EmailTemplate.createSystem(
                EmailTemplateCode.of(templateCode),
                "IAM Password Reset Request Email",
                "Sent when a user requests a password reset (token only in reset.url)",
                eventDefId);
        template = templateRepository.save(template);

        EmailTemplateVersion version = EmailTemplateVersion.createDraft(
                template.id(), 1,
                "Reset your Scopery password",
                "<h1>Password reset</h1><p>Hi {{user.fullName}},</p>" +
                "<p>We received a request to reset your password.</p>" +
                "<p><a href=\"{{reset.url}}\">Reset password</a></p>" +
                "<p>This link expires at {{reset.expiresAt}}.</p>" +
                "<p>If you did not request this, you can ignore this email. Contact {{support.email}} if you need help.</p>",
                "Hi {{user.fullName}}, reset your password: {{reset.url}} (expires {{reset.expiresAt}}). Support: {{support.email}}");
        version.publish();
        version = templateRepository.saveVersion(version);
        template.publishVersion(version.id());
        templateRepository.save(template);

        seedRule("RULE_IAM_PASSWORD_RESET_REQUEST_EMAIL", "IAM Password Reset Request Rule",
                eventDefId, template.id(), EmailRecipientStrategy.EVENT_TARGET_USER, true);
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
