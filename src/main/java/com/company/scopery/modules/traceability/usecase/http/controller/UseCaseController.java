package com.company.scopery.modules.traceability.usecase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.usecase.application.action.AddSupportingFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.BulkCreateUseCaseJobHandler;
import com.company.scopery.modules.traceability.usecase.application.action.BulkLinkRequirementFunctionJobHandler;
import com.company.scopery.modules.traceability.usecase.application.action.CreateUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.action.DeleteUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.action.ImportUseCaseNestedAction;
import com.company.scopery.modules.traceability.usecase.application.action.LinkRequirementToFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.LinkRequirementToUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.action.RemoveSupportingFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.UnlinkRequirementFromFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.UnlinkRequirementFromUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.action.UpdateUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.command.AddSupportingFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.BulkCreateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.CreateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.ImportUseCaseNestedCommand;
import com.company.scopery.modules.traceability.usecase.application.command.LinkRequirementToFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.LinkRequirementToUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.RemoveSupportingFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UnlinkRequirementFromFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UnlinkRequirementFromUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.response.ImportUseCaseNestedResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseDetailResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseResponse;
import com.company.scopery.modules.traceability.usecase.application.service.UseCaseQueryService;
import com.company.scopery.modules.traceability.usecase.http.request.AddSupportingFunctionRequest;
import com.company.scopery.modules.traceability.usecase.http.request.BulkCreateUseCaseRequest;
import com.company.scopery.modules.traceability.usecase.http.request.CreateUseCaseRequest;
import com.company.scopery.modules.traceability.usecase.http.request.ImportUseCaseNestedRequest;
import com.company.scopery.modules.traceability.usecase.application.command.BulkLinkRequirementFunctionCommand;
import com.company.scopery.modules.traceability.usecase.http.request.BulkLinkRequirementFunctionRequest;
import com.company.scopery.modules.traceability.usecase.http.request.LinkRequirementRequest;
import com.company.scopery.modules.traceability.usecase.http.request.NestedUseCasePartsRequest;
import com.company.scopery.modules.traceability.usecase.http.request.UpdateUseCaseRequest;
import com.company.scopery.platform.bulkjob.BulkJobResponse;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Traceability - Use Case")
public class UseCaseController {

