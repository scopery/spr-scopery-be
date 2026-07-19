package com.company.scopery.modules.workspace.shared.listeners;

import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport;
import com.company.scopery.modules.eventregistry.shared.seed.EventDefinitionSeedSupport.VariableSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Seeds Phase 03 workspace/org event definitions idempotently (sourceSystem = SCOPERY_WORKSPACE).
 *
 * Uses INSERT ... ON CONFLICT ... DO UPDATE ... RETURNING id so the UUID is returned
 * from the INSERT statement itself — no separate SELECT needed, which avoids the
 * mysterious "SELECT returns 0 rows right after INSERT" failure seen with JPA queries
 * and plain-JDBC post-insert SELECTs.
 */
@Component
@Order(11)
public class WorkspaceEventDefinitionSeedInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceEventDefinitionSeedInitializer.class);
    public static final String SOURCE_SYSTEM = "SCOPERY_WORKSPACE";

    private static final String UPSERT_RETURNING_SQL =
            "INSERT INTO app_event_definition " +
            "(id, code, name, source_system, event_key, description, status, event_version, " +
            " is_system_event, owner_module, created_at, updated_at, created_by, updated_by) " +
            "VALUES (gen_random_uuid(), ?, ?, ?, ?, ?, 'ACTIVE', 1, true, 'WORKSPACE', " +
            "        now(), now(), 'SYSTEM', 'SYSTEM') " +
            "ON CONFLICT ON CONSTRAINT uq_app_event_definition_source_system_event_key " +
            "DO UPDATE SET updated_at = now() " +
            "RETURNING id";

    private static final String SELECT_ID_BY_CODE =
            "SELECT id FROM app_event_definition WHERE code = ?";

    private final EventDefinitionRepository eventDefinitionRepository;
    private final JdbcTemplate jdbc;

    public WorkspaceEventDefinitionSeedInitializer(EventDefinitionRepository eventDefinitionRepository,
                                                    JdbcTemplate jdbc) {
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.jdbc = jdbc;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        for (String code : EVENT_CODES) {
            upsertAndGetId(code);
        }
        seedOrgInvitationVariables();
        seedJoinRequestVariables();
        log.info("[WorkspaceEventDefinitionSeed] Workspace event seeding complete ({} codes)", EVENT_CODES.size());
    }

    /**
     * Upserts the event definition and returns its UUID in one atomic statement.
     * Uses RETURNING id so the UUID comes directly from the INSERT result set.
     * Falls back to a code-based SELECT only when the code uniqueness constraint fires
     * (i.e., the same code already exists under a different source_system).
     */
    private UUID upsertAndGetId(String code) {
        try {
            return jdbc.queryForObject(UPSERT_RETURNING_SQL, UUID.class,
                    code, humanize(code), SOURCE_SYSTEM, code, "Workspace/org event: " + code);
        } catch (DuplicateKeyException e) {
            log.warn("[WorkspaceEventDefinitionSeed] code={} code-constraint conflict, using code-matched record", code);
            return jdbc.queryForObject(SELECT_ID_BY_CODE, UUID.class, code);
        }
    }

    private void seedOrgInvitationVariables() {
        UUID id = upsertAndGetId("ORG_INVITATION_CREATED");
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, id, List.of(
                VariableSpec.of("invitee.email",        "Invitee Email",      VariableType.EMAIL,    true),
                VariableSpec.of("invitee.name",         "Invitee Name",       VariableType.STRING,   false),
                VariableSpec.of("organization.id",      "Organization ID",    VariableType.UUID,     true),
                VariableSpec.of("organization.name",    "Organization Name",  VariableType.STRING,   true),
                VariableSpec.of("invitation.url",       "Invitation URL",     VariableType.URL,      true),
                VariableSpec.of("invitation.expiresAt", "Invitation Expiry",  VariableType.DATETIME, true),
                VariableSpec.of("inviter.name",         "Inviter Name",       VariableType.STRING,   false),
                VariableSpec.of("support.email",        "Support Email",      VariableType.EMAIL,    false)
        ));
    }

    private void seedJoinRequestVariables() {
        UUID createdId = upsertAndGetId("WORKSPACE_JOIN_REQUEST_CREATED");
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, createdId, List.of(
                VariableSpec.of("requester.email", "Requester Email",    VariableType.EMAIL,  true),
                VariableSpec.of("requester.name",  "Requester Name",     VariableType.STRING, false),
                VariableSpec.of("workspace.id",    "Workspace ID",       VariableType.UUID,   true),
                VariableSpec.of("workspace.name",  "Workspace Name",     VariableType.STRING, true),
                VariableSpec.of("admin.email",     "Admin Notify Email", VariableType.EMAIL,  false)
        ));

        UUID rejectedId = upsertAndGetId("WORKSPACE_JOIN_REQUEST_REJECTED");
        EventDefinitionSeedSupport.ensureVariables(eventDefinitionRepository, log, rejectedId, List.of(
                VariableSpec.of("requester.email", "Requester Email", VariableType.EMAIL,  true),
                VariableSpec.of("requester.name",  "Requester Name",  VariableType.STRING, false),
                VariableSpec.of("workspace.id",    "Workspace ID",    VariableType.UUID,   true),
                VariableSpec.of("workspace.name",  "Workspace Name",  VariableType.STRING, true),
                VariableSpec.of("rejector.name",   "Rejector Name",   VariableType.STRING, false)
        ));
    }

    private static String humanize(String code) {
        return code.replace('_', ' ');
    }

    private static final List<String> EVENT_CODES = List.of(
            "ORGANIZATION_CREATED", "ORGANIZATION_UPDATED", "ORGANIZATION_ACTIVATED", "ORGANIZATION_ARCHIVED",
            "ORGANIZATION_OWNER_ADDED", "ORGANIZATION_OWNER_REMOVED",
            "ORGANIZATION_MEMBER_ADDED", "ORGANIZATION_MEMBER_ACTIVATED",
            "ORGANIZATION_MEMBER_SUSPENDED", "ORGANIZATION_MEMBER_REMOVED",
            "ORG_INVITATION_CREATED", "ORG_INVITATION_ACCEPTED", "ORG_INVITATION_CANCELLED",
            "ORG_TEAM_CREATED", "ORG_TEAM_UPDATED", "ORG_TEAM_ARCHIVED",
            "ORG_TEAM_MEMBER_ADDED", "ORG_TEAM_MEMBER_REMOVED",
            "ORG_TEAM_ASSIGNED_TO_WORKSPACE", "ORG_TEAM_REVOKED_FROM_WORKSPACE",
            "WORKSPACE_CREATED", "WORKSPACE_UPDATED", "WORKSPACE_ACTIVATED", "WORKSPACE_ARCHIVED",
            "WORKSPACE_MEMBER_ADDED", "WORKSPACE_MEMBER_ACTIVATED", "WORKSPACE_MEMBER_DEACTIVATED",
            "WORKSPACE_INVITATION_CREATED", "WORKSPACE_INVITATION_ACCEPTED", "WORKSPACE_INVITATION_REVOKED",
            "WORKSPACE_JOIN_REQUEST_CREATED", "WORKSPACE_JOIN_REQUEST_APPROVED",
            "WORKSPACE_JOIN_REQUEST_REJECTED", "WORKSPACE_JOIN_REQUEST_CANCELLED",
            "WORKSPACE_CONTEXT_SWITCHED",
            "WORKSPACE_ONBOARDING_STARTED", "WORKSPACE_ONBOARDING_COMPLETED", "WORKSPACE_ONBOARDING_FAILED"
    );
}
