package com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.seed;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
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
        var def = findOrCreate("USER_SIGNED_UP", "User Signed Up", "SCOPERY", "USER_SIGNED_UP",
                "Fired when a new user completes registration");
        upsertVariables(def.id(), List.of(
                entry("user.id", "User ID", VariableType.UUID, true),
                entry("user.email", "User Email", VariableType.EMAIL, true),
                entry("user.fullName", "User Full Name", VariableType.STRING, true),
                entry("user.createdAt", "Account Created At", VariableType.DATETIME, false)
        ));
    }

    private void seedWorkspaceInvitationCreated() {
        var def = findOrCreate("WORKSPACE_INVITATION_CREATED", "Workspace Invitation Created",
                "SCOPERY", "WORKSPACE_INVITATION_CREATED",
                "Fired when a workspace invitation is created");
        upsertVariables(def.id(), List.of(
                entry("invitee.email", "Invitee Email", VariableType.EMAIL, true),
                entry("invitee.name", "Invitee Name", VariableType.STRING, false),
                entry("workspace.id", "Workspace ID", VariableType.UUID, true),
                entry("workspace.name", "Workspace Name", VariableType.STRING, true),
                entry("inviter.name", "Inviter Name", VariableType.STRING, false),
                entry("invitation.expiresAt", "Invitation Expiry", VariableType.DATETIME, false),
                entry("invitation.link", "Invitation Link", VariableType.URL, false)
        ));
    }

    private void seedWorkspaceInvitationAccepted() {
        var def = findOrCreate("WORKSPACE_INVITATION_ACCEPTED", "Workspace Invitation Accepted",
                "SCOPERY", "WORKSPACE_INVITATION_ACCEPTED",
                "Fired when a user accepts a workspace invitation");
        upsertVariables(def.id(), List.of(
                entry("acceptor.email", "Acceptor Email", VariableType.EMAIL, true),
                entry("acceptor.name", "Acceptor Name", VariableType.STRING, false),
                entry("workspace.id", "Workspace ID", VariableType.UUID, true),
                entry("workspace.name", "Workspace Name", VariableType.STRING, true)
        ));
    }

    private void seedWorkspaceJoinRequestCreated() {
        var def = findOrCreate("WORKSPACE_JOIN_REQUEST_CREATED", "Workspace Join Request Created",
                "SCOPERY", "WORKSPACE_JOIN_REQUEST_CREATED",
                "Fired when a user submits a join request for a workspace");
        upsertVariables(def.id(), List.of(
                entry("requester.name", "Requester Name", VariableType.STRING, false),
                entry("requester.email", "Requester Email", VariableType.EMAIL, true),
                entry("workspace.id", "Workspace ID", VariableType.UUID, true),
                entry("workspace.name", "Workspace Name", VariableType.STRING, true)
        ));
    }

    private void seedWorkspaceJoinRequestApproved() {
        var def = findOrCreate("WORKSPACE_JOIN_REQUEST_APPROVED", "Workspace Join Request Approved",
                "SCOPERY", "WORKSPACE_JOIN_REQUEST_APPROVED",
                "Fired when a workspace join request is approved");
        upsertVariables(def.id(), List.of(
                entry("requester.email", "Requester Email", VariableType.EMAIL, true),
                entry("requester.name", "Requester Name", VariableType.STRING, false),
                entry("workspace.id", "Workspace ID", VariableType.UUID, true),
                entry("workspace.name", "Workspace Name", VariableType.STRING, true),
                entry("approver.name", "Approver Name", VariableType.STRING, false)
        ));
    }

    private EventDefinition findOrCreate(String code, String name, String sourceSystem,
                                          String eventKey, String description) {
        EventDefinitionCode defCode = EventDefinitionCode.of(code);
        return eventDefinitionRepository.findByCode(defCode).orElseGet(() -> {
            EventDefinition def = EventDefinition.create(
                    defCode, name,
                    SourceSystemCode.of(sourceSystem),
                    EventKey.of(eventKey),
                    description, null, null);
            return eventDefinitionRepository.save(def);
        });
    }

    private void upsertVariables(java.util.UUID eventDefinitionId, List<VariableEntry> entries) {
        eventDefinitionRepository.deleteVariablesByEventDefinitionId(eventDefinitionId);
        for (VariableEntry e : entries) {
            eventDefinitionRepository.saveVariable(
                    EventVariable.create(eventDefinitionId, e.path, e.label, e.type, e.required, null, null));
        }
    }

    private VariableEntry entry(String path, String label, VariableType type, boolean required) {
        return new VariableEntry(path, label, type, required);
    }

    private record VariableEntry(String path, String label, VariableType type, boolean required) {}
}
