package com.company.scopery.modules.specpack.blockimport.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.specpack.blockimport.application.action.ApplyJsonImportAction;
import com.company.scopery.modules.specpack.blockimport.application.action.PreviewJsonImportAction;
import com.company.scopery.modules.specpack.blockimport.application.action.ValidateJsonImportAction;
import com.company.scopery.modules.specpack.blockimport.application.response.ImportPreviewResponse;
import com.company.scopery.modules.specpack.blockimport.domain.enums.BlockMergeMode;
import com.company.scopery.modules.specpack.shared.constant.SpecPackApiPaths;
import com.company.scopery.modules.specpack.shared.util.SpecPackEnumParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Spec Pack - Import")
@RestController
public class SpecPackImportController {

    private final ValidateJsonImportAction validateAction;
    private final PreviewJsonImportAction previewAction;
    private final ApplyJsonImportAction applyAction;

    public SpecPackImportController(ValidateJsonImportAction validateAction,
                                    PreviewJsonImportAction previewAction,
                                    ApplyJsonImportAction applyAction) {
        this.validateAction = validateAction;
        this.previewAction = previewAction;
        this.applyAction = applyAction;
    }

    @Operation(summary = "Validate a block JSON import payload (no DB changes)")
    @PostMapping(SpecPackApiPaths.IMPORTS_VALIDATE_JSON)
    public ApiResponse<ImportPreviewResponse> validateJson(
            @PathVariable UUID projectId,
            @PathVariable UUID packId,
            @RequestBody byte[] payload) {
        return ApiResponse.success(validateAction.execute(payload));
    }

    @Operation(summary = "Preview merge decisions for a block JSON import")
    @PostMapping(SpecPackApiPaths.IMPORTS_PREVIEW_JSON)
    public ApiResponse<ImportPreviewResponse> previewJson(
            @PathVariable UUID projectId,
            @PathVariable UUID packId,
            @RequestParam(defaultValue = "MERGE_BY_BLOCK_KEY") String mergeMode,
            @RequestBody byte[] payload) {
        BlockMergeMode mode = SpecPackEnumParser.parseRequired(BlockMergeMode.class, mergeMode, "mergeMode");
        return ApiResponse.success(previewAction.execute(payload, packId, mode));
    }

    @Operation(summary = "Apply a block JSON import to the Spec Pack")
    @PostMapping(SpecPackApiPaths.IMPORTS_APPLY_JSON)
    public ApiResponse<ImportPreviewResponse> applyJson(
            @PathVariable UUID projectId,
            @PathVariable UUID packId,
            @RequestParam(defaultValue = "MERGE_BY_BLOCK_KEY") String mergeMode,
            @RequestBody byte[] payload) {
        BlockMergeMode mode = SpecPackEnumParser.parseRequired(BlockMergeMode.class, mergeMode, "mergeMode");
        return ApiResponse.success(applyAction.execute(payload, packId, mode));
    }
}
