package com.company.scopery.modules.quality.defectlink.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.defectlink.application.action.ArchiveDefectLinkAction;
import com.company.scopery.modules.quality.defectlink.application.action.CreateDefectLinkAction;
import com.company.scopery.modules.quality.defectlink.application.command.CreateDefectLinkCommand;
import com.company.scopery.modules.quality.defectlink.application.response.DefectLinkResponse;
import com.company.scopery.modules.quality.defectlink.application.service.DefectLinkQueryService;
import com.company.scopery.modules.quality.defectlink.http.request.CreateDefectLinkRequest;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(QualityApiPaths.DEFECT_LINKS)
@Tag(name = "Quality - Defect Links")
public class DefectLinkController {
    private final CreateDefectLinkAction create;
    private final ArchiveDefectLinkAction archive;
    private final DefectLinkQueryService query;
    public DefectLinkController(CreateDefectLinkAction create, ArchiveDefectLinkAction archive, DefectLinkQueryService query) {
        this.create=create; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary = "Add defect link")
    public ApiResponse<DefectLinkResponse> create(@PathVariable UUID projectId, @PathVariable UUID defectId,
                                                @Valid @RequestBody CreateDefectLinkRequest r) {
        return ApiResponse.success(create.execute(new CreateDefectLinkCommand(projectId, defectId, r.targetType(), r.targetId(), r.linkType())));
    }
    @GetMapping @Operation(summary = "List defect links")
    public ApiResponse<List<DefectLinkResponse>> list(@PathVariable UUID projectId, @PathVariable UUID defectId) {
        return ApiResponse.success(query.listByDefect(projectId, defectId));
    }
    @PatchMapping("/{linkId}/archive") @Operation(summary = "Archive defect link")
    public ApiResponse<DefectLinkResponse> archive(@PathVariable UUID projectId, @PathVariable UUID linkId) {
        return ApiResponse.success(archive.execute(projectId, linkId));
    }
}
