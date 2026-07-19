package com.company.scopery.modules.iam.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds Phase 02 IAM/Identity event definitions idempotently (sourceSystem = SCOPERY_IAM).
 */
@Component
@Order(10)
public class IamEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(IamEventDefinitionSeedInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_IAM";

    private final EventDefinitionRepository eventDefinitionRepository;

    public IamEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        seedIdentityEvents();
        seedPolicyEvents();
        seedPasswordResetRequestVariables();
        log.info("[IamEventDefinitionSeed] IAM event seeding complete");
    }

    private void seedIdentityEvents() {
        for (SeedEvent e : IDENTITY_EVENTS) {
            findOrCreate(e.code(), e.name(), e.description());
        }
    }

    private void seedPolicyEvents() {
        for (SeedEvent e : POLICY_EVENTS) {
            findOrCreate(e.code(), e.name(), e.description());
        }
    }

    private void seedPasswordResetRequestVariables() {
        EventDefinition def = findOrCreate(
                "IAM_PASSWORD_RESET_REQUESTED",
                "IAM Password Reset Requested",
                "Fired when a user requests a password reset");
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, def.id(), List.of(
                EventDefinitionSeedSupport.VariableSpec.of("user.id", "User ID", VariableType.UUID, true),
                EventDefinitionSeedSupport.VariableSpec.of("user.fullName", "User Full Name", VariableType.STRING, true),
                EventDefinitionSeedSupport.VariableSpec.sensitive("targetUser.email", "Target User Email", VariableType.EMAIL, true),
                EventDefinitionSeedSupport.VariableSpec.sensitive("reset.url", "Password Reset URL", VariableType.URL, true),
                EventDefinitionSeedSupport.VariableSpec.of("reset.expiresAt", "Reset Token Expiry", VariableType.DATETIME, true),
                EventDefinitionSeedSupport.VariableSpec.of("support.email", "Support Email", VariableType.EMAIL, false),
                EventDefinitionSeedSupport.VariableSpec.of("occurredAt", "Occurred At", VariableType.DATETIME, false),
                EventDefinitionSeedSupport.VariableSpec.of("traceId", "Trace ID", VariableType.STRING, false)
        ));
    }

    private EventDefinition findOrCreate(String code, String name, String description) {
        return EventDefinitionSeedSupport.findOrCreate(
                eventDefinitionRepository,
                SOURCE_SYSTEM,
                code,
                name,
                description,
                null,
                "IAM");
    }

    private static final List<SeedEvent> IDENTITY_EVENTS = List.of(
            new SeedEvent("IAM_USER_CREATED", "IAM User Created", "User account created"),
            new SeedEvent("IAM_USER_UPDATED", "IAM User Updated", "User profile updated"),
            new SeedEvent("IAM_USER_ACTIVATED", "IAM User Activated", "User activated"),
            new SeedEvent("IAM_USER_DEACTIVATED", "IAM User Deactivated", "User deactivated"),
            new SeedEvent("IAM_USER_SUSPENDED", "IAM User Suspended", "User suspended"),
            new SeedEvent("IAM_USER_LOGGED_IN", "IAM User Logged In", "Successful login"),
            new SeedEvent("IAM_LOGIN_FAILED", "IAM Login Failed", "Failed login attempt"),
            new SeedEvent("IAM_USER_LOGGED_OUT", "IAM User Logged Out", "User logged out"),
            new SeedEvent("IAM_REFRESH_TOKEN_ROTATED", "IAM Refresh Token Rotated", "Refresh token rotated"),
            new SeedEvent("IAM_SESSIONS_REVOKED", "IAM Sessions Revoked", "All sessions revoked"),
            new SeedEvent("IAM_PASSWORD_CHANGED", "IAM Password Changed", "Password changed by user"),
            new SeedEvent("IAM_PASSWORD_RESET_REQUESTED", "IAM Password Reset Requested", "Password reset requested"),
            new SeedEvent("IAM_PASSWORD_RESET_COMPLETED", "IAM Password Reset Completed", "Password reset completed")
    );

    private static final List<SeedEvent> POLICY_EVENTS = List.of(
            new SeedEvent("IAM_ROLE_CREATED", "IAM Role Created", "Role created"),
            new SeedEvent("IAM_ROLE_UPDATED", "IAM Role Updated", "Role updated"),
            new SeedEvent("IAM_ROLE_DELETED", "IAM Role Deleted", "Role deleted"),
            new SeedEvent("IAM_ROLE_ASSIGNED", "IAM Role Assigned", "Role assigned"),
            new SeedEvent("IAM_ROLE_ASSIGNMENT_DEACTIVATED", "IAM Role Assignment Deactivated", "Role assignment deactivated"),
            new SeedEvent("IAM_RESOURCE_REGISTERED", "IAM Resource Registered", "Auth resource registered"),
            new SeedEvent("IAM_RESOURCE_DEACTIVATED", "IAM Resource Deactivated", "Auth resource deactivated"),
            new SeedEvent("IAM_ACCESS_GRANTED", "IAM Access Granted", "Access grant created"),
            new SeedEvent("IAM_ACCESS_REVOKED", "IAM Access Revoked", "Access grant revoked"),
            new SeedEvent("IAM_GRANT_ACTION_ATTACHED", "IAM Grant Action Attached", "Permission action attached to grant"),
            new SeedEvent("IAM_GRANT_ACTION_REMOVED", "IAM Grant Action Removed", "Permission action removed from grant"),
            new SeedEvent("IAM_OWNER_POLICY_CREATED", "IAM Owner Policy Created", "Owner policy created"),
            new SeedEvent("IAM_OWNER_GRANT_BOOTSTRAPPED", "IAM Owner Grant Bootstrapped", "Owner grant bootstrapped"),
            new SeedEvent("IAM_DELEGATION_CREATED", "IAM Delegation Created", "Delegation created"),
            new SeedEvent("IAM_DELEGATION_REJECTED", "IAM Delegation Rejected", "Delegation rejected"),
            new SeedEvent("IAM_AUTHORIZATION_DENIED", "IAM Authorization Denied", "Authorization denied")
    );

    private record SeedEvent(String code, String name, String description) {}
}
