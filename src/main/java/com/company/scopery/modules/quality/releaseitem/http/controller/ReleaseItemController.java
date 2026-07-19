package com.company.scopery.modules.quality.releaseitem.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.quality.releaseitem.application.action.ArchiveReleaseItemAction;
import com.company.scopery.modules.quality.releaseitem.application.action.CreateReleaseItemAction;
import com.company.scopery.modules.quality.releaseitem.application.command.CreateReleaseItemCommand;
import com.company.scopery.modules.quality.releaseitem.application.response.ReleaseItemResponse;
import com.company.scopery.modules.quality.releaseitem.application.service.ReleaseItemQueryService;
import com.company.scopery.modules.quality.releaseitem.http.request.CreateReleaseItemRequest;
import com.company.scopery.modules.quality.shared.constant.QualityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(QualityApiPaths.RELEASE_ITEMS)
@Tag(name = "Quality - Release Items")
public class ReleaseItemController {
    private final CreateReleaseItemAction create;
    private final ArchiveReleaseItemAction archive;
    private final ReleaseItemQueryService query;
    public ReleaseItemController(CreateReleaseItemAction create, ArchiveReleaseItemAction archive, ReleaseItemQueryService query) {
        this.create=create; this.archive=archive; this.query=query;
    }
    @PostMapping @Operation(summary = "Add release item")
    public ApiResponse<ReleaseItemResponse> create(@PathVariable UUID projectId, @PathVariable UUID releasePackageId,
                                                @Valid @RequestBody CreateReleaseItemRequest r) {
        return ApiResponse.success(create.execute(new CreateReleaseItemCommand(projectId, releasePackageId, r.targetType(), r.targetId(), r.required(), r.status(), r.notes())));
    }
    @GetMapping @Operation(summary = "List release items")
    public ApiResponse<List<ReleaseItemResponse>> list(@PathVariable UUID projectId, @PathVariable UUID releasePackageId) {
        return ApiResponse.success(query.listByRelease(projectId, releasePackageId));
    }
    @PatchMapping("/{itemId}/archive") @Operation(summary = "Archive release item")
    public ApiResponse<ReleaseItemResponse> archive(@PathVariable UUID projectId, @PathVariable UUID itemId) {
        return ApiResponse.success(archive.execute(projectId, itemId));
    }
}