    private final UseCaseQueryService queryService;
    private final CreateUseCaseAction createAction;
    private final ImportUseCaseNestedAction importNestedAction;
    private final UpdateUseCaseAction updateAction;
    private final DeleteUseCaseAction deleteAction;
    private final AddSupportingFunctionAction addSupportingFnAction;
    private final RemoveSupportingFunctionAction removeSupportingFnAction;
    private final LinkRequirementToUseCaseAction linkReqToUcAction;
    private final UnlinkRequirementFromUseCaseAction unlinkReqFromUcAction;
    private final LinkRequirementToFunctionAction linkReqToFnAction;
    private final UnlinkRequirementFromFunctionAction unlinkReqFromFnAction;
    private final BulkLinkRequirementFunctionJobHandler bulkLinkReqFnHandler;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public UseCaseController(UseCaseQueryService queryService,
                             CreateUseCaseAction createAction,
                             ImportUseCaseNestedAction importNestedAction,
                             UpdateUseCaseAction updateAction,
                             DeleteUseCaseAction deleteAction,
                             AddSupportingFunctionAction addSupportingFnAction,
                             RemoveSupportingFunctionAction removeSupportingFnAction,
                             LinkRequirementToUseCaseAction linkReqToUcAction,
                             UnlinkRequirementFromUseCaseAction unlinkReqFromUcAction,
                             LinkRequirementToFunctionAction linkReqToFnAction,
                             UnlinkRequirementFromFunctionAction unlinkReqFromFnAction,
                             BulkLinkRequirementFunctionJobHandler bulkLinkReqFnHandler,
                             BulkJobService bulkJobService,
                             ObjectMapper objectMapper) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.importNestedAction = importNestedAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
        this.addSupportingFnAction = addSupportingFnAction;
        this.removeSupportingFnAction = removeSupportingFnAction;
        this.linkReqToUcAction = linkReqToUcAction;
        this.unlinkReqFromUcAction = unlinkReqFromUcAction;
        this.linkReqToFnAction = linkReqToFnAction;
        this.unlinkReqFromFnAction = unlinkReqFromFnAction;
        this.bulkLinkReqFnHandler = bulkLinkReqFnHandler;
        this.bulkJobService = bulkJobService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(TraceabilityApiPaths.USE_CASES)
    @Operation(summary = "List use cases for a project")
    public ApiResponse<List<UseCaseResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(queryService.listByProject(projectId));
    }

    @PostMapping(TraceabilityApiPaths.USE_CASES)
    @Operation(summary = "Create a use case")
    public ApiResponse<UseCaseDetailResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateUseCaseRequest r) {
        return ApiResponse.success(createAction.execute(toCreateCommand(projectId, r)));
    }

    @PostMapping(TraceabilityApiPaths.USE_CASES + "/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create use cases (async — poll GET /api/bulk-jobs/{id} for status). Optional nested flows/conditions/rules/criteria are applied by BE after each shell.")
    public ApiResponse<BulkJobResponse> bulkCreate(
            @PathVariable UUID projectId,
            @Valid @RequestBody BulkCreateUseCaseRequest r) {
        var items = r.items().stream().map(i -> toCreateCommand(projectId, i)).toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateUseCaseCommand(projectId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateUseCaseJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    private static CreateUseCaseCommand toCreateCommand(UUID projectId, CreateUseCaseRequest r) {
        return new CreateUseCaseCommand(
                projectId,
                r.primaryFunctionId() != null ? UUID.fromString(r.primaryFunctionId()) : null,
                r.key(),
                r.name(),
                r.goal(),
                r.primaryActorName(),
                r.triggerText(),
                mapFlows(r.flows()),
                mapConditions(r.conditions()),
                mapRules(r.businessRules()),
                mapCriteria(r.acceptanceCriteria())
        );
    }

    private static List<CreateUseCaseCommand.NestedFlow> mapFlows(
            List<NestedUseCasePartsRequest.NestedFlowRequest> flows) {
        if (flows == null) return null;
        return flows.stream()
                .map(f -> new CreateUseCaseCommand.NestedFlow(
                        f.flowType(),
                        f.name(),
                        f.conditionText(),
                        f.steps() == null
                                ? null
                                : f.steps().stream()
                                        .map(s -> new CreateUseCaseCommand.NestedStep(
                                                s.stepType(),
                                                s.resolvedContentJson(),
                                                s.displayOrder()))
                                        .toList()))
                .toList();
    }

    private static List<CreateUseCaseCommand.NestedCondition> mapConditions(
            List<NestedUseCasePartsRequest.NestedConditionRequest> conditions) {
        if (conditions == null) return null;
        return conditions.stream()
                .map(c -> new CreateUseCaseCommand.NestedCondition(
                        c.conditionType(), c.content(), c.displayOrder()))
                .toList();
    }

    private static List<CreateUseCaseCommand.NestedBusinessRule> mapRules(
            List<NestedUseCasePartsRequest.NestedBusinessRuleRequest> rules) {
        if (rules == null) return null;
        return rules.stream()
                .map(r -> new CreateUseCaseCommand.NestedBusinessRule(
                        r.ruleCode(), r.description(), r.displayOrder()))
                .toList();
    }

    private static List<CreateUseCaseCommand.NestedAcceptanceCriterion> mapCriteria(
            List<NestedUseCasePartsRequest.NestedAcceptanceCriterionRequest> criteria) {
        if (criteria == null) return null;
        return criteria.stream()
                .map(c -> new CreateUseCaseCommand.NestedAcceptanceCriterion(
                        c.title(), c.givenText(), c.whenText(), c.thenText(), c.displayOrder()))
                .toList();
    }

    @GetMapping(TraceabilityApiPaths.USE_CASE)
    @Operation(summary = "Get use case detail")
    public ApiResponse<UseCaseDetailResponse> getDetail(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId) {
        return ApiResponse.success(queryService.getDetail(projectId, useCaseId));
    }

    @PostMapping(TraceabilityApiPaths.USE_CASE_NESTED_IMPORT)
    @Operation(summary = "Import nested parts onto an existing use case (one request; BE applies flows/steps/conditions/rules/criteria)")
    public ApiResponse<ImportUseCaseNestedResponse> importNested(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @Valid @RequestBody ImportUseCaseNestedRequest r) {
        return ApiResponse.success(importNestedAction.execute(new ImportUseCaseNestedCommand(
                projectId,
                useCaseId,
                mapFlows(r.flows()),
                mapConditions(r.conditions()),
                mapRules(r.businessRules()),
                mapCriteria(r.acceptanceCriteria()),
                r.supportingFunctionIds()
        )));
    }

    @PutMapping(TraceabilityApiPaths.USE_CASE)
    @Operation(summary = "Update a use case")
    public ApiResponse<UseCaseResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @Valid @RequestBody UpdateUseCaseRequest r) {
        return ApiResponse.success(updateAction.execute(new UpdateUseCaseCommand(
                projectId, useCaseId, r.name(), r.goal(), r.primaryActorName(), r.triggerText(), r.status(),
                r.primaryFunctionId() != null && !r.primaryFunctionId().isBlank()
                        ? UUID.fromString(r.primaryFunctionId())
                        : null)));
    }

    @DeleteMapping(TraceabilityApiPaths.USE_CASE)
    @Operation(summary = "Delete a use case")
    public ApiResponse<Void> delete(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId) {
        deleteAction.execute(new DeleteUseCaseCommand(projectId, useCaseId));
        return ApiResponse.success(null);
    }

    @GetMapping(TraceabilityApiPaths.FUNCTION_USE_CASES)
    @Operation(summary = "List use cases for a function")
    public ApiResponse<List<UseCaseResponse>> listByFunction(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId) {
        return ApiResponse.success(queryService.listByFunction(projectId, functionalItemId));
    }

    @PostMapping(TraceabilityApiPaths.USE_CASE_SUPPORTING_FNS)
    @Operation(summary = "Add a supporting function to a use case")
    public ApiResponse<Void> addSupportingFunction(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @Valid @RequestBody AddSupportingFunctionRequest r) {
        addSupportingFnAction.execute(new AddSupportingFunctionCommand(
                projectId, useCaseId, UUID.fromString(r.functionId())));
        return ApiResponse.success(null);
    }

    @DeleteMapping(TraceabilityApiPaths.USE_CASE_SUPPORTING_FN)
    @Operation(summary = "Remove a supporting function from a use case")
    public ApiResponse<Void> removeSupportingFunction(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID functionId) {
        removeSupportingFnAction.execute(new RemoveSupportingFunctionCommand(projectId, useCaseId, functionId));
        return ApiResponse.success(null);
    }

    @GetMapping(TraceabilityApiPaths.USE_CASE_REQUIREMENTS)
    @Operation(summary = "Get requirement IDs linked to a use case")
    public ApiResponse<List<UUID>> getUseCaseRequirements(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId) {
        return ApiResponse.success(queryService.getLinkedRequirementIdsByUseCase(projectId, useCaseId));
    }

    @PostMapping(TraceabilityApiPaths.USE_CASE_REQUIREMENTS)
    @Operation(summary = "Link a requirement to a use case")
    public ApiResponse<Void> linkRequirementToUseCase(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @Valid @RequestBody LinkRequirementRequest r) {
        linkReqToUcAction.execute(new LinkRequirementToUseCaseCommand(
                projectId, useCaseId, UUID.fromString(r.requirementId())));
        return ApiResponse.success(null);
    }

    @DeleteMapping(TraceabilityApiPaths.USE_CASE_REQUIREMENT)
    @Operation(summary = "Unlink a requirement from a use case")
    public ApiResponse<Void> unlinkRequirementFromUseCase(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @PathVariable UUID requirementId) {
        unlinkReqFromUcAction.execute(new UnlinkRequirementFromUseCaseCommand(projectId, useCaseId, requirementId));
        return ApiResponse.success(null);
    }

    @GetMapping(TraceabilityApiPaths.FUNCTION_REQUIREMENTS)
    @Operation(summary = "Get requirement IDs linked to a function")
    public ApiResponse<List<UUID>> getFunctionRequirements(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId) {
        return ApiResponse.success(queryService.getLinkedRequirementIdsByFunction(projectId, functionalItemId));
    }

    @PostMapping(TraceabilityApiPaths.FUNCTION_REQUIREMENTS)
    @Operation(summary = "Link a requirement to a function")
    public ApiResponse<Void> linkRequirementToFunction(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @Valid @RequestBody LinkRequirementRequest r) {
        linkReqToFnAction.execute(new LinkRequirementToFunctionCommand(
                projectId, functionalItemId, UUID.fromString(r.requirementId())));
        return ApiResponse.success(null);
    }

    @DeleteMapping(TraceabilityApiPaths.FUNCTION_REQUIREMENT)
    @Operation(summary = "Unlink a requirement from a function")
    public ApiResponse<Void> unlinkRequirementFromFunction(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @PathVariable UUID requirementId) {
        unlinkReqFromFnAction.execute(new UnlinkRequirementFromFunctionCommand(
                projectId, functionalItemId, requirementId));
        return ApiResponse.success(null);
    }

    @PostMapping(TraceabilityApiPaths.FUNCTION_REQUIREMENTS_BULK_LINK)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk link requirements to a function (async — poll GET /api/bulk-jobs/{id} for status). Already-linked pairs are skipped.")
    public ApiResponse<BulkJobResponse> bulkLinkRequirementsToFunction(
            @PathVariable UUID projectId,
            @PathVariable UUID functionalItemId,
            @Valid @RequestBody BulkLinkRequirementFunctionRequest r) {
        var requirementIds = r.requirementIds().stream().map(UUID::fromString).toList();
        try {
            String payload = objectMapper.writeValueAsString(
                    new BulkLinkRequirementFunctionCommand(projectId, functionalItemId, requirementIds));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(bulkLinkReqFnHandler.supportsJobType(), requirementIds.size(), payload)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk link payload", e);
        }
    }
}
