package com.company.scopery.modules.governance.objecttype.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.governance.objecttype.application.response.GovernedObjectTypeResponse;
import com.company.scopery.modules.governance.objecttype.application.service.GovernedObjectTypeQueryService;
import com.company.scopery.modules.governance.shared.constant.GovernanceApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping(GovernanceApiPaths.OBJECT_TYPES) @Tag(name = "Governance - Object Types")
public class GovernedObjectTypeController {
    private final GovernedObjectTypeQueryService query;
    public GovernedObjectTypeController(GovernedObjectTypeQueryService query) { this.query = query; }

    @GetMapping @Operation(summary = "List all governed object types")
    public ApiResponse<List<GovernedObjectTypeResponse>> list() { return ApiResponse.success(query.listAll()); }

    @GetMapping("/{objectTypeCode}") @Operation(summary = "Get governed object type by code")
    public ApiResponse<GovernedObjectTypeResponse> get(@PathVariable String objectTypeCode) {
        return ApiResponse.success(query.getByCode(objectTypeCode));
    }
}
