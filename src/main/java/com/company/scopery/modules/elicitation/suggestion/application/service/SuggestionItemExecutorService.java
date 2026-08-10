package com.company.scopery.modules.elicitation.suggestion.application.service;

import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.traceability.businessrule.application.command.CreateBusinessRuleCommand;
import com.company.scopery.modules.traceability.appcomponent.application.action.CreateRegistryAppComponentAction;
import com.company.scopery.modules.traceability.appcomponent.application.command.CreateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.functionalitem.application.action.CreateFunctionalItemAction;
import com.company.scopery.modules.traceability.functionalitem.application.action.UpdateFunctionalItemAction;
import com.company.scopery.modules.traceability.functionalitem.application.command.CreateFunctionalItemCommand;
import com.company.scopery.modules.traceability.functionalitem.application.command.UpdateFunctionalItemCommand;
import com.company.scopery.modules.traceability.functionscreen.application.action.LinkFunctionScreenAction;
import com.company.scopery.modules.traceability.functionscreen.application.command.LinkFunctionScreenCommand;
import com.company.scopery.modules.traceability.requirement.application.action.CreateRequirementAction;
import com.company.scopery.modules.traceability.requirement.application.action.UpdateRequirementAction;
import com.company.scopery.modules.traceability.requirement.application.command.CreateRequirementCommand;
import com.company.scopery.modules.traceability.requirement.application.command.UpdateRequirementCommand;
import com.company.scopery.modules.traceability.screen.application.action.CreateRegistryScreenAction;
import com.company.scopery.modules.traceability.screen.application.action.UpdateRegistryScreenAction;
import com.company.scopery.modules.traceability.screen.application.command.CreateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.command.UpdateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screencomponent.application.action.LinkScreenComponentAction;
import com.company.scopery.modules.traceability.screencomponent.application.command.LinkScreenComponentCommand;
import com.company.scopery.modules.traceability.usecase.application.action.AddSupportingFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.CreateUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.action.LinkRequirementToFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.RemoveSupportingFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.UnlinkRequirementFromFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.UpdateUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.command.AddSupportingFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.CreateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.LinkRequirementToFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.RemoveSupportingFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UnlinkRequirementFromFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseCommand;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SuggestionItemExecutorService {

    private static final Logger log = LoggerFactory.getLogger(SuggestionItemExecutorService.class);

    private final UpdateRequirementAction updateRequirement;
    private final CreateRequirementAction createRequirement;
    private final UpdateFunctionalItemAction updateFunction;
    private final CreateFunctionalItemAction createFunction;
    private final CreateUseCaseAction createUseCase;
    private final UpdateUseCaseAction updateUseCase;
    private final LinkRequirementToFunctionAction linkRequirementFunction;
    private final UnlinkRequirementFromFunctionAction unlinkRequirementFunction;
    private final AddSupportingFunctionAction addSupportingFunction;
    private final RemoveSupportingFunctionAction removeSupportingFunction;
    private final CreateRegistryScreenAction createScreen;
    private final UpdateRegistryScreenAction updateScreen;
    private final LinkFunctionScreenAction linkFunctionScreen;
    private final CreateRegistryAppComponentAction createComponent;
    private final LinkScreenComponentAction linkScreenComponent;
    private final NamedParameterJdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    public SuggestionItemExecutorService(
            UpdateRequirementAction updateRequirement,
            CreateRequirementAction createRequirement,
            UpdateFunctionalItemAction updateFunction,
            CreateFunctionalItemAction createFunction,
            CreateUseCaseAction createUseCase,
            UpdateUseCaseAction updateUseCase,
            LinkRequirementToFunctionAction linkRequirementFunction,
            UnlinkRequirementFromFunctionAction unlinkRequirementFunction,
            AddSupportingFunctionAction addSupportingFunction,
            RemoveSupportingFunctionAction removeSupportingFunction,
            CreateRegistryScreenAction createScreen,
            UpdateRegistryScreenAction updateScreen,
            LinkFunctionScreenAction linkFunctionScreen,
            CreateRegistryAppComponentAction createComponent,
            LinkScreenComponentAction linkScreenComponent,
            NamedParameterJdbcTemplate jdbc,
            ObjectMapper objectMapper) {
        this.updateRequirement = updateRequirement;
        this.createRequirement = createRequirement;
        this.updateFunction = updateFunction;
        this.createFunction = createFunction;
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.linkRequirementFunction = linkRequirementFunction;
        this.unlinkRequirementFunction = unlinkRequirementFunction;
        this.addSupportingFunction = addSupportingFunction;
        this.removeSupportingFunction = removeSupportingFunction;
        this.createScreen = createScreen;
        this.updateScreen = updateScreen;
        this.linkFunctionScreen = linkFunctionScreen;
        this.createComponent = createComponent;
        this.linkScreenComponent = linkScreenComponent;
        this.jdbc = jdbc;
        this.objectMapper = objectMapper;
    }

    public String execute(ElicitationSuggestionItem item, UUID projectId, UUID workspaceId, UUID scopePackageId) {
        JsonNode changes = parseChanges(item.changesJson());
        UUID targetId = item.targetEntityId();

        return switch (item.action()) {
            case "UPDATE_REQUIREMENT" -> executeUpdateRequirement(item, changes, projectId, targetId);
            case "CREATE_REQUIREMENT" -> executeCreateRequirement(changes, projectId, scopePackageId);
            case "UPDATE_FUNCTION"    -> executeUpdateFunction(changes, projectId, targetId);
            case "CREATE_FUNCTION"    -> executeCreateFunction(changes, projectId, workspaceId);
            case "UPDATE_USE_CASE"    -> executeUpdateUseCase(changes, projectId, targetId);
            case "CREATE_USE_CASE"    -> executeCreateUseCase(changes, projectId);
            case "CREATE_SCREEN"      -> executeCreateScreen(changes, projectId, workspaceId);
            case "UPDATE_SCREEN"      -> executeUpdateScreen(changes, workspaceId, targetId);
            case "LINK_REQUIREMENT_FUNCTION"   -> executeLinkRequirementFunction(item, projectId, targetId);
            case "UNLINK_REQUIREMENT_FUNCTION" -> executeUnlinkRequirementFunction(item, projectId, targetId);
            case "LINK_FUNCTION_USE_CASE"      -> executeLinkFunctionUseCase(changes, projectId, targetId);
            case "UNLINK_FUNCTION_USE_CASE"    -> executeUnlinkFunctionUseCase(changes, projectId, targetId);
            case "LINK_FUNCTION_SCREEN"        -> executeLinkFunctionScreen(changes, projectId, targetId);
            case "CREATE_COMPONENT"            -> executeCreateComponent(changes, workspaceId);
            case "LINK_SCREEN_COMPONENT"       -> executeLinkScreenComponent(changes, workspaceId, targetId);
            case "CREATE_NOTIFICATION", "UPDATE_NOTIFICATION", "LINK_FUNCTION_NOTIFICATION" ->
                    throw new UnsupportedOperationException("Notification actions are not yet supported: " + item.action());
            default -> {
                log.warn("Unknown suggestion action type: {}", item.action());
                throw new UnsupportedOperationException("Unknown action type: " + item.action());
            }
        };
    }

    public UUID loadWorkspaceId(UUID projectId) {
        String sql = "SELECT workspace_id FROM project_project WHERE id = :id LIMIT 1";
        List<UUID> ids = jdbc.query(sql, new MapSqlParameterSource("id", projectId),
                (rs, i) -> (UUID) rs.getObject("workspace_id"));
        if (ids.isEmpty()) throw new IllegalArgumentException("Project not found: " + projectId);
        return ids.get(0);
    }

    // ── Requirement ───────────────────────────────────────────────────────────

    private String executeUpdateRequirement(ElicitationSuggestionItem item, JsonNode c, UUID projectId, UUID targetId) {
        if (targetId == null) throw new IllegalArgumentException("targetEntityId required for UPDATE_REQUIREMENT");
        updateRequirement.execute(new UpdateRequirementCommand(
                targetId, projectId,
                text(c, "title"), text(c, "description"), text(c, "priority"),
                text(c, "requirementType"), uuid(c, "applicationId"),
                uuid(c, "functionalItemId"), uuid(c, "nonFunctionalItemId"),
                uuid(c, "scopeItemId"), uuid(c, "scopePackageId"), text(c, "requiresUseCase")
        ));
        return null;
    }

    private String executeCreateRequirement(JsonNode c, UUID projectId, UUID scopePackageId) {
        createRequirement.execute(new CreateRequirementCommand(
                projectId, uuid(c, "applicationId"),
                text(c, "code"), text(c, "title"), text(c, "description"),
                text(c, "requirementType"), text(c, "priority"),
                uuid(c, "functionalItemId"), uuid(c, "nonFunctionalItemId"),
                uuid(c, "scopeItemId"), coalesce(uuid(c, "scopePackageId"), scopePackageId)
        ));
        return null;
    }

    // ── Functional Item ───────────────────────────────────────────────────────

    private String executeUpdateFunction(JsonNode c, UUID projectId, UUID targetId) {
        if (targetId == null) throw new IllegalArgumentException("targetEntityId required for UPDATE_FUNCTION");
        Map<String, Object> cur = loadFunctionalItemCurrentValues(targetId);
        updateFunction.execute(new UpdateFunctionalItemCommand(
                targetId, projectId,
                coalesceUuid(uuid(c, "moduleId"), (UUID) cur.get("moduleId")),
                coalesceStr(text(c, "title"), (String) cur.get("title")),
                coalesceStr(text(c, "description"), (String) cur.get("description")),
                coalesceStr(text(c, "priority"), (String) cur.get("priority")),
                coalesceStr(text(c, "status"), (String) cur.get("status")),
                coalesceStr(text(c, "type"), (String) cur.get("type")),
                null
        ));
        return null;
    }

    private String executeCreateFunction(JsonNode c, UUID projectId, UUID workspaceId) {
        createFunction.execute(new CreateFunctionalItemCommand(
                projectId, coalesce(uuid(c, "workspaceId"), workspaceId),
                uuid(c, "moduleId"), text(c, "code"), text(c, "title"), text(c, "description"),
                text(c, "priority"), text(c, "type"),
                parseStringList(c, "acceptanceCriteria"),
                parseBusinessRules(c)
        ));
        return null;
    }

    // ── Use Case ──────────────────────────────────────────────────────────────

    private String executeUpdateUseCase(JsonNode c, UUID projectId, UUID targetId) {
        if (targetId == null) throw new IllegalArgumentException("targetEntityId required for UPDATE_USE_CASE");
        Map<String, Object> cur = loadUseCaseCurrentValues(targetId);
        updateUseCase.execute(new UpdateUseCaseCommand(
                projectId, targetId,
                coalesceStr(text(c, "name"), (String) cur.get("name")),
                coalesceStr(text(c, "goal"), (String) cur.get("goal")),
                coalesceStr(text(c, "primaryActorName"), (String) cur.get("primaryActorName")),
                coalesceStr(text(c, "triggerText"), (String) cur.get("triggerText")),
                coalesceStr(text(c, "status"), (String) cur.get("status")),
                uuid(c, "primaryFunctionId")
        ));
        return null;
    }

    private String executeCreateUseCase(JsonNode c, UUID projectId) {
        createUseCase.execute(new CreateUseCaseCommand(
                projectId, uuid(c, "primaryFunctionId"),
                text(c, "key"), text(c, "name"), text(c, "goal"),
                text(c, "primaryActorName"), text(c, "triggerText")
        ));
        return null;
    }

    // ── Screen ────────────────────────────────────────────────────────────────

    private String executeCreateScreen(JsonNode c, UUID projectId, UUID workspaceId) {
        UUID appId = uuid(c, "applicationId");
        if (appId == null) throw new IllegalArgumentException("changesJson.applicationId required for CREATE_SCREEN");
        createScreen.execute(new CreateRegistryScreenCommand(
                coalesce(uuid(c, "workspaceId"), workspaceId), appId, projectId,
                text(c, "code"), text(c, "name"), text(c, "routePath")
        ));
        return null;
    }

    private String executeUpdateScreen(JsonNode c, UUID workspaceId, UUID targetId) {
        if (targetId == null) throw new IllegalArgumentException("targetEntityId required for UPDATE_SCREEN");
        UUID appId = uuid(c, "applicationId");
        if (appId == null) throw new IllegalArgumentException("changesJson.applicationId required for UPDATE_SCREEN");
        updateScreen.execute(new UpdateRegistryScreenCommand(
                coalesce(uuid(c, "workspaceId"), workspaceId), appId, targetId,
                text(c, "name"), text(c, "routePath")
        ));
        return null;
    }

    // ── Links ─────────────────────────────────────────────────────────────────

    private String executeLinkRequirementFunction(ElicitationSuggestionItem item, UUID projectId, UUID targetId) {
        UUID functionId = targetId;
        UUID requirementId = item.requirementId();
        if (functionId == null) throw new IllegalArgumentException("targetEntityId (functionId) required for LINK_REQUIREMENT_FUNCTION");
        if (requirementId == null) throw new IllegalArgumentException("requirementId required for LINK_REQUIREMENT_FUNCTION");
        linkRequirementFunction.execute(new LinkRequirementToFunctionCommand(projectId, functionId, requirementId));
        return null;
    }

    private String executeUnlinkRequirementFunction(ElicitationSuggestionItem item, UUID projectId, UUID targetId) {
        UUID functionId = targetId;
        UUID requirementId = item.requirementId();
        if (functionId == null) throw new IllegalArgumentException("targetEntityId (functionId) required for UNLINK_REQUIREMENT_FUNCTION");
        if (requirementId == null) throw new IllegalArgumentException("requirementId required for UNLINK_REQUIREMENT_FUNCTION");
        unlinkRequirementFunction.execute(new UnlinkRequirementFromFunctionCommand(projectId, functionId, requirementId));
        return null;
    }

    private String executeLinkFunctionUseCase(JsonNode c, UUID projectId, UUID targetId) {
        UUID useCaseId = targetId;
        UUID functionId = uuid(c, "functionId");
        if (useCaseId == null) throw new IllegalArgumentException("targetEntityId (useCaseId) required for LINK_FUNCTION_USE_CASE");
        if (functionId == null) throw new IllegalArgumentException("changesJson.functionId required for LINK_FUNCTION_USE_CASE");
        addSupportingFunction.execute(new AddSupportingFunctionCommand(projectId, useCaseId, functionId));
        return null;
    }

    private String executeUnlinkFunctionUseCase(JsonNode c, UUID projectId, UUID targetId) {
        UUID useCaseId = targetId;
        UUID functionId = uuid(c, "functionId");
        if (useCaseId == null) throw new IllegalArgumentException("targetEntityId (useCaseId) required for UNLINK_FUNCTION_USE_CASE");
        if (functionId == null) throw new IllegalArgumentException("changesJson.functionId required for UNLINK_FUNCTION_USE_CASE");
        removeSupportingFunction.execute(new RemoveSupportingFunctionCommand(projectId, useCaseId, functionId));
        return null;
    }

    private String executeLinkFunctionScreen(JsonNode c, UUID projectId, UUID targetId) {
        UUID functionId = targetId;
        UUID screenId = uuid(c, "screenId");
        if (functionId == null) throw new IllegalArgumentException("targetEntityId (functionId) required for LINK_FUNCTION_SCREEN");
        if (screenId == null) throw new IllegalArgumentException("changesJson.screenId required for LINK_FUNCTION_SCREEN");
        linkFunctionScreen.execute(new LinkFunctionScreenCommand(projectId, functionId, screenId, text(c, "note")));
        return null;
    }

    private String executeCreateComponent(JsonNode c, UUID workspaceId) {
        UUID appId = uuid(c, "applicationId");
        if (appId == null) throw new IllegalArgumentException("changesJson.applicationId required for CREATE_COMPONENT");
        createComponent.execute(new CreateRegistryAppComponentCommand(
                appId, coalesce(uuid(c, "workspaceId"), workspaceId),
                text(c, "code"), text(c, "name"), text(c, "description"), text(c, "componentType")
        ));
        return null;
    }

    private String executeLinkScreenComponent(JsonNode c, UUID workspaceId, UUID targetId) {
        UUID screenId = targetId;
        UUID componentId = uuid(c, "componentId");
        if (screenId == null) throw new IllegalArgumentException("targetEntityId (screenId) required for LINK_SCREEN_COMPONENT");
        if (componentId == null) throw new IllegalArgumentException("changesJson.componentId required for LINK_SCREEN_COMPONENT");
        linkScreenComponent.execute(new LinkScreenComponentCommand(
                coalesce(uuid(c, "workspaceId"), workspaceId),
                screenId, componentId,
                uuid(c, "sectionId"),
                intVal(c, "displayOrder", 0),
                text(c, "note")
        ));
        return null;
    }

    // ── JDBC helpers ──────────────────────────────────────────────────────────

    private Map<String, Object> loadFunctionalItemCurrentValues(UUID id) {
        String sql = "SELECT title, description, priority, status, type, module_id FROM app_functional_item WHERE id = :id LIMIT 1";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource("id", id));
        if (rows.isEmpty()) throw new IllegalArgumentException("FunctionalItem not found: " + id);
        Map<String, Object> row = rows.get(0);
        return Map.of(
                "title", orEmpty(row.get("title")),
                "description", row.getOrDefault("description", ""),
                "priority", orEmpty(row.get("priority")),
                "status", orEmpty(row.get("status")),
                "type", orEmpty(row.get("type")),
                "moduleId", row.get("module_id")
        );
    }

    private Map<String, Object> loadUseCaseCurrentValues(UUID id) {
        String sql = "SELECT name, goal, primary_actor_name, trigger_text, status FROM app_use_case WHERE id = :id LIMIT 1";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource("id", id));
        if (rows.isEmpty()) throw new IllegalArgumentException("UseCase not found: " + id);
        Map<String, Object> row = rows.get(0);
        return Map.of(
                "name", orEmpty(row.get("name")),
                "goal", row.getOrDefault("goal", ""),
                "primaryActorName", row.getOrDefault("primary_actor_name", ""),
                "triggerText", row.getOrDefault("trigger_text", ""),
                "status", orEmpty(row.get("status"))
        );
    }

    // ── JSON helpers ──────────────────────────────────────────────────────────

    private JsonNode parseChanges(String changesJson) {
        if (changesJson == null || changesJson.isBlank()) return objectMapper.createObjectNode();
        try {
            return objectMapper.readTree(changesJson);
        } catch (Exception e) {
            log.warn("Failed to parse changesJson: {}", e.getMessage());
            return objectMapper.createObjectNode();
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.path(field);
        return (v.isMissingNode() || v.isNull()) ? null : v.asText(null);
    }

    private static UUID uuid(JsonNode node, String field) {
        String v = text(node, field);
        if (v == null || v.isBlank()) return null;
        try { return UUID.fromString(v); } catch (IllegalArgumentException e) { return null; }
    }

    private static int intVal(JsonNode node, String field, int defaultVal) {
        JsonNode v = node.path(field);
        return v.isInt() ? v.intValue() : defaultVal;
    }

    private static UUID coalesce(UUID a, UUID b) { return a != null ? a : b; }
    private static UUID coalesceUuid(UUID a, Object b) { return a != null ? a : (UUID) b; }
    private static String coalesceStr(String a, Object b) { return a != null ? a : (b != null ? b.toString() : null); }
    private static String orEmpty(Object v) { return v != null ? v.toString() : ""; }

    private static List<String> parseStringList(JsonNode node, String field) {
        JsonNode arr = node.path(field);
        if (!arr.isArray() || arr.isEmpty()) return null;
        List<String> list = new ArrayList<>();
        for (JsonNode item : arr) {
            String v = item.asText(null);
            if (v != null && !v.isBlank()) list.add(v);
        }
        return list.isEmpty() ? null : list;
    }

    private static List<CreateBusinessRuleCommand> parseBusinessRules(JsonNode node) {
        JsonNode arr = node.path("businessRules");
        if (!arr.isArray() || arr.isEmpty()) return null;
        List<CreateBusinessRuleCommand> list = new ArrayList<>();
        for (JsonNode br : arr) {
            String code = text(br, "code");
            String title = text(br, "title");
            if (code == null || title == null) continue;
            String severity = text(br, "severity");
            list.add(new CreateBusinessRuleCommand(null, null, code, title,
                    text(br, "description"), severity != null ? severity : "MEDIUM"));
        }
        return list.isEmpty() ? null : list;
    }
}
