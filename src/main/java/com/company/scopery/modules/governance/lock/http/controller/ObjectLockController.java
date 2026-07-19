package com.company.scopery.modules.governance.lock.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.governance.lock.application.action.*;
import com.company.scopery.modules.governance.lock.application.command.*;
import com.company.scopery.modules.governance.lock.application.response.ObjectLockResponse;
import com.company.scopery.modules.governance.lock.http.request.CreateObjectLockRequest;
import com.company.scopery.modules.governance.lock.http.request.FinalizeObjectRequest;
import com.company.scopery.modules.governance.shared.constant.GovernanceApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping(GovernanceApiPaths.PROJECT + "/locks") @Tag(name = "Governance - Locks")
public class ObjectLockController {
    private final CreateObjectLockAction create; private final ReleaseObjectLockAction release;
    private final FinalizeObjectAction finalize; private final UnfinalizeObjectAction unfinalize;
    public ObjectLockController(CreateObjectLockAction create, ReleaseObjectLockAction release,
                                FinalizeObjectAction finalize, UnfinalizeObjectAction unfinalize) {
        this.create=create; this.release=release; this.finalize=finalize; this.unfinalize=unfinalize;
    }
    @PostMapping @Operation(summary="Create object lock")
    public ApiResponse<ObjectLockResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateObjectLockRequest r) {
        return ApiResponse.success(create.execute(new CreateObjectLockCommand(projectId, r.objectTypeCode(), r.targetId(), r.lockType(), r.reason())));
    }
    @PostMapping("/{lockId}/release") @Operation(summary="Release object lock")
    public ApiResponse<ObjectLockResponse> release(@PathVariable UUID projectId, @PathVariable UUID lockId) {
        return ApiResponse.success(release.execute(new ReleaseObjectLockCommand(projectId, lockId)));
    }
    @PostMapping("/{objectTypeCode}/{targetId}/finalize") @Operation(summary="Finalize object (create FINALIZED lock)")
    public ApiResponse<ObjectLockResponse> finalizeObject(@PathVariable UUID projectId, @PathVariable String objectTypeCode,
            @PathVariable UUID targetId, @RequestBody(required = false) FinalizeObjectRequest r) {
        String reason = r != null ? r.reason() : null;
        return ApiResponse.success(finalize.execute(new FinalizeObjectCommand(projectId, objectTypeCode, targetId, reason)));
    }
    @PostMapping("/{objectTypeCode}/{targetId}/unfinalize") @Operation(summary="Unfinalize object (release FINALIZED lock)")
    public ApiResponse<ObjectLockResponse> unfinalizeObject(@PathVariable UUID projectId, @PathVariable String objectTypeCode,
            @PathVariable UUID targetId, @RequestBody(required = false) FinalizeObjectRequest r) {
        String reason = r != null ? r.reason() : null;
        return ApiResponse.success(unfinalize.execute(new UnfinalizeObjectCommand(projectId, objectTypeCode, targetId, reason)));
    }
}
