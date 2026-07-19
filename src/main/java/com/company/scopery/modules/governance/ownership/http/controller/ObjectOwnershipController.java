package com.company.scopery.modules.governance.ownership.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.governance.ownership.application.action.*;
import com.company.scopery.modules.governance.ownership.application.command.*;
import com.company.scopery.modules.governance.ownership.application.response.ObjectOwnershipResponse;
import com.company.scopery.modules.governance.ownership.application.service.ObjectOwnershipQueryService;
import com.company.scopery.modules.governance.ownership.http.request.AssignOwnershipRequest;
import com.company.scopery.modules.governance.shared.constant.GovernanceApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @RequestMapping(GovernanceApiPaths.PROJECT + "/ownership") @Tag(name = "Governance - Ownership")
public class ObjectOwnershipController {
    private final AssignOwnershipAction assign; private final RevokeOwnershipAction revoke;
    private final TransferOwnershipAction transfer; private final ObjectOwnershipQueryService query;
    public ObjectOwnershipController(AssignOwnershipAction assign, RevokeOwnershipAction revoke,
                                     TransferOwnershipAction transfer, ObjectOwnershipQueryService query) {
        this.assign = assign; this.revoke = revoke; this.transfer = transfer; this.query = query;
    }
    @PostMapping("/assign") @Operation(summary="Assign object ownership")
    public ApiResponse<ObjectOwnershipResponse> assign(@PathVariable UUID projectId, @Valid @RequestBody AssignOwnershipRequest r) {
        return ApiResponse.success(assign.execute(new AssignOwnershipCommand(projectId, r.objectTypeCode(), r.targetId(), r.ownerTargetType(), r.ownerTargetId())));
    }
    @PostMapping("/transfer") @Operation(summary="Transfer object ownership")
    public ApiResponse<ObjectOwnershipResponse> transfer(@PathVariable UUID projectId, @Valid @RequestBody AssignOwnershipRequest r) {
        return ApiResponse.success(transfer.execute(new TransferOwnershipCommand(projectId, r.objectTypeCode(), r.targetId(), r.ownerTargetType(), r.ownerTargetId(), null)));
    }
    @PostMapping("/revoke") @Operation(summary="Revoke object ownership")
    public ApiResponse<ObjectOwnershipResponse> revoke(@PathVariable UUID projectId, @RequestParam String objectTypeCode, @RequestParam UUID targetId) {
        return ApiResponse.success(revoke.execute(new RevokeOwnershipCommand(projectId, objectTypeCode, targetId)));
    }
    @GetMapping @Operation(summary="Get active ownership")
    public ApiResponse<ObjectOwnershipResponse> get(@PathVariable UUID projectId, @RequestParam String objectTypeCode, @RequestParam UUID targetId) {
        return ApiResponse.success(query.getActive(projectId, objectTypeCode, targetId));
    }
    @GetMapping("/list") @Operation(summary="List project ownerships")
    public ApiResponse<List<ObjectOwnershipResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.listByProject(projectId)); }
}
