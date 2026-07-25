package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.scope.scopeitem.domain.enums.ScopeItemType;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItem;
import com.company.scopery.modules.scope.scopeitem.domain.model.ScopeItemRepository;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackage;
import com.company.scopery.modules.scope.scopepackage.domain.model.ScopePackageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreateScopeItemToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateScopeItemToolAdapter.class);
    private static final String TOOL_CODE = "create_scope_item";
    private static final String TOOL_VERSION = "v1";

    private final ScopePackageRepository scopePackageRepository;
    private final ScopeItemRepository scopeItemRepository;

    public CreateScopeItemToolAdapter(ScopePackageRepository scopePackageRepository,
                                      ScopeItemRepository scopeItemRepository) {
        this.scopePackageRepository = scopePackageRepository;
        this.scopeItemRepository = scopeItemRepository;
    }

    @Override public String toolCode() { return TOOL_CODE; }
    @Override public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new scope item inside a project's scope package. "
                + "Required: projectId, title, type (FEATURE/REQUIREMENT/CONSTRAINT/ASSUMPTION/EXCLUSION). "
                + "Optional: packageId (UUID of target scope package; if omitted, uses the first editable package found), "
                + "code, description, inScope (default true), priority (LOW/MEDIUM/HIGH/CRITICAL), acceptanceRequired (default true). "
                + "FIELD COMPLETENESS rules: "
                + "(1) description: always include a clear explanation of what this scope item covers. "
                + "(2) type: FEATURE for product features, REQUIREMENT for specific requirements, CONSTRAINT for constraints, "
                + "ASSUMPTION for assumptions, EXCLUSION for explicitly out-of-scope items. "
                + "(3) inScope: true unless creating an out-of-scope exclusion item. "
                + "(4) priority: infer from context or default to MEDIUM.";
    }

    @Override
    public String parametersSchemaJson() {
        return """
                {
                  "type": "object",
                  "properties": {
                    "projectId":          { "type": "string", "format": "uuid" },
                    "packageId":          { "type": "string", "format": "uuid", "description": "Target scope package. Auto-resolved if omitted." },
                    "title":              { "type": "string" },
                    "type":               { "type": "string", "enum": ["FEATURE", "REQUIREMENT", "CONSTRAINT", "ASSUMPTION", "EXCLUSION"] },
                    "code":               { "type": "string" },
                    "description":        { "type": "string" },
                    "inScope":            { "type": "boolean" },
                    "priority":           { "type": "string", "enum": ["LOW", "MEDIUM", "HIGH", "CRITICAL"] }
                  },
                  "required": ["projectId", "title", "type"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        var hints = new java.util.LinkedHashMap<String, String>();
        String title = getString(inputArgs, "title");
        if (title != null) hints.put("title", title);
        String type = getString(inputArgs, "type");
        if (type != null) hints.put("type", type);
        String priority = getString(inputArgs, "priority");
        if (priority != null) hints.put("priority", priority);
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String projectId = getString(input, "projectId");
        String title = getString(input, "title");
        String type = getString(input, "type");
        if (projectId == null || title == null || title.isBlank() || type == null) {
            return new AiActionDryRunResult(false, List.of("Missing required: projectId, title, type"), null, false, null);
        }
        String diff = "{\"projectId\":\"" + projectId + "\",\"title\":\"" + title + "\",\"type\":\"" + type + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diff);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String projectId = getString(input, "projectId");
        String title = getString(input, "title");
        String typeStr = getString(input, "type");

        if (projectId == null || title == null || title.isBlank() || typeStr == null) {
            return AiActionToolResult.failed("MISSING_REQUIRED_INPUT", false);
        }

        UUID projectUuid;
        try {
            projectUuid = UUID.fromString(projectId);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_UUID_INPUT", false);
        }

        ScopeItemType type;
        try {
            type = ScopeItemType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_SCOPE_ITEM_TYPE", false);
        }

        // Resolve package: use explicit packageId or find the first editable one
        ScopePackage pkg = resolvePackage(projectUuid, getString(input, "packageId"));
        if (pkg == null) {
            return AiActionToolResult.failed("NO_EDITABLE_SCOPE_PACKAGE", false);
        }

        String description = getString(input, "description");
        String code = getString(input, "code");
        String priorityStr = getString(input, "priority");
        boolean inScope = input.get("inScope") == null || Boolean.TRUE.equals(input.get("inScope"))
                || "true".equalsIgnoreCase(getString(input, "inScope"));

        ScopeItem item = ScopeItem.create(
                pkg.id(), projectUuid, pkg.workspaceId(), type,
                code, title.trim(), description,
                inScope, false,
                priorityStr != null ? priorityStr.toUpperCase() : "MEDIUM",
                true, null);

        ScopeItem saved = scopeItemRepository.save(item);

        log.info("[CreateScopeItemTool] created — id={} project={} package={}", saved.id(), projectUuid, pkg.id());

        String resultSummary = "{\"scopeItemId\":\"" + saved.id() + "\",\"title\":\"" + saved.title()
                + "\",\"type\":\"" + saved.type() + "\",\"packageId\":\"" + pkg.id() + "\"}";
        return AiActionToolResult.succeeded(saved.id() + ":v1", resultSummary, null, null);
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
    }

    private ScopePackage resolvePackage(UUID projectId, String packageIdStr) {
        if (packageIdStr != null) {
            try {
                UUID packageUuid = UUID.fromString(packageIdStr);
                Optional<ScopePackage> found = scopePackageRepository.findByIdAndProjectId(packageUuid, projectId);
                if (found.isPresent() && found.get().isEditable()) return found.get();
            } catch (IllegalArgumentException ignored) { /* fall through to auto-resolve */ }
        }
        // Auto-resolve: prefer the current package if editable, then any editable
        List<ScopePackage> packages = scopePackageRepository.findByProjectId(projectId);
        return packages.stream()
                .filter(ScopePackage::isEditable)
                .findFirst()
                .orElse(null);
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
