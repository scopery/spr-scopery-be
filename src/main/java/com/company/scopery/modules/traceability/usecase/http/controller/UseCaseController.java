package com.company.scopery.modules.traceability.usecase.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.modules.traceability.usecase.application.action.AddSupportingFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.CreateUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.action.DeleteUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.action.LinkRequirementToFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.LinkRequirementToUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.action.RemoveSupportingFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.UnlinkRequirementFromFunctionAction;
import com.company.scopery.modules.traceability.usecase.application.action.UnlinkRequirementFromUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.action.UpdateUseCaseAction;
import com.company.scopery.modules.traceability.usecase.application.command.AddSupportingFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.CreateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.LinkRequirementToFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.LinkRequirementToUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.RemoveSupportingFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UnlinkRequirementFromFunctionCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UnlinkRequirementFromUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseDetailResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseResponse;
import com.company.scopery.modules.traceability.usecase.application.service.UseCaseQueryService;
import com.company.scopery.modules.traceability.usecase.http.request.AddSupportingFunctionRequest;
import com.company.scopery.modules.traceability.usecase.http.request.CreateUseCaseRequest;
import com.company.scopery.modules.traceability.usecase.http.request.LinkRequirementRequest;
import com.company.scopery.modules.traceability.usecase.http.request.UpdateUseCaseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Traceability - Use Case")
public class UseCaseController {

    private final UseCaseQueryService queryService;
    private final CreateUseCaseAction createAction;
    private final UpdateUseCaseAction updateAction;
    private final DeleteUseCaseAction deleteAction;
    private final AddSupportingFunctionAction addSupportingFnAction;
    private final RemoveSupportingFunctionAction removeSupportingFnAction;
    private final LinkRequirementToUseCaseAction linkReqToUcAction;
    private final UnlinkRequirementFromUseCaseAction unlinkReqFromUcAction;
    private final LinkRequirementToFunctionAction linkReqToFnAction;
    private final UnlinkRequirementFromFunctionAction unlinkReqFromFnAction;

    public UseCaseController(UseCaseQueryService queryService,
                             CreateUseCaseAction createAction,
                             UpdateUseCaseAction updateAction,
                             DeleteUseCaseAction deleteAction,
                             AddSupportingFunctionAction addSupportingFnAction,
                             RemoveSupportingFunctionAction removeSupportingFnAction,
                             LinkRequirementToUseCaseAction linkReqToUcAction,
                             UnlinkRequirementFromUseCaseAction unlinkReqFromUcAction,
                             LinkRequirementToFunctionAction linkReqToFnAction,
                             UnlinkRequirementFromFunctionAction unlinkReqFromFnAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.deleteAction = deleteAction;
        this.addSupportingFnAction = addSupportingFnAction;
        this.removeSupportingFnAction = removeSupportingFnAction;
        this.linkReqToUcAction = linkReqToUcAction;
        this.unlinkReqFromUcAction = unlinkReqFromUcAction;
        this.linkReqToFnAction = linkReqToFnAction;
        this.unlinkReqFromFnAction = unlinkReqFromFnAction;
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
        return ApiResponse.success(createAction.execute(new CreateUseCaseCommand(
                projectId, UUID.fromString(r.primaryFunctionId()),
                r.key(), r.name(), r.goal(), r.primaryActorName(), r.triggerText())));
    }

    @GetMapping(TraceabilityApiPaths.USE_CASE)
    @Operation(summary = "Get use case detail")
    public ApiResponse<UseCaseDetailResponse> getDetail(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId) {
        return ApiResponse.success(queryService.getDetail(projectId, useCaseId));
    }

    @PutMapping(TraceabilityApiPaths.USE_CASE)
    @Operation(summary = "Update a use case")
    public ApiResponse<UseCaseResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID useCaseId,
            @Valid @RequestBody UpdateUseCaseRequest r) {
        return ApiResponse.success(updateAction.execute(new UpdateUseCaseCommand(
                projectId, useCaseId, r.name(), r.goal(), r.primaryActorName(), r.triggerText(), r.status())));
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
}
