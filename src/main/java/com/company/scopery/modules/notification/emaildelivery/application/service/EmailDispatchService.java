package com.company.scopery.modules.notification.emaildelivery.application.service;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventVariable;
import com.company.scopery.modules.notification.emaildelivery.domain.model.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.model.EmailDeliveryRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailMessage;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailProviderType;
import com.company.scopery.modules.notification.emailrule.application.service.EmailRecipientResolver;
import com.company.scopery.modules.notification.emailrule.application.service.EmailRuleMatcher;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailtemplate.application.service.EmailTemplateRenderer;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateStatus;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateVersion;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.notificationitem.application.service.NotificationItemCreator;
import com.company.scopery.modules.notification.shared.NotificationActivityActions;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationEntityTypes;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import com.company.scopery.modules.notification.shared.privacy.SensitivePayloadMasker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmailDispatchService {

    private static final Logger log = LoggerFactory.getLogger(EmailDispatchService.class);

    private final EventDefinitionRepository eventDefinitionRepository;
    private final EmailRuleMatcher ruleMatcher;
    private final EmailRecipientResolver recipientResolver;
    private final EmailTemplateRepository templateRepository;
    private final EmailTemplateRenderer templateRenderer;
    private final EmailDeliveryRepository deliveryRepository;
    private final EmailOutboxRepository outboxRepository;
    private final NotificationItemCreator notificationItemCreator;
    private final SensitivePayloadMasker sensitivePayloadMasker;
    private final NotificationProperties notificationProperties;
    private final NotificationActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;
    private final ObjectMapper objectMapper;

    public EmailDispatchService(EventDefinitionRepository eventDefinitionRepository,
                                 EmailRuleMatcher ruleMatcher,
                                 EmailRecipientResolver recipientResolver,
                                 EmailTemplateRepository templateRepository,
                                 EmailTemplateRenderer templateRenderer,
                                 EmailDeliveryRepository deliveryRepository,
                                 EmailOutboxRepository outboxRepository,
                                 NotificationItemCreator notificationItemCreator,
                                 SensitivePayloadMasker sensitivePayloadMasker,
                                 NotificationProperties notificationProperties,
                                 NotificationActivityLogger activityLogger,
                                 ImmutableAuditEventService auditEventService,
                                 ObjectMapper objectMapper) {
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.ruleMatcher = ruleMatcher;
        this.recipientResolver = recipientResolver;
        this.templateRepository = templateRepository;
        this.templateRenderer = templateRenderer;
        this.deliveryRepository = deliveryRepository;
        this.outboxRepository = outboxRepository;
        this.notificationItemCreator = notificationItemCreator;
        this.sensitivePayloadMasker = sensitivePayloadMasker;
        this.notificationProperties = notificationProperties;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void dispatch(EmailNotificationTriggerPayload triggerPayload) {
        if (!notificationProperties.isEnabled()) {
            log.debug("[EmailDispatch] Notification disabled, skipping dispatch for event {}",
                    triggerPayload.eventDefinitionId());
            return;
        }

        EventDefinition eventDef = eventDefinitionRepository
                .findById(triggerPayload.eventDefinitionId())
                .orElseThrow(() -> NotificationExceptions.dispatchEventDefinitionNotFound(
                        triggerPayload.eventDefinitionId()));

        if (eventDef.status() != EventDefinitionStatus.ACTIVE) {
            throw NotificationExceptions.dispatchEventDefinitionNotActive(triggerPayload.eventDefinitionId());
        }

        List<EmailRule> rules = ruleMatcher.matchRules(
                triggerPayload.eventDefinitionId(), triggerPayload.workspaceId());

        if (rules.isEmpty()) {
            log.debug("[EmailDispatch] No matching rules for event {}", triggerPayload.eventDefinitionId());
            return;
        }

        Set<String> sensitivePaths = loadSensitivePaths(triggerPayload.eventDefinitionId());
        String payloadJson = serializePayload(triggerPayload.payload());
        EmailProviderType providerType = resolveProviderType();

        for (EmailRule rule : rules) {
            dispatchForRule(rule, triggerPayload, payloadJson, providerType, sensitivePaths);
        }
    }

    private void dispatchForRule(EmailRule rule, EmailNotificationTriggerPayload triggerPayload,
                                  String payloadJson, EmailProviderType providerType,
                                  Set<String> sensitivePaths) {
        EmailTemplate template = templateRepository.findById(rule.templateId()).orElse(null);
        if (template == null || template.status() != EmailTemplateStatus.ACTIVE) {
            log.warn("[EmailDispatch] Template {} not found or inactive for rule {}", rule.templateId(), rule.code());
            return;
        }

        if (template.currentVersionId() == null) {
            log.warn("[EmailDispatch] Template {} has no published version for rule {}", template.id(), rule.code());
            return;
        }

        EmailTemplateVersion version = templateRepository.findVersionById(template.currentVersionId()).orElse(null);
        if (version == null) {
            log.warn("[EmailDispatch] Template version {} not found for rule {}", template.currentVersionId(), rule.code());
            return;
        }

        List<EmailRecipientResolver.RecipientResult> recipients =
                recipientResolver.resolveAll(rule, triggerPayload.payload());

        if (recipients.isEmpty()) {
            EmailDelivery skipped = EmailDelivery.createSkipped(
                    rule.id(), template.id(), version.id(),
                    triggerPayload.eventDefinitionId(), triggerPayload.workspaceId(),
                    "No recipients resolved", payloadJson);
            deliveryRepository.save(skipped);
            return;
        }

        for (EmailRecipientResolver.RecipientResult recipient : recipients) {
            dispatchForRecipient(rule, triggerPayload, payloadJson, providerType, sensitivePaths,
                    template, version, recipient);
        }
    }

    private void dispatchForRecipient(EmailRule rule, EmailNotificationTriggerPayload triggerPayload,
                                      String payloadJson, EmailProviderType providerType,
                                      Set<String> sensitivePaths, EmailTemplate template,
                                      EmailTemplateVersion version,
                                      EmailRecipientResolver.RecipientResult recipient) {
        if (recipient.skipped()) {
            EmailDelivery skipped = EmailDelivery.createSkipped(
                    rule.id(), template.id(), version.id(),
                    triggerPayload.eventDefinitionId(), triggerPayload.workspaceId(),
                    recipient.skipReason(), payloadJson);
            deliveryRepository.save(skipped);
            log.info("[EmailDispatch] Delivery SKIPPED for rule {}: {}", rule.code(), recipient.skipReason());
            return;
        }

        String dedupKey = buildDedupKey(triggerPayload, rule, recipient.email());
        if (outboxRepository.existsByDedupKey(dedupKey)) {
            activityLogger.logSuccess(NotificationEntityTypes.EMAIL_OUTBOX, rule.id(),
                    NotificationActivityActions.NOTIFICATION_DEDUPLICATED,
                    "Skipped duplicate email for rule " + rule.code() + " key=" + dedupKey);
            auditEventService.record(AuditEventType.NOTIFICATION_DEDUPLICATED, triggerPayload.actorUserId(), "SYSTEM",
                    NotificationEntityTypes.EMAIL_OUTBOX, rule.id(), null, triggerPayload.workspaceId(),
                    null, Map.of("dedupKey", dedupKey, "ruleId", rule.id().toString()),
                    "Duplicate notification skipped");
            log.info("[EmailDispatch] Dedup skip for rule {} key={}", rule.code(), dedupKey);
            return;
        }

        try {
            Map<String, Object> renderPayload = triggerPayload.payload();
            if (!rule.allowSensitiveVariables() && !sensitivePaths.isEmpty()) {
                renderPayload = sensitivePayloadMasker.mask(renderPayload, sensitivePaths);
                activityLogger.logSuccess(NotificationEntityTypes.EMAIL_RULE, rule.id(),
                        NotificationActivityActions.NOTIFICATION_SENSITIVE_VARIABLE_MASKED,
                        "Sensitive payload values masked for rule " + rule.code());
            }

            String renderedSubject = templateRenderer.render(version.subjectTemplate(), renderPayload);
            String renderedHtml = templateRenderer.render(version.htmlBodyTemplate(), renderPayload);
            String renderedText = templateRenderer.render(version.textBodyTemplate(), renderPayload);

            EmailDelivery delivery = EmailDelivery.create(
                    rule.id(), template.id(), version.id(),
                    triggerPayload.eventDefinitionId(), triggerPayload.workspaceId(),
                    recipient.email(), renderedSubject, renderedHtml, renderedText, payloadJson);
            delivery = deliveryRepository.save(delivery);

            EmailMessage message = new EmailMessage(
                    recipient.email(), renderedSubject, renderedHtml, renderedText);
            EmailOutbox outbox = EmailOutbox.create(delivery.id(), message, providerType, dedupKey);
            outboxRepository.save(outbox);

            UUID inAppUserId = recipient.userId() != null ? recipient.userId() : triggerPayload.actorUserId();
            notificationItemCreator.createIfAbsent(
                    inAppUserId, rule, triggerPayload, renderedSubject, renderedText, dedupKey);

            log.info("[EmailDispatch] Delivery CREATED for rule {} → to={}", rule.code(), recipient.email());

        } catch (Exception e) {
            log.error("[EmailDispatch] Render/dispatch failed for rule {}: {}", rule.code(), e.getMessage());
            EmailDelivery failed = EmailDelivery.createSkipped(
                    rule.id(), template.id(), version.id(),
                    triggerPayload.eventDefinitionId(), triggerPayload.workspaceId(),
                    "Dispatch failed: " + e.getMessage(), payloadJson);
            failed.markFailed("Dispatch failed: " + e.getMessage());
            deliveryRepository.save(failed);
        }
    }

    static String buildDedupKey(EmailNotificationTriggerPayload trigger, EmailRule rule, String recipientEmail) {
        String occurrence = extractOccurrenceKey(trigger.payload(), trigger.actorUserId());
        return trigger.eventDefinitionId() + ":" + occurrence + ":" + rule.id() + ":" + recipientEmail;
    }

    private static String extractOccurrenceKey(Map<String, Object> payload, UUID actorUserId) {
        if (payload != null) {
            for (String key : List.of("occurrenceId", "eventOccurrenceId", "aggregateId")) {
                Object value = payload.get(key);
                if (value != null && !String.valueOf(value).isBlank()) {
                    return String.valueOf(value);
                }
            }
        }
        if (actorUserId != null) {
            return actorUserId.toString();
        }
        return "na";
    }

    private Set<String> loadSensitivePaths(UUID eventDefinitionId) {
        List<EventVariable> variables = eventDefinitionRepository.findVariablesByEventDefinitionId(eventDefinitionId);
        return variables.stream()
                .filter(EventVariable::sensitive)
                .map(EventVariable::variablePath)
                .collect(Collectors.toSet());
    }

    private EmailProviderType resolveProviderType() {
        try {
            return EmailProviderType.valueOf(notificationProperties.getProvider().toUpperCase());
        } catch (IllegalArgumentException e) {
            return EmailProviderType.LOG_ONLY;
        }
    }

    private String serializePayload(Map<String, Object> payload) {
        if (payload == null) return null;
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
