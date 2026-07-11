package com.company.scopery.modules.workspace.access.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.workspace.access.application.response.WorkspaceAccessResponse;
import com.company.scopery.modules.workspace.access.application.response.WorkspaceEffectiveAccessEntry;
import com.company.scopery.modules.workspace.access.application.response.WorkspaceSubjectAccessResponse;
import com.company.scopery.modules.workspace.access.application.service.WorkspaceAccessQueryService;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Workspace - Effective Access")
public class WorkspaceAccessController {
    private final WorkspaceAccessQueryService accessQueryService;
    private final CurrentUserAuthorizationService currentUserService;

    public WorkspaceAccessController(WorkspaceAccessQueryService accessQueryService,
                                     CurrentUserAuthorizationService currentUserService) {
        this.accessQueryService = accessQueryService;
        this.currentUserService = currentUserService;
    }

    @GetMapping(WorkspaceApiPaths.ME_WORKSPACES)
    @Operation(summary = "List workspaces available through effective IAM access")
    public ApiResponse<List<WorkspaceEffectiveAccessEntry>> myWorkspaces() {
        UUID userId = currentUserService.resolveCurrentUser().id();
        return ApiResponse.success(accessQueryService.findWorkspacesForUser(userId));
    }

    @GetMapping(WorkspaceApiPaths.WORKSPACE_ACCESS)
    @Operation(summary = "List effective access subjects for a workspace")
    public ApiResponse<WorkspaceAccessResponse> workspaceAccess(@PathVariable UUID workspaceId) {
        return ApiResponse.success(accessQueryService.listWorkspaceAccess(workspaceId));
    }

    @GetMapping(WorkspaceApiPaths.WORKSPACE_ACCESS_EXPLAIN)
    @Operation(summary = "Explain effective workspace access for a subject")
    public ApiResponse<WorkspaceSubjectAccessResponse> explain(
            @PathVariable UUID workspaceId,
            @PathVariable String subjectType,
            @PathVariable UUID subjectId) {
        IamSubjectType parsedType = IamEnumParser.parseRequired(
                IamSubjectType.class, subjectType,
                IamErrorCatalog.INVALID_IAM_SUBJECT_TYPE.code(), "subjectType");
        return ApiResponse.success(accessQueryService.explainSubjectAccess(workspaceId, parsedType, subjectId));
    }
}
