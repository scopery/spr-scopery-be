package com.company.scopery.modules.ratecard.membercostrole.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.ratecard.membercostrole.application.action.*;
import com.company.scopery.modules.ratecard.membercostrole.application.command.*;
import com.company.scopery.modules.ratecard.membercostrole.application.query.SearchMemberCostRoleQuery;
import com.company.scopery.modules.ratecard.membercostrole.application.response.MemberCostRoleResponse;
import com.company.scopery.modules.ratecard.membercostrole.application.service.MemberCostRoleQueryService;
import com.company.scopery.modules.ratecard.membercostrole.http.request.AssignMemberCostRoleRequest;
import com.company.scopery.modules.ratecard.membercostrole.http.request.UpdateMemberCostRoleRequest;
import com.company.scopery.modules.ratecard.shared.constant.RateCardApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(RateCardApiPaths.MEMBER_COST_ROLES)
@Tag(name = "Rate Card - Member Cost Roles")
public class MemberCostRoleController {
    private final MemberCostRoleQueryService queryService;
    private final AssignMemberCostRoleAction assignAction;
    private final UpdateMemberCostRoleAction updateAction;
    private final ArchiveMemberCostRoleAction archiveAction;

    public MemberCostRoleController(MemberCostRoleQueryService queryService, AssignMemberCostRoleAction assignAction,
                                    UpdateMemberCostRoleAction updateAction, ArchiveMemberCostRoleAction archiveAction) {
        this.queryService = queryService; this.assignAction = assignAction;
        this.updateAction = updateAction; this.archiveAction = archiveAction;
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Assign member cost role")
    public ApiResponse<MemberCostRoleResponse> assign(@Valid @RequestBody AssignMemberCostRoleRequest request) {
        return ApiResponse.success(assignAction.execute(new AssignMemberCostRoleCommand(
                request.workspaceId(), request.workspaceMemberId(), request.costRoleId(),
                request.isDefault(), request.effectiveFrom(), request.effectiveTo())));
    }

    @GetMapping("/{assignmentId}") public ApiResponse<MemberCostRoleResponse> get(@PathVariable UUID assignmentId) {
        return ApiResponse.success(queryService.get(assignmentId));
    }

    @GetMapping public ApiResponse<PageResponse<MemberCostRoleResponse>> search(
            @RequestParam UUID workspaceId, @RequestParam(required = false) UUID workspaceMemberId,
            @RequestParam(required = false) UUID userId, @RequestParam(required = false) UUID costRoleId,
            @RequestParam(required = false) String status, @RequestParam(required = false) LocalDate effectiveDate,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.fromDomain(queryService.search(new SearchMemberCostRoleQuery(
                workspaceId, workspaceMemberId, userId, costRoleId, status, effectiveDate, page, size))));
    }

    @PutMapping("/{assignmentId}") public ApiResponse<MemberCostRoleResponse> update(
            @PathVariable UUID assignmentId, @Valid @RequestBody UpdateMemberCostRoleRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateMemberCostRoleCommand(
                assignmentId, request.costRoleId(), request.isDefault(), request.effectiveFrom(), request.effectiveTo())));
    }

    @PatchMapping("/{assignmentId}/archive") public ApiResponse<MemberCostRoleResponse> archive(@PathVariable UUID assignmentId) {
        return ApiResponse.success(archiveAction.execute(new ArchiveMemberCostRoleCommand(assignmentId)));
    }
}
