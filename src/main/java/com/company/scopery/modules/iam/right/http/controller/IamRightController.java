package com.company.scopery.modules.iam.right.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.iam.shared.constant.IamApiPaths;
import com.company.scopery.modules.iam.right.application.query.SearchIamRightQuery;
import com.company.scopery.modules.iam.right.application.response.IamRightResponse;
import com.company.scopery.modules.iam.right.application.service.IamRightQueryService;
import com.company.scopery.modules.iam.right.http.request.SearchIamRightRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "IAM - Rights", description = "View the right catalog (read-only — seeded by system)")
@RestController
@RequestMapping(IamApiPaths.IAM_RIGHTS)
public class IamRightController {

    private final IamRightQueryService queryService;

    public IamRightController(IamRightQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Get right by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IamRightResponse>> getRight(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(queryService.getRight(id)));
    }

    @Operation(summary = "Search rights by keyword, module, or status")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<IamRightResponse>>> searchRights(
            @ModelAttribute SearchIamRightRequest request) {
        PageResult<IamRightResponse> result = queryService.searchRights(new SearchIamRightQuery(
                request.keyword(), request.module(), request.status(),
                request.page(), request.size()));
        return ResponseEntity.ok(ApiResponse.success(PageResponse.fromDomain(result)));
    }
}
