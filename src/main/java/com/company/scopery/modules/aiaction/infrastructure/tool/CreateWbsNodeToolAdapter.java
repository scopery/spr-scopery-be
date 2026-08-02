package com.company.scopery.modules.aiaction.infrastructure.tool;

import com.company.scopery.modules.aiaction.application.port.AiActionCompensationResult;
import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CreateWbsNodeToolAdapter implements AiActionToolAdapter {

    private static final Logger log = LoggerFactory.getLogger(CreateWbsNodeToolAdapter.class);

    private static final String TOOL_CODE = "create_wbs_node";
    private static final String TOOL_VERSION = "v1";

    private final WbsNodeRepository wbsNodeRepository;

    public CreateWbsNodeToolAdapter(WbsNodeRepository wbsNodeRepository) {
        this.wbsNodeRepository = wbsNodeRepository;
    }

    @Override
    public String toolCode() { return TOOL_CODE; }

    @Override
    public String toolVersion() { return TOOL_VERSION; }

    @Override
    public String description() {
        return "Create a new WBS (Work Breakdown Structure) node in a project. Use when the user asks to add a work package, milestone, or task group to a project's WBS. "
                + "Required: projectId, projectPhaseId, title, nodeType (WORK_PACKAGE/TASK_GROUP/MILESTONE). Optional: parentId, description.";
    }

    @Override
    public String parametersSchemaJson() {
        return """
                {
                  "type": "object",
                  "properties": {
                    "projectId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the project to add the WBS node to."
                    },
                    "projectPhaseId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "The UUID of the project phase this WBS node belongs to."
                    },
                    "title": {
                      "type": "string",
                      "description": "The title of the WBS node."
                    },
                    "nodeType": {
                      "type": "string",
                      "enum": ["WORK_PACKAGE", "TASK_GROUP", "MILESTONE"],
                      "description": "The type of WBS node."
                    },
                    "parentId": {
                      "type": "string",
                      "format": "uuid",
                      "description": "Optional UUID of the parent WBS node. Omit for root-level nodes."
                    },
                    "description": {
                      "type": "string",
                      "description": "Optional description of the WBS node."
                    }
                  },
                  "required": ["projectId", "projectPhaseId", "title", "nodeType"]
                }
                """;
    }

    @Override
    public Map<String, String> resolveDisplayHints(Map<String, Object> inputArgs) {
        java.util.Map<String, String> hints = new java.util.LinkedHashMap<>();
        String title = getString(inputArgs, "title");
        if (title != null) hints.put("title", title);
        String nodeType = getString(inputArgs, "nodeType");
        if (nodeType != null) hints.put("nodeType", nodeType);
        return hints;
    }

    @Override
    public AiActionDryRunResult dryRun(Map<String, Object> input, AiActionStep step) {
        String projectId = getString(input, "projectId");
        String projectPhaseId = getString(input, "projectPhaseId");
        String title = getString(input, "title");
        String nodeType = getString(input, "nodeType");

        if (projectId == null || projectPhaseId == null || title == null || title.isBlank() || nodeType == null) {
            return new AiActionDryRunResult(false, List.of("Missing required field: projectId, projectPhaseId, title, or nodeType"), null, false, null);
        }
        try {
            UUID.fromString(projectId);
            UUID.fromString(projectPhaseId);
        } catch (IllegalArgumentException e) {
            return new AiActionDryRunResult(false, List.of("Invalid UUID format for projectId or projectPhaseId"), null, false, null);
        }
        try {
            WbsNodeType.valueOf(nodeType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new AiActionDryRunResult(false, List.of("Invalid nodeType — must be WORK_PACKAGE, TASK_GROUP, or MILESTONE"), null, false, null);
        }
        String diffJson = "{\"projectId\":\"" + projectId + "\",\"title\":\"" + title + "\",\"nodeType\":\"" + nodeType + "\"}";
        return new AiActionDryRunResult(true, List.of(), null, false, diffJson);
    }

    @Override
    @Transactional
    public AiActionToolResult execute(Map<String, Object> input, AiActionStep step, AiActionExecution execution) {
        String projectId = getString(input, "projectId");
        String projectPhaseId = getString(input, "projectPhaseId");
        String title = getString(input, "title");
        String nodeTypeStr = getString(input, "nodeType");

        if (projectId == null || projectPhaseId == null || title == null || title.isBlank() || nodeTypeStr == null) {
            return AiActionToolResult.failed("MISSING_REQUIRED_INPUT", false);
        }

        UUID projectUuid;
        UUID projectPhaseUuid;
        try {
            projectUuid = UUID.fromString(projectId);
            projectPhaseUuid = UUID.fromString(projectPhaseId);
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_UUID_INPUT", false);
        }

        WbsNodeType nodeType;
        try {
            nodeType = WbsNodeType.valueOf(nodeTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AiActionToolResult.failed("INVALID_WBS_NODE_TYPE", false);
        }

        String parentIdStr = getString(input, "parentId");
        UUID parentUuid = null;
        if (parentIdStr != null && !parentIdStr.isBlank()) {
            try {
                parentUuid = UUID.fromString(parentIdStr);
            } catch (IllegalArgumentException e) {
                return AiActionToolResult.failed("INVALID_UUID_INPUT", false);
            }
        }

        String description = getString(input, "description");

        int level;
        String path;

        if (parentUuid != null) {
            Optional<WbsNode> parentOpt = wbsNodeRepository.findById(parentUuid);
            if (parentOpt.isEmpty()) {
                return AiActionToolResult.failed("PARENT_WBS_NODE_NOT_FOUND", false);
            }
            WbsNode parent = parentOpt.get();
            String code = generateUniqueCode(projectUuid);
            level = parent.level() + 1;
            path = parent.path() + "/" + code;

            WbsNode node = WbsNode.create(projectUuid, projectPhaseUuid, parentUuid, code, title, description, nodeType, level, path, 1);
            WbsNode saved = wbsNodeRepository.save(node);

            log.info("[CreateWbsNodeTool] WBS node created — id={} code={} project={} parent={}", saved.id(), saved.code(), projectUuid, parentUuid);

            String resultToken = saved.id() + ":v1";
            String resultSummary = "{\"nodeId\":\"" + saved.id() + "\",\"code\":\"" + saved.code()
                    + "\",\"title\":\"" + saved.title() + "\",\"nodeType\":\"" + saved.nodeType().name()
                    + "\",\"level\":" + saved.level() + "}";

            return AiActionToolResult.succeeded(resultToken, resultSummary, null, null);
        } else {
            String code = generateUniqueCode(projectUuid);
            level = 1;
            path = code;

            WbsNode node = WbsNode.create(projectUuid, projectPhaseUuid, null, code, title, description, nodeType, level, path, 1);
            WbsNode saved = wbsNodeRepository.save(node);

            log.info("[CreateWbsNodeTool] WBS node created — id={} code={} project={}", saved.id(), saved.code(), projectUuid);

            String resultToken = saved.id() + ":v1";
            String resultSummary = "{\"nodeId\":\"" + saved.id() + "\",\"code\":\"" + saved.code()
                    + "\",\"title\":\"" + saved.title() + "\",\"nodeType\":\"" + saved.nodeType().name()
                    + "\",\"level\":" + saved.level() + "}";

            return AiActionToolResult.succeeded(resultToken, resultSummary, null, null);
        }
    }

    @Override
    public AiActionCompensationResult compensate(Map<String, Object> input, AiActionStepExecution stepExecution) {
        return AiActionCompensationResult.unsupported();
    }

    private String generateUniqueCode(UUID projectUuid) {
        for (int i = 0; i < 10; i++) {
            String code = "WBS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            if (!wbsNodeRepository.existsByProjectIdAndCode(projectUuid, code)) {
                return code;
            }
        }
        return "WBS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private static String getString(Map<String, Object> input, String key) {
        Object val = input.get(key);
        return val != null ? val.toString() : null;
    }
}
