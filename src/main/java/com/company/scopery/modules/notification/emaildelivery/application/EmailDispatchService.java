package com.company.scopery.modules.notification.emaildelivery.application;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionStatus;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailMessage;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailProviderType;
import com.company.scopery.modules.notification.emailrule.application.EmailRecipientResolver;
import com.company.scopery.modules.notification.emailrule.application.EmailRuleMatcher;
import com.company.scopery.modules.notification.emailrule.domain.EmailRule;
import com.company.scopery.modules.notification.emailtemplate.application.EmailTemplateRenderer;
import com.company.scopery.modules.notification.emailtemplate.domain.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.EmailTemplateStatus;
import com.company.scopery.modules.notification.emailtemplate.domain.EmailTemplateVersion;
import com.company.scopery.modules.notification.emailtrigger.domain.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import com.company.scopery.modules.notification.shared.error.NotificationExceptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    private final NotificationProperties notificationProperties;
    private final ObjectMapper objectMapper;

    public EmailDispatchService(EventDefinitionRepository eventDefinitionRepository,
                                 EmailRuleMatcher ruleMatcher,
                                 EmailRecipientResolver recipientResolver,
                                 EmailTemplateRepository templateRepository,
                                 EmailTemplateRenderer templateRenderer,
                                 EmailDeliveryRepository deliveryRepository,
                                 EmailOutboxRepository outboxRepository,
                                 NotificationProperties notificationProperties,
                                 ObjectMapper objectMapper) {
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.ruleMatcher = ruleMatcher;
        this.recipientResolver = recipientResolver;
        this.templateRepository = templateRepository;
        this.templateRenderer = templateRenderer;
        this.deliveryRepository = deliveryRepository;
        this.outboxRepository = outboxRepository;
        this.notificationProperties = notificationProperties;
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

        String payloadJson = serializePayload(triggerPayload.payload());
        EmailProviderType providerType = resolveProviderType();

        for (EmailRule rule : rules) {
            dispatchForRule(rule, triggerPayload, payloadJson, providerType);
        }
    }

    private void dispatchForRule(EmailRule rule, EmailNotificationTriggerPayload triggerPayload,
                                  String payloadJson, EmailProviderType providerType) {
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

        EmailRecipientResolver.RecipientResult recipient =
                recipientResolver.resolve(rule, triggerPayload.payload());

        if (recipient.skipped()) {
            EmailDelivery skipped = EmailDelivery.createSkipped(
                    rule.id(), template.id(), version.id(),
                    triggerPayload.eventDefinitionId(), triggerPayload.workspaceId(),
                    recipient.skipReason(), payloadJson);
            deliveryRepository.save(skipped);
            log.info("[EmailDispatch] Delivery SKIPPED for rule {}: {}", rule.code(), recipient.skipReason());
            return;
        }

        try {
            Map<String, Object> payload = triggerPayload.payload();
            String renderedSubject = templateRenderer.render(version.subjectTemplate(), payload);
            String renderedHtml = templateRenderer.render(version.htmlBodyTemplate(), payload);
            String renderedText = templateRenderer.render(version.textBodyTemplate(), payload);

            EmailDelivery delivery = EmailDelivery.create(
                    rule.id(), template.id(), version.id(),
                    triggerPayload.eventDefinitionId(), triggerPayload.workspaceId(),
                    recipient.email(), renderedSubject, renderedHtml, renderedText, payloadJson);
            delivery = deliveryRepository.save(delivery);

            EmailMessage message = new EmailMessage(
                    recipient.email(), renderedSubject, renderedHtml, renderedText);
            EmailOutbox outbox = EmailOutbox.create(delivery.id(), message, providerType);
            outboxRepository.save(outbox);

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
