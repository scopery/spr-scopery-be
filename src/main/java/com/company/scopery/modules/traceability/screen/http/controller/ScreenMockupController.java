package com.company.scopery.modules.traceability.screen.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screen.application.action.ConfirmScreenMockupUploadAction;
import com.company.scopery.modules.traceability.screen.application.action.RequestScreenMockupUploadAction;
import com.company.scopery.modules.traceability.screen.application.command.ConfirmScreenMockupUploadCommand;
import com.company.scopery.modules.traceability.screen.application.command.RequestScreenMockupUploadCommand;
import com.company.scopery.modules.traceability.screen.application.response.ScreenMockupConfirmResponse;
import com.company.scopery.modules.traceability.screen.application.response.ScreenMockupUploadResponse;
import com.company.scopery.modules.traceability.screen.http.request.ConfirmScreenMockupUploadRequest;
import com.company.scopery.modules.traceability.screen.http.request.RequestScreenMockupUploadRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_MOCKUP)
@Tag(name = "Traceability - Screen Mockup")
public class ScreenMockupController {

    private final RequestScreenMockupUploadAction requestUpload;
    private final ConfirmScreenMockupUploadAction confirmUpload;

    public ScreenMockupController(RequestScreenMockupUploadAction requestUpload,
                                   ConfirmScreenMockupUploadAction confirmUpload) {
        this.requestUpload = requestUpload;
        this.confirmUpload = confirmUpload;
    }

    @PostMapping("/upload-url")
    @Operation(summary = "Request presigned URL to upload screen mockup image")
    public ApiResponse<ScreenMockupUploadResponse> requestUploadUrl(@PathVariable UUID workspaceId,
                                                                     @PathVariable UUID screenId,
                                                                     @Valid @RequestBody RequestScreenMockupUploadRequest r) {
        return ApiResponse.success(requestUpload.execute(
                new RequestScreenMockupUploadCommand(workspaceId, null, screenId, r.contentType())));
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm screen mockup upload and save object key")
    public ApiResponse<ScreenMockupConfirmResponse> confirmUpload(@PathVariable UUID workspaceId,
                                                                   @PathVariable UUID screenId,
                                                                   @Valid @RequestBody ConfirmScreenMockupUploadRequest r) {
        return ApiResponse.success(confirmUpload.execute(
                new ConfirmScreenMockupUploadCommand(workspaceId, null, screenId, r.objectKey())));
    }
}
