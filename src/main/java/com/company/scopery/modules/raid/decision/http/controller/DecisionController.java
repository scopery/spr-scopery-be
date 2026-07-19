package com.company.scopery.modules.raid.decision.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.raid.decision.application.action.*;
import com.company.scopery.modules.raid.decision.application.command.*;
import com.company.scopery.modules.raid.decision.application.response.DecisionImpactResponse;
import com.company.scopery.modules.raid.decision.application.response.DecisionOptionResponse;
import com.company.scopery.modules.raid.decision.application.response.DecisionRecordResponse;
import com.company.scopery.modules.raid.decision.application.service.DecisionQueryService;
import com.company.scopery.modules.raid.decision.http.request.*;
import com.company.scopery.modules.raid.decisionlink.application.action.CreateDecisionLinkAction;
import com.company.scopery.modules.raid.decisionlink.application.action.DeleteDecisionLinkAction;
import com.company.scopery.modules.raid.decisionlink.application.command.CreateDecisionLinkCommand;
import com.company.scopery.modules.raid.decisionlink.application.command.DeleteDecisionLinkCommand;
import com.company.scopery.modules.raid.decisionlink.application.response.DecisionLinkResponse;
import com.company.scopery.modules.raid.decisionlink.application.service.DecisionLinkQueryService;
import com.company.scopery.modules.raid.decisionlink.http.request.CreateDecisionLinkRequest;
import com.company.scopery.modules.raid.shared.constant.RaidApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(RaidApiPaths.DECISIONS)
@Tag(name = "RAID - Decisions")
public class DecisionController {
    private final CreateDecisionAction create;
    private final UpdateDecisionAction update;
    private final SupersedeDecisionAction supersede;
    private final DecideDecisionAction decide;
    private final RejectDecisionAction reject;
    private final ArchiveDecisionAction archive;
    private final CreateDecisionOptionAction createOption;
    private final UpdateDecisionOptionAction updateOption;
    private final DeleteDecisionOptionAction deleteOption;
    private final UpsertDecisionImpactAction upsertImpact;
    private final CreateDecisionLinkAction createLink;
    private final DeleteDecisionLinkAction deleteLink;
    private final DecisionQueryService query;
    private final DecisionLinkQueryService linkQuery;

    public DecisionController(CreateDecisionAction create, UpdateDecisionAction update,
                              SupersedeDecisionAction supersede, DecideDecisionAction decide,
                              RejectDecisionAction reject, ArchiveDecisionAction archive,
                              CreateDecisionOptionAction createOption, UpdateDecisionOptionAction updateOption,
                              DeleteDecisionOptionAction deleteOption, UpsertDecisionImpactAction upsertImpact,
                              CreateDecisionLinkAction createLink, DeleteDecisionLinkAction deleteLink,
                              DecisionQueryService query, DecisionLinkQueryService linkQuery) {
        this.create = create;
        this.update = update;
        this.supersede = supersede;
        this.decide = decide;
        this.reject = reject;
        this.archive = archive;
        this.createOption = createOption;
        this.updateOption = updateOption;
        this.deleteOption = deleteOption;
        this.upsertImpact = upsertImpact;
        this.createLink = createLink;
        this.deleteLink = deleteLink;
        this.query = query;
        this.linkQuery = linkQuery;
    }

    @PostMapping
    @Operation(summary = "Create decision")
    public ApiResponse<DecisionRecordResponse> create(@PathVariable UUID projectId,
                                                      @Valid @RequestBody CreateDecisionRequest request) {
        return ApiResponse.success(create.execute(new CreateDecisionCommand(
                projectId, request.title(), request.rationale(), request.category(), request.code())));
    }

    @GetMapping
    @Operation(summary = "List decisions")
    public ApiResponse<List<DecisionRecordResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{decisionId}")
    @Operation(summary = "Get decision")
    public ApiResponse<DecisionRecordResponse> get(@PathVariable UUID projectId, @PathVariable UUID decisionId) {
        return ApiResponse.success(query.get(projectId, decisionId));
    }

    @PutMapping("/{decisionId}")
    @Operation(summary = "Update decision")
    public ApiResponse<DecisionRecordResponse> update(@PathVariable UUID projectId, @PathVariable UUID decisionId,
                                                      @Valid @RequestBody UpdateDecisionRequest request) {
        return ApiResponse.success(update.execute(new UpdateDecisionCommand(
                projectId, decisionId, request.title(), request.rationale())));
    }

    @PostMapping("/{decisionId}/supersede")
    @Operation(summary = "Supersede decision")
    public ApiResponse<DecisionRecordResponse> supersede(@PathVariable UUID projectId, @PathVariable UUID decisionId,
                                                           @Valid @RequestBody(required = false) SupersedeDecisionRequest request) {
        var req = request == null ? new SupersedeDecisionRequest(null, null, null, null) : request;
        return ApiResponse.success(supersede.execute(new SupersedeDecisionCommand(
                projectId, decisionId, req.replacementDecisionId(), req.title(), req.rationale(), req.category())));
    }

