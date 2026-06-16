package com.company.scopery.modules.iam.right.api;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.right.application.IamRightApplicationService;
import com.company.scopery.modules.iam.right.application.query.SearchIamRightQuery;
import com.company.scopery.modules.iam.right.application.response.IamRightResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "IAM - Rights", description = "View the right catalog (read-only — seeded by system)")
@RestController
@RequestMapping(IamApiPaths.IAM_RIGHTS)
public class IamRightController {

    private final IamRightApplicationService service;

    public IamRightController(IamRightApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Get right by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamRightResponse>> getRight(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(service.getRight(id)));
    }

    @Operation(summary = "Search rights by keyword, module, or status")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamRightResponse>>> searchRights(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(
                service.searchRights(new SearchIamRightQuery(keyword, module, status, page, size)))));
    }
}
