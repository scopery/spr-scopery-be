package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.user.application.command.RequestPasswordResetCommand;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import com.company.scopery.platform.security.PasswordResetTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RequestPasswordResetAction {

    private static final Logger log = LoggerFactory.getLogger(RequestPasswordResetAction.class);
    private static final String EVENT_CODE = "IAM_PASSWORD_RESET_REQUESTED";
    private static final long TOKEN_TTL_MINUTES = 30;

    private final IamUserRepository userRepository;
    private final PasswordResetTokenService resetTokenService;
    private final IamActivityLogger activityLogger;
    private final TransactionalOutboxService outboxService;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final NotificationProperties notificationProperties;

    public RequestPasswordResetAction(IamUserRepository userRepository,
                                      PasswordResetTokenService resetTokenService,
                                      IamActivityLogger activityLogger,
                                      TransactionalOutboxService outboxService,
                                      EmailNotificationTriggerPublisher notificationPublisher,
                                      EventDefinitionRepository eventDefinitionRepository,
                                      NotificationProperties notificationProperties) {
        this.userRepository = userRepository;
        this.resetTokenService = resetTokenService;
        this.activityLogger = activityLogger;
        this.outboxService = outboxService;
        this.notificationPublisher = notificationPublisher;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.notificationProperties = notificationProperties;
    }

    @Transactional
    public void execute(RequestPasswordResetCommand command) {
        try {
            userRepository.findByEmail(EmailAddress.of(command.email())).ifPresent(this::issueReset);
        } catch (IllegalArgumentException ignored) {
            // Invalid email format — still return generic success to caller.
        }
    }

    private void issueReset(IamUser user) {
        String rawToken = resetTokenService.create(user.id());
        Instant expiresAt = Instant.now().plus(TOKEN_TTL_MINUTES, ChronoUnit.MINUTES);
        String resetUrl = notificationProperties.getFrontendBaseUrl().replaceAll("/$", "")
                + "/reset-password?token=" + rawToken;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", user.id());
        payload.put("occurredAt", Instant.now().toString());
        payload.put("traceId", MDC.get("traceId"));
        payload.put("user", Map.of(
                "id", user.id().toString(),
                "fullName", user.fullName() == null ? "" : user.fullName()));
        payload.put("targetUser", Map.of("email", user.email().value()));
        payload.put("reset", Map.of(
                "url", resetUrl,
                "expiresAt", expiresAt.toString()));
        payload.put("support", Map.of("email", notificationProperties.getFromAddress()));

        outboxService.enqueue("IAM_USER", user.id(), EVENT_CODE, safeOutboxPayload(user.id(), expiresAt));

        UUID eventDefId = eventDefinitionRepository.findByCode(EventDefinitionCode.of(EVENT_CODE))
                .map(e -> e.id())
                .orElse(null);
        if (eventDefId != null) {
            notificationPublisher.publish(new EmailNotificationTriggerPayload(
                    eventDefId, "SCOPERY_IAM", EVENT_CODE, null, user.id(), payload));
        } else {
            log.warn("Event definition {} not found; password-reset email not queued", EVENT_CODE);
        }

        log.info("Password reset requested for userId={}", user.id());
        activityLogger.logSuccess(IamEntityTypes.IAM_USER, user.id(),
                IamActivityActions.REQUEST_IAM_USER_PASSWORD_RESET,
                "Password reset requested for user: " + user.id());
    }

    /** Outbox payload must never contain raw token or reset URL. */
    private static Map<String, Object> safeOutboxPayload(UUID userId, Instant expiresAt) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("userId", userId);
        payload.put("resetExpiresAt", expiresAt.toString());
        payload.put("occurredAt", Instant.now().toString());
        payload.put("traceId", MDC.get("traceId"));
        return payload;
    }
}