    @PostMapping("/{decisionId}/decide")
    @Operation(summary = "Decide")
    public ApiResponse<DecisionRecordResponse> decide(@PathVariable UUID projectId, @PathVariable UUID decisionId,
                                                      @Valid @RequestBody DecideDecisionRequest request) {
        return ApiResponse.success(decide.execute(
                new DecideDecisionCommand(projectId, decisionId, request.outcome())));
    }

    @PostMapping("/{decisionId}/reject")
    @Operation(summary = "Reject decision")
    public ApiResponse<DecisionRecordResponse> reject(@PathVariable UUID projectId, @PathVariable UUID decisionId,
                                                      @Valid @RequestBody RejectDecisionRequest request) {
        return ApiResponse.success(reject.execute(
                new RejectDecisionCommand(projectId, decisionId, request.reason())));
    }

    @PatchMapping("/{decisionId}/archive")
    @Operation(summary = "Archive decision")
    public ApiResponse<DecisionRecordResponse> archive(@PathVariable UUID projectId, @PathVariable UUID decisionId) {
        return ApiResponse.success(archive.execute(projectId, decisionId));
    }

    @PostMapping("/{decisionId}/options")
    @Operation(summary = "Add decision option")
    public ApiResponse<DecisionOptionResponse> addOption(@PathVariable UUID projectId, @PathVariable UUID decisionId,
                                                         @Valid @RequestBody CreateDecisionOptionRequest request) {
        return ApiResponse.success(createOption.execute(new CreateDecisionOptionCommand(
                projectId, decisionId, request.optionTitle(), request.optionDescription(),
                request.pros(), request.cons(), request.estimatedImpact())));
    }

    @PutMapping("/{decisionId}/options/{optionId}")
    @Operation(summary = "Update decision option")
    public ApiResponse<DecisionOptionResponse> updateOption(@PathVariable UUID projectId,
                                                            @PathVariable UUID decisionId,
                                                            @PathVariable UUID optionId,
                                                            @Valid @RequestBody UpdateDecisionOptionRequest request) {
        return ApiResponse.success(updateOption.execute(new UpdateDecisionOptionCommand(
                projectId, decisionId, optionId, request.optionTitle(), request.optionDescription(),
                request.pros(), request.cons(), request.estimatedImpact())));
    }

    @DeleteMapping("/{decisionId}/options/{optionId}")
    @Operation(summary = "Delete decision option")
    public ApiResponse<Void> deleteOption(@PathVariable UUID projectId, @PathVariable UUID decisionId,
                                          @PathVariable UUID optionId) {
        deleteOption.execute(new DeleteDecisionOptionCommand(projectId, decisionId, optionId));
        return ApiResponse.success(null);
    }

    @GetMapping("/{decisionId}/options")
    @Operation(summary = "List decision options")
    public ApiResponse<List<DecisionOptionResponse>> options(@PathVariable UUID projectId,
                                                             @PathVariable UUID decisionId) {
        return ApiResponse.success(query.listOptions(projectId, decisionId));
    }

    @GetMapping("/{decisionId}/impact")
    @Operation(summary = "Get decision impact")
    public ApiResponse<DecisionImpactResponse> impact(@PathVariable UUID projectId, @PathVariable UUID decisionId) {
        return ApiResponse.success(query.getImpact(projectId, decisionId).orElse(null));
    }

    @PutMapping("/{decisionId}/impact")
    @Operation(summary = "Upsert decision impact")
    public ApiResponse<DecisionImpactResponse> upsertImpact(@PathVariable UUID projectId,
                                                            @PathVariable UUID decisionId,
                                                            @Valid @RequestBody UpsertDecisionImpactRequest request) {
        return ApiResponse.success(upsertImpact.execute(new UpsertDecisionImpactCommand(
                projectId, decisionId, request.scopeImpact(), request.scheduleImpactDays(),
                request.estimateHoursImpact(), request.costImpact(), request.revenueImpact(),
                request.marginImpact(), request.riskImpact(), request.deliverableImpact(),
                request.acceptanceImpact())));
    }

    @PostMapping("/{decisionId}/links")
    @Operation(summary = "Create decision link")
    public ApiResponse<DecisionLinkResponse> createLink(@PathVariable UUID projectId,
                                                        @PathVariable UUID decisionId,
                                                        @Valid @RequestBody CreateDecisionLinkRequest request) {
        return ApiResponse.success(createLink.execute(new CreateDecisionLinkCommand(
                projectId, decisionId, request.linkType(), request.targetType(), request.targetId())));
    }

    @GetMapping("/{decisionId}/links")
    @Operation(summary = "List decision links")
    public ApiResponse<List<DecisionLinkResponse>> listLinks(@PathVariable UUID projectId,
                                                             @PathVariable UUID decisionId) {
        return ApiResponse.success(linkQuery.list(projectId, decisionId));
    }

    @DeleteMapping("/{decisionId}/links/{linkId}")
    @Operation(summary = "Delete decision link")
    public ApiResponse<Void> deleteLink(@PathVariable UUID projectId, @PathVariable UUID decisionId,
                                        @PathVariable UUID linkId) {
        deleteLink.execute(new DeleteDecisionLinkCommand(projectId, decisionId, linkId));
        return ApiResponse.success(null);
    }
}
