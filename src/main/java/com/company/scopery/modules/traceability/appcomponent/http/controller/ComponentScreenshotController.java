package com.company.scopery.modules.traceability.appcomponent.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.appcomponent.application.action.ConfirmComponentScreenshotUploadAction;
import com.company.scopery.modules.traceability.appcomponent.application.action.RequestComponentScreenshotUploadAction;
import com.company.scopery.modules.traceability.appcomponent.application.command.ConfirmComponentScreenshotUploadCommand;
import com.company.scopery.modules.traceability.appcomponent.application.command.RequestComponentScreenshotUploadCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.ComponentScreenshotConfirmResponse;
import com.company.scopery.modules.traceability.appcomponent.application.response.ComponentScreenshotUploadResponse;
import com.company.scopery.modules.traceability.appcomponent.http.request.ConfirmComponentScreenshotUploadRequest;
import com.company.scopery.modules.traceability.appcomponent.http.request.RequestComponentScreenshotUploadRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.COMPONENT_SCREENSHOT)
@Tag(name = "Traceability - Component Screenshot")
public class ComponentScreenshotController {

    private final RequestComponentScreenshotUploadAction requestUpload;
    private final ConfirmComponentScreenshotUploadAction confirmUpload;

    public ComponentScreenshotController(RequestComponentScreenshotUploadAction requestUpload,
                                          ConfirmComponentScreenshotUploadAction confirmUpload) {
        this.requestUpload = requestUpload;
        this.confirmUpload = confirmUpload;
    }

    @PostMapping("/upload-url")
    @Operation(summary = "Request presigned URL to upload component screenshot image")
    public ApiResponse<ComponentScreenshotUploadResponse> requestUploadUrl(@PathVariable UUID workspaceId,
                                                                            @PathVariable UUID componentId,
                                                                            @Valid @RequestBody RequestComponentScreenshotUploadRequest r) {
        return ApiResponse.success(requestUpload.execute(
                new RequestComponentScreenshotUploadCommand(workspaceId, null, componentId, r.contentType())));
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm component screenshot upload and save object key")
    public ApiResponse<ComponentScreenshotConfirmResponse> confirmUpload(@PathVariable UUID workspaceId,
                                                                          @PathVariable UUID componentId,
                                                                          @Valid @RequestBody ConfirmComponentScreenshotUploadRequest r) {
        return ApiResponse.success(confirmUpload.execute(
                new ConfirmComponentScreenshotUploadCommand(workspaceId, null, componentId, r.objectKey())));
    }
}
