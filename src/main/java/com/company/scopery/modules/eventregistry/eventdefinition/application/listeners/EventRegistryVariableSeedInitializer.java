package com.company.scopery.modules.eventregistry.eventdefinition.application.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport.VariableSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Legacy variable enrichment for SCOPERY-sourced definitions used by email templates.
 * Uses add-missing-only semantics (never deletes variables).
 */
@Component
@Order(20)
public class EventRegistryVariableSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(EventRegistryVariableSeedInitializer.class);

    private final EventDefinitionRepository eventDefinitionRepository;

    public EventRegistryVariableSeedInitializer(EventDefinitionRepository eventDefinitionRepository) {
        this.eventDefinitionRepository = eventDefinitionRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        seedUserSignedUp();
        seedWorkspaceInvitationCreated();
        seedWorkspaceInvitationAccepted();
        seedWorkspaceJoinRequestCreated();
        seedWorkspaceJoinRequestApproved();
        log.info("[EventRegistryVariableSeed] Variable seeding complete");
    }

    private void seedUserSignedUp() {
        var def = findOrCreate("USER_SIGNED_UP", "User Signed Up", "SCOPERY",
                "Fired when a new user completes registration");
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, def.id(), List.of(
                VariableSpec.of("user.id", "User ID", VariableType.UUID, true),
                VariableSpec.sensitive("user.email", "User Email", VariableType.EMAIL, true),
                VariableSpec.of("user.fullName", "User Full Name", VariableType.STRING, true),
                VariableSpec.of("user.createdAt", "Account Created At", VariableType.DATETIME, false)
        ));
    }

    private void seedWorkspaceInvitationCreated() {
        var def = findOrCreate("WORKSPACE_INVITATION_CREATED", "Workspace Invitation Created", "SCOPERY",
                "Fired when a workspace invitation is created");
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, def.id(), List.of(
                VariableSpec.sensitive("invitee.email", "Invitee Email", VariableType.EMAIL, true),
                VariableSpec.of("invitee.name", "Invitee Name", VariableType.STRING, false),
                VariableSpec.of("workspace.id", "Workspace ID", VariableType.UUID, true),
                VariableSpec.of("workspace.name", "Workspace Name", VariableType.STRING, true),
                VariableSpec.of("inviter.name", "Inviter Name", VariableType.STRING, false),
                VariableSpec.of("invitation.expiresAt", "Invitation Expiry", VariableType.DATETIME, false),
                VariableSpec.sensitive("invitation.link", "Invitation Link", VariableType.URL, false)
        ));
    }

    private void seedWorkspaceInvitationAccepted() {
        var def = findOrCreate("WORKSPACE_INVITATION_ACCEPTED", "Workspace Invitation Accepted", "SCOPERY",
                "Fired when a user accepts a workspace invitation");
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, def.id(), List.of(
                VariableSpec.sensitive("acceptor.email", "Acceptor Email", VariableType.EMAIL, true),
                VariableSpec.of("acceptor.name", "Acceptor Name", VariableType.STRING, false),
                VariableSpec.of("workspace.id", "Workspace ID", VariableType.UUID, true),
                VariableSpec.of("workspace.name", "Workspace Name", VariableType.STRING, true)
        ));
    }

    private void seedWorkspaceJoinRequestCreated() {
        var def = findOrCreate("WORKSPACE_JOIN_REQUEST_CREATED", "Workspace Join Request Created", "SCOPERY",
                "Fired when a user submits a join request for a workspace");
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, def.id(), List.of(
                VariableSpec.of("requester.name", "Requester Name", VariableType.STRING, false),
                VariableSpec.sensitive("requester.email", "Requester Email", VariableType.EMAIL, true),
                VariableSpec.of("workspace.id", "Workspace ID", VariableType.UUID, true),
                VariableSpec.of("workspace.name", "Workspace Name", VariableType.STRING, true)
        ));
    }

    private void seedWorkspaceJoinRequestApproved() {
        var def = findOrCreate("WORKSPACE_JOIN_REQUEST_APPROVED", "Workspace Join Request Approved", "SCOPERY",
                "Fired when a workspace join request is approved");
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, def.id(), List.of(
                VariableSpec.sensitive("requester.email", "Requester Email", VariableType.EMAIL, true),
                VariableSpec.of("requester.name", "Requester Name", VariableType.STRING, false),
                VariableSpec.of("workspace.id", "Workspace ID", VariableType.UUID, true),
                VariableSpec.of("workspace.name", "Workspace Name", VariableType.STRING, true),
                VariableSpec.of("approver.name", "Approver Name", VariableType.STRING, false)
        ));
    }

    private EventDefinition findOrCreate(String code, String name, String sourceSystem, String description) {
        return EventDefinitionSeedSupport.findOrCreate(
                eventDefinitionRepository, sourceSystem, code, name, description, null, "EVENT_REGISTRY");
    }
}
