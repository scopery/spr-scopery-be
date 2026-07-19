package com.company.scopery.modules.servicesupport.escalation.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.escalation.application.action.*;
import com.company.scopery.modules.servicesupport.escalation.application.command.CreateEscalationRuleCommand;
import com.company.scopery.modules.servicesupport.escalation.application.response.SupportEscalationRuleResponse;
import com.company.scopery.modules.servicesupport.escalation.application.service.SupportEscalationRuleQueryService;
import com.company.scopery.modules.servicesupport.escalation.http.request.CreateEscalationRuleRequest;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Service Support - Escalation Rules")
public class SupportEscalationRuleController {
    private final SupportEscalationRuleQueryService query;
    private final CreateSupportEscalationRuleAction createAction;
    private final DisableSupportEscalationRuleAction disableAction;
    private final EnableSupportEscalationRuleAction enableAction;

    public SupportEscalationRuleController(SupportEscalationRuleQueryService query,
            CreateSupportEscalationRuleAction createAction, DisableSupportEscalationRuleAction disableAction,
            EnableSupportEscalationRuleAction enableAction) {
        this.query = query; this.createAction = createAction;
        this.disableAction = disableAction; this.enableAction = enableAction;
    }

    @GetMapping(SupportApiPaths.ESCALATION_RULES)
    @Operation(summary = "List escalation rules")
    public ApiResponse<List<SupportEscalationRuleResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.listByWorkspace(workspaceId));
    }

    @PostMapping(SupportApiPaths.ESCALATION_RULES)
    @Operation(summary = "Create escalation rule")
    public ApiResponse<SupportEscalationRuleResponse> create(@PathVariable UUID workspaceId,
            @RequestBody @Valid CreateEscalationRuleRequest req) {
        return ApiResponse.success(createAction.execute(workspaceId,
                new CreateEscalationRuleCommand(req.ruleCode(), req.name(), req.triggerType())));
    }

    @PostMapping(SupportApiPaths.ESCALATION_RULE_DISABLE)
    @Operation(summary = "Disable escalation rule")
    public ApiResponse<SupportEscalationRuleResponse> disable(@PathVariable UUID workspaceId,
            @PathVariable UUID ruleId) {
        return ApiResponse.success(disableAction.execute(workspaceId, ruleId));
    }

    @PostMapping(SupportApiPaths.ESCALATION_RULE_ENABLE)
    @Operation(summary = "Enable escalation rule")
    public ApiResponse<SupportEscalationRuleResponse> enable(@PathVariable UUID workspaceId,
            @PathVariable UUID ruleId) {
        return ApiResponse.success(enableAction.execute(workspaceId, ruleId));
    }
}
