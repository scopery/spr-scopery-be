package com.company.scopery.modules.trust.datasubject.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.trust.datasubject.application.action.RebuildDataSubjectIndexAction;
import com.company.scopery.modules.trust.datasubject.application.response.DataSubjectIndexResponse;
import com.company.scopery.modules.trust.datasubject.application.service.DataSubjectIndexQueryService;
import com.company.scopery.modules.trust.shared.constant.TrustApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Trust / Compliance")
public class DataSubjectIndexController {
    private final RebuildDataSubjectIndexAction rebuildAction;
    private final DataSubjectIndexQueryService queryService;
    public DataSubjectIndexController(RebuildDataSubjectIndexAction rebuildAction, DataSubjectIndexQueryService queryService) {
        this.rebuildAction = rebuildAction; this.queryService = queryService;
    }
    @PostMapping(TrustApiPaths.DATA_SUBJECTS_REBUILD) @Operation(summary = "Rebuild data subject index")
    public ApiResponse<DataSubjectIndexResponse> rebuild(@PathVariable UUID workspaceId) {
        return ApiResponse.success(rebuildAction.execute(workspaceId));
    }
    @GetMapping(TrustApiPaths.DATA_SUBJECTS) @Operation(summary = "List data subject index entries")
    public ApiResponse<List<DataSubjectIndexResponse>> list(@PathVariable UUID workspaceId) {
        return ApiResponse.success(queryService.listByWorkspace(workspaceId));
    }
    @GetMapping(TrustApiPaths.DATA_SUBJECTS + "/{subjectIndexId}") @Operation(summary = "Get data subject index entry")
    public ApiResponse<DataSubjectIndexResponse> getById(@PathVariable UUID workspaceId, @PathVariable UUID subjectIndexId) {
        return ApiResponse.success(queryService.getById(workspaceId, subjectIndexId));
    }
}
