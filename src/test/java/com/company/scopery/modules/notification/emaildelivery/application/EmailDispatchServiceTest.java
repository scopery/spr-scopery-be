package com.company.scopery.modules.notification.emaildelivery.application;

import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventVariable;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import com.company.scopery.modules.notification.emaildelivery.application.service.EmailDispatchService;
import com.company.scopery.modules.notification.emaildelivery.domain.model.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.model.EmailDeliveryRepository;
import com.company.scopery.modules.notification.emaildelivery.domain.enums.EmailDeliveryStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutbox;
import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailrule.application.service.EmailRecipientResolver;
import com.company.scopery.modules.notification.emailrule.application.service.EmailRuleMatcher;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRecipientStrategy;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleScope;
import com.company.scopery.modules.notification.emailrule.domain.enums.EmailRuleStatus;
import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;
import com.company.scopery.modules.notification.emailtemplate.application.service.EmailTemplateRenderer;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateScope;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateStatus;
import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateVersionStatus;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateRepository;
import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateVersion;
import com.company.scopery.modules.notification.emailtemplate.domain.valueobject.EmailTemplateCode;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.notificationitem.application.service.NotificationItemCreator;
import com.company.scopery.modules.notification.shared.NotificationActivityLogger;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import com.company.scopery.modules.notification.shared.privacy.SensitivePayloadMasker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailDispatchServiceTest {

    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private EmailRuleMatcher ruleMatcher;
    @Mock private EmailRecipientResolver recipientResolver;
    @Mock private EmailTemplateRepository templateRepository;
    @Mock private EmailTemplateRenderer templateRenderer;
    @Mock private EmailDeliveryRepository deliveryRepository;
    @Mock private EmailOutboxRepository outboxRepository;
    @Mock private NotificationItemCreator notificationItemCreator;
    @Mock private NotificationActivityLogger activityLogger;
    @Mock private ImmutableAuditEventService auditEventService;

    private NotificationProperties properties;
    private EmailDispatchService service;

    private final UUID eventDefId = UUID.randomUUID();
    private final UUID templateId = UUID.randomUUID();
    private final UUID versionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        properties = new NotificationProperties();
        properties.setEnabled(true);
        service = new EmailDispatchService(eventDefinitionRepository, ruleMatcher, recipientResolver,
                templateRepository, templateRenderer, deliveryRepository, outboxRepository,
                notificationItemCreator, new SensitivePayloadMasker(), properties,
                activityLogger, auditEventService, new ObjectMapper());
    }

    @Test
    void dispatch_disabled_doesNothing() {
        properties.setEnabled(false);
        service.dispatch(makePayload(eventDefId));
        verifyNoInteractions(eventDefinitionRepository);
    }

    @Test
    void dispatch_createsDeliveryAndOutbox_whenRecipientResolved() {
        mockActiveEventDef();
        EmailRule rule = makeRule(templateId, EmailRecipientStrategy.INVITEE_EMAIL, false);
        mockActiveTemplate(templateId, versionId);
        mockVersion(versionId);
        when(eventDefinitionRepository.findVariablesByEventDefinitionId(eventDefId)).thenReturn(List.of());

        when(ruleMatcher.matchRules(any(), any())).thenReturn(List.of(rule));
        when(recipientResolver.resolveAll(any(), any()))
                .thenReturn(List.of(EmailRecipientResolver.RecipientResult.resolved("to@example.com")));
        when(templateRenderer.render(any(), any())).thenReturn("rendered");
        when(deliveryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(outboxRepository.existsByDedupKey(any())).thenReturn(false);
        when(outboxRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.dispatch(makePayload(eventDefId));

        ArgumentCaptor<EmailDelivery> deliveryCaptor = ArgumentCaptor.forClass(EmailDelivery.class);
        verify(deliveryRepository).save(deliveryCaptor.capture());
        assertThat(deliveryCaptor.getValue().toEmail()).isEqualTo("to@example.com");
        assertThat(deliveryCaptor.getValue().status()).isEqualTo(EmailDeliveryStatus.CREATED);
        verify(outboxRepository).save(any(EmailOutbox.class));
    }

    @Test
    void dispatch_dedupSkip_doesNotCreateOutbox() {
        mockActiveEventDef();
        EmailRule rule = makeRule(templateId, EmailRecipientStrategy.INVITEE_EMAIL, false);
        mockActiveTemplate(templateId, versionId);
        mockVersion(versionId);
        when(eventDefinitionRepository.findVariablesByEventDefinitionId(eventDefId)).thenReturn(List.of());
        when(ruleMatcher.matchRules(any(), any())).thenReturn(List.of(rule));
        when(recipientResolver.resolveAll(any(), any()))
                .thenReturn(List.of(EmailRecipientResolver.RecipientResult.resolved("to@example.com")));
        when(outboxRepository.existsByDedupKey(any())).thenReturn(true);

        service.dispatch(makePayload(eventDefId));

        verify(outboxRepository, never()).save(any());
        verify(deliveryRepository, never()).save(any());
        verify(activityLogger).logSuccess(any(), any(), eq("NOTIFICATION_DEDUPLICATED"), any());
    }

    @Test
    void dispatch_masksSensitivePayload_whenRuleDisallows() {
        mockActiveEventDef();
        EmailRule rule = makeRule(templateId, EmailRecipientStrategy.INVITEE_EMAIL, false);
        mockActiveTemplate(templateId, versionId);
        mockVersion(versionId);

        EventVariable sensitive = EventVariable.create(eventDefId, "invitee.token", "Token",
                com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType.STRING,
                false, true, null, null);
        when(eventDefinitionRepository.findVariablesByEventDefinitionId(eventDefId)).thenReturn(List.of(sensitive));
        when(ruleMatcher.matchRules(any(), any())).thenReturn(List.of(rule));
        when(recipientResolver.resolveAll(any(), any()))
                .thenReturn(List.of(EmailRecipientResolver.RecipientResult.resolved("to@example.com")));
        when(outboxRepository.existsByDedupKey(any())).thenReturn(false);
        when(deliveryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(outboxRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(templateRenderer.render(any(), any())).thenReturn("rendered");

        Map<String, Object> payload = Map.of(
                "invitee", Map.of("email", "to@example.com", "token", "secret-token"),
                "aggregateId", "agg-1");
        service.dispatch(new EmailNotificationTriggerPayload(eventDefId, "TEST", "test.event",
                null, null, payload));

        ArgumentCaptor<Map<String, Object>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
        verify(templateRenderer, atLeastOnce()).render(any(), payloadCaptor.capture());
        Map<String, Object> used = payloadCaptor.getValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> invitee = (Map<String, Object>) used.get("invitee");
        assertThat(invitee.get("token")).isEqualTo("***");
    }

    @Test
    void dispatch_recipientSkipped_createsSkippedDelivery_noOutbox() {
        mockActiveEventDef();
        EmailRule rule = makeRule(templateId, EmailRecipientStrategy.WORKSPACE_USERS_WITH_RIGHT, false);
        mockActiveTemplate(templateId, versionId);
        mockVersion(versionId);
        when(eventDefinitionRepository.findVariablesByEventDefinitionId(eventDefId)).thenReturn(List.of());

        when(ruleMatcher.matchRules(any(), any())).thenReturn(List.of(rule));
        when(recipientResolver.resolveAll(any(), any()))
                .thenReturn(List.of(EmailRecipientResolver.RecipientResult.skipped("deferred unsupported")));
        when(deliveryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.dispatch(makePayload(eventDefId));

        ArgumentCaptor<EmailDelivery> deliveryCaptor = ArgumentCaptor.forClass(EmailDelivery.class);
        verify(deliveryRepository).save(deliveryCaptor.capture());
        assertThat(deliveryCaptor.getValue().status()).isEqualTo(EmailDeliveryStatus.SKIPPED);
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void dispatch_noMatchingRules_doesNotSave() {
        mockActiveEventDef();
        when(ruleMatcher.matchRules(any(), any())).thenReturn(List.of());
        service.dispatch(makePayload(eventDefId));
        verifyNoInteractions(deliveryRepository);
        verify(outboxRepository, never()).save(any());
    }

    private EventDefinition mockActiveEventDef() {
        EventDefinition def = EventDefinition.reconstitute(
                eventDefId, EventDefinitionCode.of("TEST_EVENT"), "Test Event",
                SourceSystemCode.of("TEST"), EventKey.of("TEST_EVENT"),
                null, null, null, EventDefinitionStatus.ACTIVE, 1, null,
                Instant.now(), Instant.now());
        when(eventDefinitionRepository.findById(eventDefId)).thenReturn(Optional.of(def));
        return def;
    }

    private EmailTemplate mockActiveTemplate(UUID tId, UUID vId) {
        EmailTemplate template = EmailTemplate.reconstitute(
                tId, EmailTemplateCode.of("TEST_TMPL"), "Test", null,
                EmailTemplateScope.SYSTEM, null, eventDefId,
                EmailTemplateStatus.ACTIVE, vId, Instant.now(), Instant.now(), null);
        when(templateRepository.findById(tId)).thenReturn(Optional.of(template));
        return template;
    }

    private EmailTemplateVersion mockVersion(UUID vId) {
        EmailTemplateVersion version = EmailTemplateVersion.reconstitute(
                vId, templateId, 1, "Subject", "<p>Body</p>", null,
                EmailTemplateVersionStatus.PUBLISHED, Instant.now(), null,
                Instant.now(), Instant.now());
        when(templateRepository.findVersionById(vId)).thenReturn(Optional.of(version));
        return version;
    }

    private EmailRule makeRule(UUID tId, EmailRecipientStrategy strategy, boolean allowSensitive) {
        return EmailRule.reconstitute(
                UUID.randomUUID(), "RULE_TEST", "Rule Test", null,
                EmailRuleScope.SYSTEM, null, eventDefId, tId,
                strategy, null, 10, true, false, allowSensitive,
                EmailRuleStatus.ACTIVE, Instant.now(), Instant.now(), null);
    }

    private EmailNotificationTriggerPayload makePayload(UUID eventDefId) {
        return new EmailNotificationTriggerPayload(eventDefId, "TEST", "test.event",
                null, null, Map.of(
                        "invitee", Map.of("email", "to@example.com"),
                        "aggregateId", "agg-1"));
    }
}
