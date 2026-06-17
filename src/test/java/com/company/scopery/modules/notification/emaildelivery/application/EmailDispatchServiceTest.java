package com.company.scopery.modules.notification.emaildelivery.application;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.*;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDelivery;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryRepository;
import com.company.scopery.modules.notification.emaildelivery.domain.EmailDeliveryStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.EmailOutboxRepository;
import com.company.scopery.modules.notification.emailrule.application.EmailRecipientResolver;
import com.company.scopery.modules.notification.emailrule.application.EmailRuleMatcher;
import com.company.scopery.modules.notification.emailrule.domain.*;
import com.company.scopery.modules.notification.emailtemplate.application.EmailTemplateRenderer;
import com.company.scopery.modules.notification.emailtemplate.domain.*;
import com.company.scopery.modules.notification.emailtrigger.domain.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.shared.NotificationProperties;
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
                properties, new ObjectMapper());
    }

    @Test
    void dispatch_disabled_doesNothing() {
        properties.setEnabled(false);
        service.dispatch(makePayload(eventDefId));
        verifyNoInteractions(eventDefinitionRepository);
    }

    @Test
    void dispatch_createsDeliveryAndOutbox_whenRecipientResolved() {
        EventDefinition eventDef = mockActiveEventDef();
        EmailRule rule = makeRule(templateId, EmailRecipientStrategy.INVITEE_EMAIL);
        EmailTemplate template = mockActiveTemplate(templateId, versionId);
        EmailTemplateVersion version = mockVersion(versionId);

        when(ruleMatcher.matchRules(any(), any())).thenReturn(List.of(rule));
        when(recipientResolver.resolve(any(), any()))
                .thenReturn(EmailRecipientResolver.RecipientResult.resolved("to@example.com"));
        when(templateRenderer.render(any(), any())).thenReturn("rendered");
        when(deliveryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(outboxRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.dispatch(makePayload(eventDefId));

        ArgumentCaptor<EmailDelivery> deliveryCaptor = ArgumentCaptor.forClass(EmailDelivery.class);
        verify(deliveryRepository).save(deliveryCaptor.capture());
        assertThat(deliveryCaptor.getValue().toEmail()).isEqualTo("to@example.com");
        assertThat(deliveryCaptor.getValue().status()).isEqualTo(EmailDeliveryStatus.CREATED);
        verify(outboxRepository).save(any());
    }

    @Test
    void dispatch_recipientSkipped_createsSkippedDelivery_noOutbox() {
        mockActiveEventDef();
        EmailRule rule = makeRule(templateId, EmailRecipientStrategy.WORKSPACE_USERS_WITH_RIGHT);
        mockActiveTemplate(templateId, versionId);
        mockVersion(versionId);

        when(ruleMatcher.matchRules(any(), any())).thenReturn(List.of(rule));
        when(recipientResolver.resolve(any(), any()))
                .thenReturn(EmailRecipientResolver.RecipientResult.skipped("Phase 1 unsupported"));
        when(deliveryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.dispatch(makePayload(eventDefId));

        ArgumentCaptor<EmailDelivery> deliveryCaptor = ArgumentCaptor.forClass(EmailDelivery.class);
        verify(deliveryRepository).save(deliveryCaptor.capture());
        assertThat(deliveryCaptor.getValue().status()).isEqualTo(EmailDeliveryStatus.SKIPPED);
        verifyNoInteractions(outboxRepository);
    }

    @Test
    void dispatch_noMatchingRules_doesNotSave() {
        mockActiveEventDef();
        when(ruleMatcher.matchRules(any(), any())).thenReturn(List.of());
        service.dispatch(makePayload(eventDefId));
        verifyNoInteractions(deliveryRepository);
        verifyNoInteractions(outboxRepository);
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
                EmailTemplateVersionStatus.PUBLISHED, Instant.now(), Instant.now());
        when(templateRepository.findVersionById(vId)).thenReturn(Optional.of(version));
        return version;
    }

    private EmailRule makeRule(UUID tId, EmailRecipientStrategy strategy) {
        return EmailRule.reconstitute(
                UUID.randomUUID(), "RULE_TEST", "Rule Test", null,
                EmailRuleScope.SYSTEM, null, eventDefId, tId,
                strategy, null, 10, true,
                EmailRuleStatus.ACTIVE, Instant.now(), Instant.now(), null);
    }

    private EmailNotificationTriggerPayload makePayload(UUID eventDefId) {
        return new EmailNotificationTriggerPayload(eventDefId, "TEST", "test.event",
                null, null, Map.of("invitee", Map.of("email", "to@example.com")));
    }
}
